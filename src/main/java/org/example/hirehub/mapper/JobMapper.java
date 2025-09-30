package org.example.hirehub.mapper;

import org.example.hirehub.dto.job.JobDetailDTO;
import org.example.hirehub.dto.job.JobSummaryDTO;
import org.example.hirehub.dto.resume.ResumeDetailDTO;
import org.example.hirehub.dto.resume.ResumeSummaryDTO;
import org.example.hirehub.dto.user.CompanySummaryDTO;
import org.example.hirehub.dto.user.UserSummaryDTO;
import org.example.hirehub.entity.Job;
import org.example.hirehub.entity.Resume;
import org.example.hirehub.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobMapper {

    JobDetailDTO toDTO(Job job);
    CompanySummaryDTO toDTO(User user);
    ResumeSummaryDTO toDTO(Resume resume);
}
