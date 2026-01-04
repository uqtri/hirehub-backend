package org.example.hirehub.controller;

import org.example.hirehub.dto.questionbank.*;
import org.example.hirehub.service.QuestionBankService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/question-banks")
public class QuestionBankController {
    
    private final QuestionBankService questionBankService;
    
    public QuestionBankController(QuestionBankService questionBankService) {
        this.questionBankService = questionBankService;
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createQuestionBank(
            @RequestBody CreateQuestionBankDTO dto
    ) {
        QuestionBankDTO questionBank = questionBankService.createQuestionBank(dto);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", questionBank);
        response.put("message", "Question bank created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/recruiter/{recruiterId}")
    public ResponseEntity<Map<String, Object>> getQuestionBanksByRecruiterId(
            @PathVariable Long recruiterId
    ) {
        List<QuestionBankDTO> questionBanks = questionBankService.getQuestionBanksByRecruiterId(recruiterId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", questionBanks);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getQuestionBankById(@PathVariable Long id) {
        QuestionBankDTO questionBank = questionBankService.getQuestionBankById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", questionBank);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateQuestionBank(
            @PathVariable Long id,
            @RequestBody CreateQuestionBankDTO dto
    ) {
        QuestionBankDTO questionBank = questionBankService.updateQuestionBank(id, dto);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", questionBank);
        response.put("message", "Question bank updated successfully");
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteQuestionBank(@PathVariable Long id) {
        questionBankService.deleteQuestionBank(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Question bank deleted successfully");
        return ResponseEntity.ok(response);
    }
}

