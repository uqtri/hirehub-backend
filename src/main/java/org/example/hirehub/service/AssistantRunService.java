package org.example.hirehub.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class AssistantRunService {

    private final WebClient client;
    private final AssistantService assistantService;

    public AssistantRunService(WebClient openAiWebClient, AssistantService assistantService) {
        this.client = openAiWebClient;
        this.assistantService = assistantService;
    }

    public String createThread() {
        Map resp = client.post()
                .uri("/threads")
                .header("OpenAI-Beta","assistants=v2")
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        if (resp == null || resp.get("id") == null) throw new RuntimeException("Create thread failed");
        return resp.get("id").toString();
    }

    public String postMessageWithFile(String threadId, String fileId, String userPrompt) {
        Map<String, Object> message = Map.of(
                "role", "user",
                "content", List.of(Map.of("type", "text", "text", userPrompt)),
                "attachments", List.of(
                        Map.of(
                                "file_id", fileId,
                                // tools like file_search may be needed depending on assistant config
                                "tools", List.of(Map.of("type", "file_search"))
                        )
                )
        );

        client.post()
                .uri("/threads/{id}/messages", threadId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(message)
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        return "ok";
    }

    public String runAssistant(String threadId) {
        String assistantId = assistantService.getOrCreateAssistant();
        Map<String, Object> body = Map.of(
                "assistant_id", assistantId
        );

        Map resp = client.post()
                .uri("/threads/{id}/runs", threadId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (resp == null || resp.get("id") == null) throw new RuntimeException("Run assistant failed");
        return resp.get("id").toString();
    }

    public Map pollRunUntilDone(String threadId, String runId, long timeoutMillis) {
        long start = System.currentTimeMillis();
        int sleepTimeMillis = 300;
        int maxSleepTime = 2000;

        while (true) {
            Map runStatus = client.get()
                    .uri("/threads/{t}/runs/{r}", threadId, runId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (runStatus != null) {
                Object statusObj = runStatus.get("status");
                String status = statusObj == null ? "" : statusObj.toString();
                if ("completed".equalsIgnoreCase(status) || "succeeded".equalsIgnoreCase(status)) {
                    return runStatus;
                }
                if ("failed".equalsIgnoreCase(status)) {
                    throw new RuntimeException("Assistant run failed: " + runStatus);
                }
            }

            if (System.currentTimeMillis() - start > timeoutMillis) {
                throw new RuntimeException("Timeout waiting for assistant run to complete");
            }

            try {
                Thread.sleep(sleepTimeMillis);
                sleepTimeMillis = sleepTimeMillis * 2;
                if (sleepTimeMillis > maxSleepTime) {
                    sleepTimeMillis = maxSleepTime;
                }
            } catch (InterruptedException ignored) {}
        }
    }

    public List<Map> fetchMessages(String threadId) {
        Map resp = client.get()
                .uri("/threads/{id}/messages", threadId)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (resp == null) return Collections.emptyList();
        Object data = resp.get("data"); // typical shape: { data: [ ... ] }
        if (!(data instanceof List)) return Collections.emptyList();
        return (List<Map>) data;
    }

    public String extractAnswerFromMessages(List<Map> messages) {
        StringBuilder sb = new StringBuilder();
        Map m = messages.get(0);
        Object role = m.get("role");
        Object content = m.get("content");
        if (content instanceof List) {
            List parts = (List) content;
            for (Object p : parts) {
                if (p instanceof Map) {
                    Map text = (Map)((Map) p).get("text");
                    if (text != null) sb.append(text.get("value").toString()).append("\n");
                }
            }
        } else if (content instanceof String) {
            sb.append(content).append("\n");
        }
        return sb.toString().trim();
    }
    }
