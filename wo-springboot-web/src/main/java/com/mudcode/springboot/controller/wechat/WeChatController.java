package com.mudcode.springboot.controller.wechat;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mudcode.springboot.LogUtil;
import com.mudcode.springboot.ai.azure.AzureOpenAIService;
import com.mudcode.springboot.common.util.XmlUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

@RequestMapping("/wechat")
@RestController
public class WeChatController {
    private static final Logger logger = LoggerFactory.getLogger(WeChatController.class);

    @Value("${wechat.token:K7WWQUUvhW2lIdaW6b3ezksVGTsT}")
    private String token;

    @Value("${wechat.EncodingAESKey:nY7EOxZR6Qw58OXZCuYwlYGKi5VeKxDZCPxi4ZBayvg}")
    private String EncodingAESKey;

    @Autowired
    private AzureOpenAIService azureOpenAIService;

    private Cache<Long, DeferredResult<ResponseEntity<String>>> watchRequests;
    private Cache<String, String> lastChatMsgCache;

    @PostConstruct
    public void init() {
        this.watchRequests = CacheBuilder.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(30))
                .maximumSize(1000)
                .build();
        this.lastChatMsgCache = CacheBuilder.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(10))
                .maximumSize(1000)
                .build();
    }

    @PreDestroy
    public void destroy() {
        this.watchRequests.asMap()
                .forEach((id, deferredResult) -> deferredResult.setResult(ResponseEntity.status(HttpStatus.GONE).build()));
    }

    @RequestMapping
    public DeferredResult<ResponseEntity<String>> wechat(
            @RequestParam(name = "signature", required = false) String signature,
            @RequestParam(name = "timestamp", required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce,
            @RequestParam(name = "echostr", required = false) String echostr,
            @RequestBody(required = false) String msgReq
    ) {
        LogUtil.access("WeChatMsgReq: {}", msgReq);

        DeferredResult<ResponseEntity<String>> timelyResult = new DeferredResult<>();

        boolean valid = validate(signature, timestamp, nonce);
        if (!valid) {
            timelyResult.setResult(ResponseEntity.badRequest().body("invalid token."));
            return timelyResult;
        }

        if (StringUtils.isNotEmpty(echostr)) {
            timelyResult.setResult(ResponseEntity.ok(echostr));
            return timelyResult;
        }

        ChatMsgReq chatMsg = XmlUtil.fromXml(msgReq, ChatMsgReq.class);
        if (!chatMsg.validMsgType()) {
            timelyResult.setResult(ResponseEntity.ok(XmlUtil.toXml(new ChatMsgResp(chatMsg, "暂不支持此消息类型"))));
            return timelyResult;
        }

        long msgId = chatMsg.getMsgId();
        DeferredResult<ResponseEntity<String>> cached = watchRequests.getIfPresent(msgId);
        if (cached != null) {
            return cached;
        }

        String userName = chatMsg.getFromUserName();
        DeferredResult<ResponseEntity<String>> deferredResult = new DeferredResult<>(
                Duration.ofSeconds(12).toMillis(),
                ResponseEntity.ok(XmlUtil.toXml(new ChatMsgResp(chatMsg, "思考中...")))
        );

        deferredResult.onCompletion(() -> watchRequests.invalidate(msgId));
        deferredResult.onTimeout(() -> watchRequests.invalidate(msgId));

        watchRequests.put(msgId, deferredResult);

        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        ForkJoinPool.commonPool().submit(() -> {
            try {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }

                String question = chatMsg.getContent();
                if ("1".equals(question)) {
                    while (!deferredResult.isSetOrExpired()) {
                        String lastChatMsgResp = this.lastChatMsgCache.getIfPresent(userName);
                        if (lastChatMsgResp != null) {
                            buildResult(deferredResult, lastChatMsgResp);
                            return;
                        }
                    }
                    return;
                } else if ("0".equals(question)) {
                    azureOpenAIService.reset(userName);
                    this.lastChatMsgCache.invalidate(userName);
                    buildResult(deferredResult, new ChatMsgResp(chatMsg, "已重置"));
                    return;
                }

                String imgUrl = chatMsg.getPicUrl();
                String answer = "思考中...";
                if (StringUtils.isNotEmpty(question)) {
                    answer = azureOpenAIService.chat(userName, question);
                } else if (StringUtils.isNotEmpty(imgUrl)) {
                    answer = azureOpenAIService.chat(userName, new URL(imgUrl));
                }

                String chatMsgResp = XmlUtil.toXml(new ChatMsgResp(chatMsg, answer));

                this.lastChatMsgCache.put(userName, chatMsgResp);
                buildResult(deferredResult, chatMsgResp);
            } catch (Exception e) {
                buildResult(deferredResult, new ChatMsgResp(chatMsg, "暂时无法回答您的问题"));
                logger.error(e.getMessage(), e);
            } finally {
                MDC.clear();
            }
        });

        return deferredResult;
    }

    private boolean validate(String signature, String timestamp, String nonce) {
        List<String> list = Arrays.asList(token, timestamp, nonce);
        list.sort(String::compareTo);
        String sha1 = DigestUtils.sha1Hex(String.join("", list));
        return sha1.equals(signature);
    }

    private void buildResult(DeferredResult<ResponseEntity<String>> deferredResult, String chatMsg) {
        LogUtil.access("WeChatMsgResp: {}", chatMsg);
        deferredResult.setResult(ResponseEntity.ok(chatMsg));
    }

    private void buildResult(DeferredResult<ResponseEntity<String>> deferredResult, ChatMsgResp chatMsg) {
        String chatMsgResp = XmlUtil.toXml(chatMsg);
        buildResult(deferredResult, chatMsgResp);
    }
}
