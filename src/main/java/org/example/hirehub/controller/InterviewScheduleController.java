package org.example.hirehub.controller;

import org.example.hirehub.dto.interview.*;
import org.example.hirehub.service.InterviewScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interview-schedule")
public class InterviewScheduleController {

    private final InterviewScheduleService scheduleService;

    public InterviewScheduleController(InterviewScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping("/request")
    public ResponseEntity<Map<String, Object>> createScheduleRequest(
            @RequestBody CreateScheduleRequestDTO dto) {
        ScheduleRequestDTO scheduleRequest = scheduleService.createScheduleRequest(dto);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", scheduleRequest);
        response.put("message", "Schedule request created and sent to applicant");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/request/{requestCode}")
    public ResponseEntity<Map<String, Object>> getScheduleRequest(
            @PathVariable String requestCode) {
        ScheduleRequestDTO scheduleRequest = scheduleService.getScheduleRequestByCode(requestCode);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", scheduleRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/select")
    public ResponseEntity<Map<String, Object>> selectTimeSlot(
            @RequestBody SelectTimeSlotDTO dto) {
        InterviewRoomDTO room = scheduleService.selectTimeSlot(dto);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", room);
        response.put("message", "Time slot selected and interview room created");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/pending/applicant/{applicantId}")
    public ResponseEntity<Map<String, Object>> getPendingRequestsByApplicant(
            @PathVariable Long applicantId) {
        List<ScheduleRequestDTO> requests = scheduleService.getPendingRequestsByApplicant(applicantId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", requests);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending/recruiter/{recruiterId}")
    public ResponseEntity<Map<String, Object>> getPendingRequestsByRecruiter(
            @PathVariable Long recruiterId) {
        List<ScheduleRequestDTO> requests = scheduleService.getPendingRequestsByRecruiter(recruiterId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", requests);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all/applicant/{applicantId}")
    public ResponseEntity<Map<String, Object>> getAllRequestsByApplicant(
            @PathVariable Long applicantId) {
        List<ScheduleRequestDTO> requests = scheduleService.getAllRequestsByApplicant(applicantId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", requests);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all/recruiter/{recruiterId}")
    public ResponseEntity<Map<String, Object>> getAllRequestsByRecruiter(
            @PathVariable Long recruiterId) {
        List<ScheduleRequestDTO> requests = scheduleService.getAllRequestsByRecruiter(recruiterId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", requests);
        return ResponseEntity.ok(response);
    }
}
