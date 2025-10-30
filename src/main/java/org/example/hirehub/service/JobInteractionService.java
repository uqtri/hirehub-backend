package org.example.hirehub.service;

import org.example.hirehub.dto.jobInteraction.CreateJobInteractionRequestDTO;
import org.example.hirehub.dto.jobInteraction.JobInteractionFilter;
import org.example.hirehub.entity.Job;
import org.example.hirehub.entity.JobInteraction;
import org.example.hirehub.entity.User;
import org.example.hirehub.mapper.JobInteractionMapper;
import org.example.hirehub.repository.JobInteractionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.example.hirehub.specification.JobInteractionSpecification.*;

@Service
public class JobInteractionService {

    private final JobInteractionMapper jobInteractionMapper;
    private final JobInteractionRepository jobInteractionRepository;
    private final UserService userService;
    private final JobService jobService;

    JobInteractionService(JobInteractionMapper jobInteractionMapper, JobInteractionRepository jobInteractionRepository, UserService userService, JobService jobService) {
        this.jobInteractionMapper = jobInteractionMapper;
        this.jobInteractionRepository = jobInteractionRepository;
        this.userService = userService;
        this.jobService = jobService;
    }
    public JobInteraction create(CreateJobInteractionRequestDTO request) {

        Specification<JobInteraction> specification = interactionType(request.getInteraction()).and(userId(request.getUserId())).and(jobId(request.getJobId()));

        JobInteraction existingJobInteraction = jobInteractionRepository.findOne(specification).orElse(null);

        if(existingJobInteraction != null) {
            jobInteractionRepository.delete(existingJobInteraction);
            return null;
        }
        JobInteraction jobInteraction = new JobInteraction();


        jobInteractionMapper.updateFromDTO(jobInteraction, request);
        User user = userService.getUserById(request.getUserId());
        Job job = jobService.getJobById(request.getJobId());

        jobInteraction.setUser(user);
        jobInteraction.setJob(job);

        return jobInteractionRepository.save(jobInteraction);
    }
    public List<JobInteraction> findAll(JobInteractionFilter filter) {

        Specification<JobInteraction> specifications = interactionType(filter.getInteraction()).and(userId(filter.getUserId()));

        return jobInteractionRepository.findAll(specifications);
    }
    public Page<JobInteraction> findAll(JobInteractionFilter filter, Pageable pageable) {

        Specification<JobInteraction> specifications = interactionType(filter.getInteraction()).and(userId(filter.getUserId()));

        return jobInteractionRepository.findAll(specifications, pageable);
    }
    public void deleteById (Long id) {
        jobInteractionRepository.deleteById(id);
    }

}
