package org.example.hirehub.mapper;

import org.example.hirehub.dto.job.JobSummaryDTO;
import org.example.hirehub.dto.report.CreateReportRequestDTO;
import org.example.hirehub.dto.report.ReportDetailDTO;
import org.example.hirehub.dto.report.UpdateReportRequestDTO;
import org.example.hirehub.dto.user.UserSummaryDTO;
import org.example.hirehub.entity.Job;
import org.example.hirehub.entity.Report;
import org.example.hirehub.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    ReportDetailDTO toDTO (Report report);
    UserSummaryDTO toDTO (User user);
    JobSummaryDTO toDTO (Job job);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDTO(@MappingTarget Report report, CreateReportRequestDTO dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDTO(@MappingTarget Report report, UpdateReportRequestDTO dto);


}
