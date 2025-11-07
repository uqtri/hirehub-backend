package org.example.hirehub.mapper;

import org.example.hirehub.dto.relationship.CreateRelationshipRequestDTO;
import org.example.hirehub.dto.relationship.RelationshipDetailDTO;
import org.example.hirehub.dto.relationship.RelationshipSummaryDTO;
import org.example.hirehub.dto.user.UserSummaryDTO;
import org.example.hirehub.entity.Relationship;
import org.example.hirehub.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")

public interface RelationshipMapper {

//    @Mapping(target = "id", source = "relationshipKey")
    @Mapping(target = "sender", source = "userA")
    @Mapping(target = "receiver", source = "userB")

    RelationshipDetailDTO toDTO(Relationship relationship);

    UserSummaryDTO toDTO(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDTO(@MappingTarget Relationship relationship, CreateRelationshipRequestDTO dto);

}
