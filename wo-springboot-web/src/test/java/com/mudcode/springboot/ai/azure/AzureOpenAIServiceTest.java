package com.mudcode.springboot.ai.azure;

import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatMessageContentItem;
import com.azure.ai.openai.models.ChatMessageImageContentItem;
import com.azure.ai.openai.models.ChatMessageImageUrl;
import com.azure.ai.openai.models.ChatMessageTextContentItem;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.azure.ai.openai.models.ChatResponseMessage;
import com.mudcode.springboot.ApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

class AzureOpenAIServiceTest extends ApplicationTest {

    @Autowired
    private AzureOpenAIService azureOpenAIService;

    @Test
    void chat() {
        String answer = this.azureOpenAIService.chat("1", "鲁迅为什么暴打周树人？");
        logger.info("{}", answer);
    }

    @Test
    void img() throws IOException {
        try (InputStream is = this.getClass().getResourceAsStream("/medicine.jpeg")) {
            byte[] content = Objects.requireNonNull(is).readAllBytes();
            String answer = this.azureOpenAIService.chat("1", content);
            logger.info("{}", answer);
        }
    }


    @Test
    void raw() throws IOException {
        List<ChatRequestMessage> messages = new ArrayList<>();
        messages.add(new ChatRequestSystemMessage(
                "你是人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一切涉及恐怖主义，种族歧视，黄色暴力等问题的回答。"
        ));
        List<ChatMessageContentItem> contentItems = new ArrayList<>();
        contentItems.add(new ChatMessageTextContentItem(
                "请仔细解读这张图片，并对解读的内容进行整理，要确保逻辑清晰、内容准确。"
        ));

        String base64Img = null;
        try (InputStream is = this.getClass().getResourceAsStream("/medicine.jpeg")) {
            byte[] content = Objects.requireNonNull(is).readAllBytes();
            base64Img = Base64.getEncoder().encodeToString(content);
        }
        String imgUrl = "data:image/jpeg;base64," + base64Img;
        contentItems.add(new ChatMessageImageContentItem(new ChatMessageImageUrl(imgUrl)));
        ChatRequestUserMessage userMessage = new ChatRequestUserMessage(contentItems);
        messages.add(userMessage);

        ChatCompletionsOptions chatCompletionsOptions = new ChatCompletionsOptions(new ArrayList<>(messages));
        chatCompletionsOptions.setTemperature(0.75D);
        chatCompletionsOptions.setMaxTokens(16000);
        logger.info("{}", chatCompletionsOptions.toJsonString());
        ChatCompletions chatCompletions = this.azureOpenAIService.getClient().getChatCompletions("gpt-4o-mini", chatCompletionsOptions);
        logger.info(chatCompletions.toJsonString());
        ChatResponseMessage responseMessage = chatCompletions.getChoices().get(0).getMessage();
        logger.info(responseMessage.getContent());
    }

}
