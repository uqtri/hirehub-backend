package org.example.hirehub.controller;

import org.example.hirehub.dto.conversation.AddParticipantsDTO;
import org.example.hirehub.dto.conversation.ConversationDetailDTO;
import org.example.hirehub.dto.conversation.CreateConversationDTO;
import org.example.hirehub.entity.Conversation;
import org.example.hirehub.mapper.ConversationMapper;
import org.example.hirehub.service.ConversationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {
    private final ConversationService conversationService;
    private final ConversationMapper conversationMapper;

    public ConversationController(ConversationService conversationService, ConversationMapper conversationMapper) {
        this.conversationService = conversationService;
        this.conversationMapper = conversationMapper;
    }

    @PostMapping
    public ResponseEntity<ConversationDetailDTO> createConversation(@RequestBody CreateConversationDTO dto) {
        Conversation conversation = conversationService.createConversation(dto);
        ConversationDetailDTO dto_result = conversationMapper.toDTO(conversation);
        return ResponseEntity.ok(dto_result);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ConversationDetailDTO>> getUserConversations(@PathVariable Long userId) {
        List<Conversation> conversations = conversationService.getUserConversations(userId);
        List<ConversationDetailDTO> dtos = conversations.stream()
                .map(conv -> {
                    ConversationDetailDTO dto = conversationMapper.toDTO(conv);
                    dto.setUnreadCount(conversationService.getUnreadCount(conv.getId(), userId));
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<ConversationDetailDTO> getConversation(
            @PathVariable Long conversationId,
            @RequestParam Long userId) {
        Conversation conversation = conversationService.getConversationById(conversationId, userId);
        ConversationDetailDTO dto = conversationMapper.toDTO(conversation);
        dto.setUnreadCount(conversationService.getUnreadCount(conversationId, userId));
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{conversationId}/participants")
    public ResponseEntity<ConversationDetailDTO> addParticipants(
            @PathVariable Long conversationId,
            @RequestBody AddParticipantsDTO dto,
            @RequestParam Long userId) {
        Conversation conversation = conversationService.addParticipants(conversationId, dto, userId);
        ConversationDetailDTO result = conversationMapper.toDTO(conversation);
        result.setUnreadCount(conversationService.getUnreadCount(conversationId, userId));
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{conversationId}/participants/{participantId}")
    public ResponseEntity<Void> removeParticipant(
            @PathVariable Long conversationId,
            @PathVariable Long participantId,
            @RequestParam Long userId) {
        conversationService.removeParticipant(conversationId, participantId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Leader kick một participant ra khỏi group
     */
    @PostMapping("/{conversationId}/kick/{participantId}")
    public ResponseEntity<Void> kickParticipant(
            @PathVariable Long conversationId,
            @PathVariable Long participantId,
            @RequestParam Long userId) {
        conversationService.kickParticipant(conversationId, participantId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * User tự rời khỏi group
     */
    @PostMapping("/{conversationId}/leave")
    public ResponseEntity<Void> leaveGroup(
            @PathVariable Long conversationId,
            @RequestParam Long userId) {
        conversationService.leaveGroup(conversationId, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{conversationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long conversationId,
            @RequestParam Long userId) {
        conversationService.updateLastReadAt(conversationId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Leader giải tán nhóm
     */
    @DeleteMapping("/{conversationId}/disband")
    public ResponseEntity<Void> disbandGroup(
            @PathVariable Long conversationId,
            @RequestParam Long userId) {
        conversationService.disbandGroup(conversationId, userId);
        return ResponseEntity.ok().build();
    }
}
