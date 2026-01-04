package org.example.hirehub.controller;

import org.example.hirehub.dto.interview.CreateInterviewMessageDTO;
import org.example.hirehub.dto.interview.InterviewMessageDTO;
import org.example.hirehub.dto.interview.JoinRoomRequestDTO;
import org.example.hirehub.service.InterviewService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class InterviewWebSocketController {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final InterviewService interviewService;
    
    public InterviewWebSocketController(
            SimpMessagingTemplate messagingTemplate,
            InterviewService interviewService
    ) {
        this.messagingTemplate = messagingTemplate;
        this.interviewService = interviewService;
    }
    
    @MessageMapping("/interview/message")
    public void handleInterviewMessage(@Payload CreateInterviewMessageDTO dto) {
        // Check if room is expired
        var room = interviewService.getRoomByCode(dto.getRoomCode());
        if (room.isExpired()) {
            System.out.println("Cannot send message: Interview room has expired");
            return;
        }
        
        // Save message to database
        InterviewMessageDTO message = interviewService.createMessage(dto);
        
        // Send message to both participants in the room
        sendToRoom(dto.getRoomCode(), "/queue/interview-message", message);
    }
    
    @MessageMapping("/interview/join")
    public void handleJoinRoom(@Payload JoinRoomRequestDTO dto) {
        // Validate user access
        boolean hasAccess = interviewService.validateUserAccess(dto.getRoomCode(), dto.getUserId());
        
        if (!hasAccess) {
            return;
        }
        
        // Get room details
        var room = interviewService.getRoomByCode(dto.getRoomCode());
        
        // If room is expired, send read-only notification
        if (room.isExpired()) {
            String expiredMessage = "This interview has expired. You can view the conversation history but cannot send new messages.";
            CreateInterviewMessageDTO systemMsg = new CreateInterviewMessageDTO();
            systemMsg.setRoomCode(dto.getRoomCode());
            systemMsg.setSenderId(dto.getUserId());
            systemMsg.setSenderRole("SYSTEM");
            systemMsg.setType("SYSTEM");
            systemMsg.setContent(expiredMessage);
            
            InterviewMessageDTO message = interviewService.createMessage(systemMsg);
            sendToRoom(dto.getRoomCode(), "/queue/interview-expired", message);
            return;
        }
        
        // Notify the other participant
        String joinMessage = String.format("User has joined the interview room");
        
        CreateInterviewMessageDTO systemMsg = new CreateInterviewMessageDTO();
        systemMsg.setRoomCode(dto.getRoomCode());
        systemMsg.setSenderId(dto.getUserId());
        systemMsg.setSenderRole("SYSTEM");
        systemMsg.setType("SYSTEM");
        systemMsg.setContent(joinMessage);
        
        InterviewMessageDTO message = interviewService.createMessage(systemMsg);
        sendToRoom(dto.getRoomCode(), "/queue/interview-message", message);
        
        // Check if both users are present and update status to ONGOING
        sendToRoom(dto.getRoomCode(), "/queue/interview-join", message);
    }
    
    @MessageMapping("/interview/leave")
    public void handleLeaveRoom(@Payload JoinRoomRequestDTO dto) {
        String leaveMessage = "User has left the interview room";
        
        CreateInterviewMessageDTO systemMsg = new CreateInterviewMessageDTO();
        systemMsg.setRoomCode(dto.getRoomCode());
        systemMsg.setSenderId(dto.getUserId());
        systemMsg.setSenderRole("SYSTEM");
        systemMsg.setType("SYSTEM");
        systemMsg.setContent(leaveMessage);
        
        InterviewMessageDTO message = interviewService.createMessage(systemMsg);
        sendToRoom(dto.getRoomCode(), "/queue/interview-message", message);
    }
    
    @MessageMapping("/interview/question")
    public void handleInterviewQuestion(@Payload CreateInterviewMessageDTO dto) {
        // Check if room is expired
        var room = interviewService.getRoomByCode(dto.getRoomCode());
        if (room.isExpired()) {
            System.out.println("Cannot send question: Interview room has expired");
            return;
        }
        
        dto.setType("QUESTION");
        InterviewMessageDTO message = interviewService.createMessage(dto);
        sendToRoom(dto.getRoomCode(), "/queue/interview-question", message);
    }
    
    @MessageMapping("/interview/end")
    public void handleEndInterview(@Payload JoinRoomRequestDTO dto) {
        // Update room status to FINISHED
        interviewService.updateRoomStatus(dto.getRoomCode(), "FINISHED");
        
        // Notify both participants
        sendToRoom(dto.getRoomCode(), "/queue/interview-end", 
                Map.of("roomCode", dto.getRoomCode(), "message", "Interview has ended"));
    }
    
    private void sendToRoom(String roomCode, String destination, Object payload) {
        try {
            var room = interviewService.getRoomByCode(roomCode);
            
            // Send to applicant
            messagingTemplate.convertAndSendToUser(
                room.getApplicantEmail(),
                destination,
                payload
            );
            
            // Send to recruiter
            messagingTemplate.convertAndSendToUser(
                room.getRecruiterEmail(),
                destination,
                payload
            );
        } catch (Exception e) {
            System.err.println("Error sending message to room: " + e.getMessage());
        }
    }
    
    private static class Map<K, V> extends java.util.HashMap<K, V> {
        public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) {
            Map<K, V> map = new Map<>();
            map.put(k1, v1);
            map.put(k2, v2);
            return map;
        }
    }
}

