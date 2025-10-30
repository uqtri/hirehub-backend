package org.example.hirehub.controller;

import org.example.hirehub.dto.report.CreateReportRequestDTO;
import org.example.hirehub.dto.report.ReportDetailDTO;
import org.example.hirehub.dto.report.ReportFilter;
import org.example.hirehub.dto.report.UpdateReportRequestDTO;
import org.example.hirehub.entity.Report;
import org.example.hirehub.mapper.ReportMapper;
import org.example.hirehub.service.ReportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {


    private final ReportService reportService;
    private final ReportMapper reportMapper;

    public ReportController(ReportService reportService, ReportMapper reportMapper) {
        this.reportService = reportService;
        this.reportMapper = reportMapper;
    }

    @GetMapping("")
    public Page<ReportDetailDTO> getReports(@ModelAttribute ReportFilter filter, Pageable pageable) {


        return reportService.findAllWithResource(filter, pageable);
    }
    @PostMapping("")
    public ResponseEntity<Map<String, ?>> createReport(@RequestBody CreateReportRequestDTO request) {

        Report report = reportService.create(request);

        return ResponseEntity.ok().body(Map.of("data", reportMapper.toDTO(report)));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, ?>> updateReport(@PathVariable Long id, @RequestBody UpdateReportRequestDTO request) {

        Report report = reportService.update(id, request);
        return ResponseEntity.ok().body(Map.of("data", reportMapper.toDTO(report)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, ?>> deleteReport(@PathVariable Long id) {
        reportService.delete(id);

        return ResponseEntity.ok().body(Map.of("message", "Xóa báo cáo thành công"));
    }
}
