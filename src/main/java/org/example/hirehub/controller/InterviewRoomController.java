package org.example.hirehub.controller;

import org.example.hirehub.dto.interview.*;
import org.example.hirehub.service.InterviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interview-rooms")
public class InterviewRoomController {
    
    private final InterviewService interviewService;
    
    public InterviewRoomController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createInterviewRoom(
            @RequestBody CreateInterviewRoomDTO dto
    ) {
        InterviewRoomDTO room = interviewService.createInterviewRoom(dto);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", room);
        response.put("message", "Interview room created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/code/{roomCode}")
    public ResponseEntity<Map<String, Object>> getRoomByCode(@PathVariable String roomCode) {
        InterviewRoomDTO room = interviewService.getRoomByCode(roomCode);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", room);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRoomById(@PathVariable Long id) {
        InterviewRoomDTO room = interviewService.getRoomById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", room);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/recruiter/{recruiterId}")
    public ResponseEntity<Map<String, Object>> getRoomsByRecruiterId(
            @PathVariable Long recruiterId
    ) {
        List<InterviewRoomDTO> rooms = interviewService.getRoomsByRecruiterId(recruiterId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", rooms);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/applicant/{applicantId}")
    public ResponseEntity<Map<String, Object>> getRoomsByApplicantId(
            @PathVariable Long applicantId
    ) {
        List<InterviewRoomDTO> rooms = interviewService.getRoomsByApplicantId(applicantId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", rooms);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{roomCode}/status")
    public ResponseEntity<Map<String, Object>> updateRoomStatus(
            @PathVariable String roomCode,
            @RequestBody Map<String, String> request
    ) {
        String status = request.get("status");
        InterviewRoomDTO room = interviewService.updateRoomStatus(roomCode, status);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", room);
        response.put("message", "Room status updated successfully");
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{roomCode}/extend")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Map<String, Object>> extendInterview(
            @PathVariable String roomCode,
            @RequestBody Map<String, Integer> request
    ) {
        Integer additionalMinutes = request.get("additionalMinutes");
        if (additionalMinutes == null || additionalMinutes <= 0) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Additional minutes must be a positive number");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        InterviewRoomDTO room = interviewService.extendInterviewDuration(roomCode, additionalMinutes);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", room);
        response.put("message", "Interview duration extended successfully");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{roomCode}/messages")
    public ResponseEntity<Map<String, Object>> getMessagesByRoomCode(
            @PathVariable String roomCode
    ) {
        List<InterviewMessageDTO> messages = interviewService.getMessagesByRoomCode(roomCode);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", messages);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/results")
    public ResponseEntity<Map<String, Object>> submitResult(
            @RequestBody CreateInterviewResultDTO dto
    ) {
        InterviewResultDTO result = interviewService.submitResult(dto);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", result);
        response.put("message", "Interview result submitted successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/results/room/{roomId}")
    public ResponseEntity<Map<String, Object>> getResultByRoomId(@PathVariable Long roomId) {
        InterviewResultDTO result = interviewService.getResultByRoomId(roomId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", result);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/validate-access")
    public ResponseEntity<Map<String, Object>> validateAccess(
            @RequestBody JoinRoomRequestDTO request
    ) {
        boolean hasAccess = interviewService.validateUserAccess(
                request.getRoomCode(), 
                request.getUserId()
        );
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", Map.of("hasAccess", hasAccess));
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{roomId}/questions")
    public ResponseEntity<Map<String, Object>> getInterviewQuestions(@PathVariable Long roomId) {
        List<InterviewQuestionDTO> questions = interviewService.getInterviewQuestions(roomId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", questions);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/questions/{questionId}/answer")
    public ResponseEntity<Map<String, Object>> answerQuestion(
            @PathVariable Long questionId,
            @RequestBody AnswerQuestionDTO dto
    ) {
        InterviewQuestionDTO question = interviewService.answerQuestion(questionId, dto);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", question);
        response.put("message", "Answer submitted successfully");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/questions/{questionId}/evaluate")
    public ResponseEntity<Map<String, Object>> evaluateQuestion(
            @PathVariable Long questionId,
            @RequestBody EvaluateQuestionDTO dto
    ) {
        InterviewQuestionDTO question = interviewService.evaluateQuestion(questionId, dto);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", question);
        response.put("message", "Question evaluated successfully");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/results/async")
    public ResponseEntity<Map<String, Object>> submitAsyncResult(
            @RequestBody CreateInterviewResultDTO dto
    ) {
        InterviewResultDTO result = interviewService.submitAsyncResult(dto);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", result);
        response.put("message", "Interview result submitted and email sent");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/results/draft")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Map<String, Object>> saveDraftResult(
            @RequestBody CreateInterviewResultDTO dto
    ) {
        InterviewResultDTO result = interviewService.saveDraftResult(dto);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", result);
        response.put("message", "Draft evaluation saved successfully");
        return ResponseEntity.ok(response);
    }
}

