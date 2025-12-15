package org.example.hirehub.service;

import org.example.hirehub.dto.relationship.CreateRelationshipRequestDTO;
import org.example.hirehub.dto.relationship.FriendDTO;
import org.example.hirehub.dto.relationship.RelationshipFilter;
import org.example.hirehub.dto.relationship.UpdateRelationshipRequestDTO;
import org.example.hirehub.entity.Relationship;
import org.example.hirehub.entity.User;
import org.example.hirehub.key.RelationshipKey;
import org.example.hirehub.mapper.RelationshipMapper;
import org.example.hirehub.mapper.UserMapper;
import org.example.hirehub.repository.RelationshipRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import static org.example.hirehub.specification.RelationshipSpecification.*;

@Service
public class RelationshipService {

    private final RelationshipRepository relationshipRepository;
    private final RelationshipMapper relationshipMapper;
    private final FirebaseNotificationService firebaseNotificationService;
    private final UserService userService;
    private final UserMapper userMapper;

    public RelationshipService(RelationshipRepository relationshipRepository, RelationshipMapper relationshipMapper, UserService userService, UserMapper userMapper, FirebaseNotificationService firebaseNotificationService) {
        this.relationshipRepository = relationshipRepository;
        this.relationshipMapper = relationshipMapper;
        this.userService = userService;
        this.userMapper = userMapper;
        this.firebaseNotificationService = firebaseNotificationService;
    }

    public List<Relationship> findRelationshipsByUserId(Long userId) {

        return relationshipRepository.findRelationshipsByUserId(userId);
    }
    public List<Relationship> findRelationshipsByUserId(Long userId, RelationshipFilter relationshipFilter) {
        Specification<Relationship> specifications = status(relationshipFilter.getStatus()).and(receiver(userId).or(sender(userId))).and(receiver(relationshipFilter.getReceiverId())).and(sender(relationshipFilter.getSenderId()));
        return relationshipRepository.findAll(specifications);
    }
    public List<FriendDTO> findFriends(Long userId) {
        Specification<Relationship> specifications = status("connected").and(receiver(userId).or(sender(userId)));
        List<Relationship> relationships = relationshipRepository.findAll(specifications);

        List<FriendDTO> friends = new ArrayList<>();
        for (Relationship relationship : relationships) {
            FriendDTO friendDTO = new FriendDTO();
            if(relationship.getUserA().getId().equals(userId)) {
                friendDTO.setUser(userMapper.toDTO(relationship.getUserB()));
                friends.add(friendDTO);
            }
            else {
                friendDTO.setUser(userMapper.toDTO(relationship.getUserA()));
                friends.add(friendDTO);
            }
        }
        return friends;
    }

    public Relationship create(CreateRelationshipRequestDTO request) {

        User sender = userService.getUserById(request.getSenderId());
        User receiver = userService.getUserById(request.getReceiverId());

        Relationship relationship = new Relationship(sender, receiver);

        firebaseNotificationService.notifyUser(receiver.getId(), "HireHub", "Bạn có lời mời kết nối từ " + sender.getName());

        return relationshipRepository.save(relationship);
    }

    public void delete(Long userId1, Long userId2) {

        RelationshipKey relationshipKey = new RelationshipKey(userId1, userId2);
        RelationshipKey relationshipKey2 = new RelationshipKey(userId2, userId1);
        relationshipRepository.deleteById(relationshipKey);
        relationshipRepository.deleteById(relationshipKey2);
    }

    public Relationship update(Long userId1, Long userId2, UpdateRelationshipRequestDTO request) {
        RelationshipKey relationshipKey = new RelationshipKey(userId1, userId2);
        RelationshipKey relationshipKey2 = new RelationshipKey(userId2, userId1);

        Relationship relationship = relationshipRepository.findById(relationshipKey).orElse(null);
        Relationship relationship2 = relationshipRepository.findById(relationshipKey2).orElse(null);

        if(relationship != null) {
            if(request.getStatus() != null) relationship.setStatus(request.getStatus());
            relationshipRepository.save(relationship);
            return relationship;
        }
        if(relationship2 != null) {
            if(request.getStatus() != null) relationship2.setStatus(request.getStatus());
            relationshipRepository.save(relationship2);
            return relationship2;
        }
        return null;
    }
}
