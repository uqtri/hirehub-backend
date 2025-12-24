package org.example.hirehub.controller;

import org.example.hirehub.dto.chatbot.ChatRequestDTO;
import org.example.hirehub.dto.chatbot.ChatbotRequestDTO;
import org.example.hirehub.dto.job.JobDetailDTO;
import org.example.hirehub.service.ChatbotService;
import org.example.hirehub.service.GeminiChatService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;
    private final GeminiChatService geminiChatService;

    public ChatbotController(ChatbotService chatbotService, GeminiChatService geminiChatService) {
        this.chatbotService = chatbotService;
        this.geminiChatService = geminiChatService;
    }

    @PostMapping("/jobs")
    public ResponseEntity<Map<String, ?>> findJobs(@RequestBody ChatbotRequestDTO request) {
        List<JobDetailDTO> jobs = chatbotService.findJob(request);
        return ResponseEntity.ok(Map.of("data", jobs));
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(@RequestBody ChatRequestDTO request) {
        return geminiChatService
                .streamChat(
                        request.getMessage(),
                        request.getHistory() != null ? request.getHistory() : Collections.emptyList())
                .map(token -> ServerSentEvent.<String>builder()
                        .data(token.replace("\n", "\\n"))
                        .build());
    }

    @PostMapping(value = "/chat/stream/file", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChatWithFile(
            @RequestParam String message,
            @RequestParam(required = false, defaultValue = "[]") String history,
            @RequestPart("file") MultipartFile file) {

        List<org.example.hirehub.dto.chatbot.MessageDTO> historyList = Collections.emptyList();

        return geminiChatService
                .streamChatWithFile(message, historyList, file)
                .map(token -> ServerSentEvent.<String>builder()
                        .data(token.replace("\n", "\\n"))
                        .build());
    }

    @PostMapping("/resumes")
    public ResponseEntity<Map<String, ?>> analyzeResume(@ModelAttribute ChatbotRequestDTO request) {
        String response = chatbotService.analyzeResume(request);
        return ResponseEntity.ok(Map.of("data", response));
    }
}
