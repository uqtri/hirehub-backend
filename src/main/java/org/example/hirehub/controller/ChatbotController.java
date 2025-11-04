package org.example.hirehub.controller;

import org.example.hirehub.dto.chatbot.ChatbotRequestDTO;
import org.example.hirehub.dto.job.JobDetailDTO;
import org.example.hirehub.dto.job.JobSummaryDTO;
import org.example.hirehub.service.ChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/jobs")
    public ResponseEntity<Map<String, ?>> findJobs(@RequestBody ChatbotRequestDTO request) {

        List<JobDetailDTO> jobs = chatbotService.findJob(request);

        return ResponseEntity.ok(Map.of("data", jobs));
    }
}
