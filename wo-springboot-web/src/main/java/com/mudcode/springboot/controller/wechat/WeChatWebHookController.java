package com.mudcode.springboot.controller.wechat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mudcode.springboot.common.util.HttpClientUtil;
import com.mudcode.springboot.common.util.JsonUtil;
import com.mudcode.springboot.freemarker.FreeMarkerService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;

@RestController
@RequestMapping("wechat-webhook")
public class WeChatWebHookController {

    private static final Logger logger = LoggerFactory.getLogger(WeChatWebHookController.class);

    @Value("${wechat.webhook.url:https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=}")
    private String webhookUrl;

    @Autowired
    private FreeMarkerService freeMarkerService;

    private HttpClientUtil httpClientUtil;

    @PostConstruct
    public void init() throws Exception {
        this.httpClientUtil = new HttpClientUtil();
        this.httpClientUtil.init();
    }

    @PreDestroy
    public void destroy() {
        this.httpClientUtil.close();
    }

    @RequestMapping(value = {"/md/{token:.+}"})
    public ResponseEntity<String> redirectMessage(@RequestBody String body, @PathVariable String token) {
        logger.info("receive message: {}", body);
        String msg = "OK";
        try {
            Map<String, Object> map = JsonUtil.toObject(body, new TypeReference<>() {
            });
            String content = freeMarkerService.process("prom-alert.ftl", map);
            TextMessageBody messageBody = new TextMessageBody(new TextMessageContent(content));
            String response = httpClientUtil.postJsonBody(webhookUrl + token, null, messageBody.toJson());
            logger.info("receive wechat webhook response: {}", response);
            return ResponseEntity.ok().body(msg);
        } catch (Exception e) {
            msg = e.getMessage();
        }
        return ResponseEntity.internalServerError().body(msg);
    }

    @Data
    public static final class TextMessageBody {

        private final String msgtype = "markdown";

        private TextMessageContent markdown;

        public TextMessageBody(TextMessageContent markdown) {
            this.markdown = markdown;
        }

        public String toJson() {
            return JsonUtil.toJson(this);
        }

    }

    @Data
    public static final class TextMessageContent {

        private String content;

        public TextMessageContent(String content) {
            this.content = content;
        }

    }

}
