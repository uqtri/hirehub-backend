package org.example.hirehub.mapper;

import org.example.hirehub.dto.notification.NotificationSummaryDTO;
import org.example.hirehub.dto.user.UserSummaryDTO;
import org.example.hirehub.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "type", expression = "java(notification.getType().name())")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "isRead", source = "read")
    @Mapping(target = "redirectUrl", source = "redirectUrl")
    NotificationSummaryDTO toDTO(Notification notification);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "avatar", source = "avatar")
    UserSummaryDTO toUserSummaryDTO(org.example.hirehub.entity.User user);
}
