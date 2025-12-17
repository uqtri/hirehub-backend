package org.example.hirehub.controller;

import org.example.hirehub.dto.relationship.*;
import org.example.hirehub.entity.Relationship;
import org.example.hirehub.entity.User;
import org.example.hirehub.mapper.RelationshipMapper;
import org.example.hirehub.service.NotificationService;
import org.example.hirehub.service.RelationshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relationships")
public class RelationshipController {

    private final RelationshipService relationshipService;
    private final RelationshipMapper relationshipMapper;

    public RelationshipController(RelationshipService relationshipService, RelationshipMapper relationshipMapper) {
        this.relationshipService = relationshipService;
        this.relationshipMapper = relationshipMapper;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, ?>> findRelationshipsByUserId(@PathVariable Long userId, @ModelAttribute RelationshipFilter relationshipFilter) {

        List<Relationship> relationships = relationshipService.findRelationshipsByUserId(userId, relationshipFilter);

        List<RelationshipDetailDTO> data = relationships.stream().map(relationshipMapper::toDTO).toList();


        return ResponseEntity.ok().body(Map.of("data", data));
    }
    @GetMapping("/friends")
    public ResponseEntity<Map<String, ?>> findFriends(@RequestParam Long userId) {
        List<FriendDTO> friends = relationshipService.findFriends(userId);

        return ResponseEntity.ok().body(Map.of("data", friends));
    }

    @PostMapping("")
    public ResponseEntity<Map<String, ?>> create(@RequestBody CreateRelationshipRequestDTO request) {

        Relationship relationship = relationshipService.create(request);

        return ResponseEntity.ok().body(Map.of("data", relationshipMapper.toDTO(relationship)));
    }
    @DeleteMapping("/{userId1}/{userId2}")
    public ResponseEntity<Map<String, ?>> delete(@PathVariable Long userId1, @PathVariable Long userId2) {

        relationshipService.delete(userId1, userId2);
        return ResponseEntity.ok().body(Map.of("message", "Xóa quan hệ thành công"));
    }
    @PutMapping("/{userId1}/{userId2}")
    public ResponseEntity<Map<String, ?>> update(@PathVariable Long userId1, @PathVariable Long userId2, @RequestBody UpdateRelationshipRequestDTO request) {

        relationshipService.update(userId1, userId2, request);

        return ResponseEntity.ok().body(Map.of("message", "Cập nhật quan hệ thành công"));
    }
}
