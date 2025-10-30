package org.example.hirehub.service;

import org.example.hirehub.dto.report.CreateReportRequestDTO;
import org.example.hirehub.dto.report.ReportDetailDTO;
import org.example.hirehub.dto.report.ReportFilter;
import org.example.hirehub.dto.report.UpdateReportRequestDTO;
import org.example.hirehub.entity.Report;
import org.example.hirehub.entity.User;
import org.example.hirehub.exception.ResourceNotFoundException;
import org.example.hirehub.mapper.ReportMapper;
import org.example.hirehub.repository.ReportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import static org.example.hirehub.specification.ReportSpecification.*;
@Service
public class ReportService {


    private final ReportMapper reportMapper;
    private final UserService userService;
    private final JobService jobService;
    private final ReportResourceResolverService reportResourceResolverService;
    private final ReportRepository reportRepository;

    public ReportService(ReportMapper reportMapper, UserService userService, JobService jobService, ReportResourceResolverService reportResourceResolverService, ReportRepository reportRepository) {
        this.reportMapper = reportMapper;
        this.userService = userService;
        this.jobService = jobService;
        this.reportResourceResolverService = reportResourceResolverService;
        this.reportRepository = reportRepository;
    }

    public Page<Report> findAll(ReportFilter filter, Pageable pageable) {

        Specification<Report> specification = reportSpecification(filter);
        return reportRepository.findAll(specification, pageable);
    }
    public Page<ReportDetailDTO> findAllWithResource(ReportFilter filter, Pageable pageable) {
        Specification<Report> specification = reportSpecification(filter);

        return reportRepository.findAll(specification, pageable).map(report -> {
            ReportDetailDTO reportDetail = reportMapper.toDTO(report);
            Object resource = reportResourceResolverService.resolveToDTO(report.getResourceName(), report.getResourceId());
            reportDetail.setResource(resource);
            return reportDetail;
        });
    }
    public Report create(CreateReportRequestDTO request) {
        Report report = new Report();

        reportMapper.updateFromDTO(report, request);

        User user = userService.getUserById(request.getReporterId());
        if(user == null) {
            throw new ResourceNotFoundException("Không tồn tại người báo cáo");
        }
        report.setReporter(user);
        Object resource = reportResourceResolverService.resolve(request.getResourceName(), request.getResourceId());

        if(resource == null) {
            throw new ResourceNotFoundException("Không tìm thấy tài nguyên");
        }
        return reportRepository.save(report);
    }
    public Report update(Long id, UpdateReportRequestDTO request) {
        Report report = reportRepository.findById(id).orElse(null);

        if(report == null)
            throw new ResourceNotFoundException("Không tìm thấy tài nguyên");
        reportMapper.updateFromDTO(report, request);

        reportRepository.save(report);
        return report;
    }
    public void delete(Long id) {
        reportRepository.deleteById(id);
    }
}
