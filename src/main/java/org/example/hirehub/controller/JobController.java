package org.example.hirehub.controller;

import org.example.hirehub.dto.job.JobDetailDTO;
import org.example.hirehub.mapper.JobMapper;
import org.example.hirehub.service.JobService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")

public class JobController {

    private final JobService jobService;
    private final JobMapper jobMapper;

    public JobController(JobService jobService, JobMapper jobMapper) {
        this.jobService = jobService;
        this.jobMapper = jobMapper;
    }

    @GetMapping("")
    public List<JobDetailDTO> findAll(String postingDate, String company, String title,
                                      String location, String level, String workspace,
                                      String keyword){
        return jobService.getAllJobs(postingDate, company, title, location, level, workspace, keyword).stream().map(jobMapper::toDTO).toList();
    }

}
