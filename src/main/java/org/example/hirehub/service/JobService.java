package org.example.hirehub.service;

import org.springframework.stereotype.Service;

import org.example.hirehub.repository.JobRepository;
import org.example.hirehub.entity.Job;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    public JobService(JobRepository jobRepository){
        this.jobRepository = jobRepository;
    }

    public List<Job> getAllJobs(String postingDate, String company, String title,
                               String location, String level, String workspace,
                               String keyword) {

        LocalDateTime dateFilter = null;
        if (postingDate != null) {
            LocalDateTime now = LocalDateTime.now();
            switch (postingDate) {
                case "24h":
                    dateFilter = now.minusHours(24);
                    break;
                case "1w":
                    dateFilter = now.minusWeeks(1);
                    break;
                case "1m":
                    dateFilter = now.minusMonths(1);
                    break;
            }
        }

        List<Job> jobs = jobRepository.searchJobsDynamic(
                title, company, location, level, workspace, dateFilter,keyword
        );

        return new ArrayList<>(jobs);
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id).orElse(null);
    }

    public Job createJob(Job job) {
        return jobRepository.save(job);
    }

    public Job updateJob(Job job) {
        return jobRepository.save(job);
    }

    public void deleteJob(Long id) {
        Job job = jobRepository.findById(id).orElse(null);
        if(job == null) {
            return;
        }

        job.setDeleted(true);
        jobRepository.save(job);
    }
}
