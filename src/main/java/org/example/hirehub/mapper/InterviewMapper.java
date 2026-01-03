package org.example.hirehub.mapper;

import org.example.hirehub.dto.interview.*;
import org.example.hirehub.entity.InterviewMessage;
import org.example.hirehub.entity.InterviewResult;
import org.example.hirehub.entity.InterviewRoom;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InterviewMapper {
    
    @Mapping(target = "jobId", source = "job.id")
    @Mapping(target = "jobTitle", source = "job.title")
    @Mapping(target = "applicantId", source = "applicant.id")
    @Mapping(target = "applicantName", source = "applicant.name")
    @Mapping(target = "applicantEmail", source = "applicant.email")
    @Mapping(target = "applicantAvatar", source = "applicant.avatar")
    @Mapping(target = "recruiterId", source = "recruiter.id")
    @Mapping(target = "recruiterName", source = "recruiter.name")
    @Mapping(target = "recruiterEmail", source = "recruiter.email")
    @Mapping(target = "recruiterAvatar", source = "recruiter.avatar")
    InterviewRoomDTO toRoomDTO(InterviewRoom room);
    
    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "senderName", source = "sender.name")
    @Mapping(target = "senderAvatar", source = "sender.avatar")
    InterviewMessageDTO toMessageDTO(InterviewMessage message);
    
    @Mapping(target = "roomId", source = "room.id")
    InterviewResultDTO toResultDTO(InterviewResult result);
}

