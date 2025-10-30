package org.example.hirehub.service;

import org.example.hirehub.exception.ResourceNotFoundException;
import org.example.hirehub.mapper.JobMapper;
import org.example.hirehub.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class ReportResourceResolverService {

    private final UserService userService;
    private final JobService jobService;
    private final UserMapper userMapper;
    private final JobMapper jobMapper;

    public ReportResourceResolverService(UserService userService, JobService jobService, UserMapper userMapper, JobMapper jobMapper) {
        this.userService = userService;
        this.jobService = jobService;
        this.userMapper = userMapper;
        this.jobMapper = jobMapper;
    }

    public Object resolve(String resourceName, Long resourceId) {

        return switch(resourceName.toLowerCase()) {
            case "user" -> userService.getUserById(resourceId);
            case "job" -> jobService.getJobById(resourceId);
            default ->  throw new ResourceNotFoundException("Không tồn tại tai nguyên");
        };
    }
    public Object resolveToDTO(String resourceName, Long resourceId) {

        return switch(resourceName.toLowerCase()) {
            case "user" -> userMapper.toDTO(userService.getUserById(resourceId));
            case "job" -> jobMapper.toDTO(jobService.getJobById(resourceId));
            default ->  throw new ResourceNotFoundException("Không tồn tại tai nguyên");
        };
    }
}
