package com.mudcode.springboot.ai.azure;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.OpenAIServiceVersion;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatMessageContentItem;
import com.azure.ai.openai.models.ChatMessageImageContentItem;
import com.azure.ai.openai.models.ChatMessageImageUrl;
import com.azure.ai.openai.models.ChatMessageTextContentItem;
import com.azure.ai.openai.models.ChatRequestAssistantMessage;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.azure.ai.openai.models.ChatResponseMessage;
import com.azure.core.credential.KeyCredential;
import com.azure.core.http.netty.NettyAsyncHttpClientBuilder;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class AzureOpenAIService {

    private static final Logger logger = LoggerFactory.getLogger(AzureOpenAIService.class);
    private static final String system_prompt = """
            你是人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一切涉及恐怖主义，种族歧视，黄色暴力等问题的回答。
            """;
    private static final ChatRequestSystemMessage system_prompt_message = new ChatRequestSystemMessage(system_prompt);
    private final Duration timeout = Duration.ofSeconds(180);
    @Value("${azure.openai.key:ea814f6513c74bcfa6259f1bf7488e57}")
    private String azure_openai_key;
    @Value("${azure.openai.endpoint:https://llm-caller.openai.azure.com}")
    private String azure_openai_endpoint;
    @Value("${azure.openai.deployment:gpt-4o}")
    private String azure_deployment;
    @Value("${azure.openai.chat-history-size:10}")
    private int chat_history_size;
    @Getter
    private OpenAIClient client;
    private Cache<String, List<ChatRequestMessage>> messageCache;

    @PostConstruct
    public void init() {
        OpenAIClientBuilder builder = new OpenAIClientBuilder();
        builder.serviceVersion(OpenAIServiceVersion.V2024_06_01);
        builder.credential(new KeyCredential(azure_openai_key));
        builder.endpoint(azure_openai_endpoint);
        builder.httpClient(
                new NettyAsyncHttpClientBuilder()
                        .connectTimeout(timeout)
                        .responseTimeout(timeout)
                        .readTimeout(timeout)
                        .writeTimeout(timeout)
                        .build()
        );

        this.client = builder.buildClient();

        this.messageCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(Duration.ofMinutes(60))
                .build();
    }

    public void reset(String session) {
        this.messageCache.invalidate(session);
    }

    public String chat(String session, String question) {
        List<ChatRequestMessage> messages = catchUserSession(session);
        messages.add(new ChatRequestUserMessage(question));
        return catchChatResp(messages);
    }

    private List<ChatRequestMessage> catchUserSession(String session) {
        List<ChatRequestMessage> messages = this.messageCache.getIfPresent(session);
        if (messages == null) {
            messages = new CopyOnWriteArrayList<>();
            messages.add(system_prompt_message);
            this.messageCache.put(session, messages);
        }
        clearChatHistory(messages);
        return messages;
    }

    private String catchChatResp(List<ChatRequestMessage> messages) {
        long t0 = System.currentTimeMillis();
        ChatCompletionsOptions chatCompletionsOptions = new ChatCompletionsOptions(new ArrayList<>(messages));
        chatCompletionsOptions.setTemperature(0.75D);
        chatCompletionsOptions.setMaxTokens(1024);
        ChatCompletions chatCompletions = this.client.getChatCompletions(azure_deployment, chatCompletionsOptions);
        ChatResponseMessage responseMessage = chatCompletions.getChoices().get(0).getMessage();
        messages.add(new ChatRequestAssistantMessage(responseMessage.getContent()));
        long t1 = System.currentTimeMillis();
        try {
            logger.info("ChatCompletionsOptions: {}, ChatCompletions: {}, Duration: {}", chatCompletionsOptions.toJsonString(), chatCompletions.toJsonString(), Duration.ofMillis(t1 - t0));
        } catch (IOException e) {
            logger.error("toJsonString error: {}", e.getMessage());
        }
        return responseMessage.getContent();
    }

    public String chat(String session, URL url) throws IOException {
        try (InputStream is = url.openStream()) {
            byte[] content = is.readAllBytes();
            return chat(session, content);
        }
    }

    public String chat(String session, byte[] jpegImageBytes) {
        List<ChatRequestMessage> messages = catchUserSession(session);

        List<ChatMessageContentItem> contentItems = new ArrayList<>();
        contentItems.add(new ChatMessageTextContentItem("首先请描述这张图片，然后对图片的信息进行整理总结。"));

        String base64Img = Base64.getEncoder().encodeToString(jpegImageBytes);
        String imgUrl = "data:image/jpeg;base64," + base64Img;
        contentItems.add(new ChatMessageImageContentItem(new ChatMessageImageUrl(imgUrl)));
        ChatRequestUserMessage userMessage = new ChatRequestUserMessage(contentItems);
        messages.add(userMessage);

        return catchChatResp(messages);
    }

    private void clearChatHistory(List<ChatRequestMessage> messages) {
        int size = messages.size();
        if (size <= chat_history_size + 1) {
            return;
        }
        List<ChatRequestMessage> changeMessages = new CopyOnWriteArrayList<>();
        changeMessages.add(system_prompt_message);
        changeMessages.addAll(messages.subList(size - chat_history_size, size));
        messages.clear();
        messages.addAll(changeMessages);
    }
}
