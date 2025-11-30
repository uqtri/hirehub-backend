package org.example.hirehub.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class AssistantService {

    private final WebClient client;

    @Value("${app.assistant.id}")
    private String assistantIdFromConfig;

    private volatile String assistantId;

    public AssistantService(WebClient openAiWebClient) {
        this.client = openAiWebClient;
    }

    @PostConstruct
    public void init() {
        this.assistantId = assistantIdFromConfig == null ? null : assistantIdFromConfig.trim();
    }

    public String getOrCreateAssistant() {
        if (assistantId != null && !assistantId.isEmpty()) return assistantId;

        Map<String, Object> body = Map.of(
                "model", "gpt-4.1",   // hoặc model mong muốn
                "name", "CV Analyzer",
                "instructions", "Bạn là chuyên gia phân tích CV. Đọc file đính kèm, tóm tắt, đánh giá kỹ năng, kinh nghiệm, và đề xuất cải thiện."
        );

        Map resp = client.post()
                .uri("/assistants")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (resp == null || resp.get("id") == null) {
            throw new RuntimeException("Create assistant failed");
        }

        this.assistantId = resp.get("id").toString();

        return assistantId;
    }
}
