package org.example.hirehub.service;

import jdk.jfr.Threshold;
import org.example.hirehub.dto.chatbot.ChatbotRequestDTO;
import org.example.hirehub.dto.job.JobDetailDTO;
import org.example.hirehub.dto.skill.SkillSummaryDTO;
import org.example.hirehub.entity.Job;
import org.example.hirehub.entity.Skill;
import org.example.hirehub.entity.User;
import org.example.hirehub.mapper.JobMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatbotService {

    private final UserService userService;
    private final JobService jobService;
    private final FuzzyMatchService fuzzyMatchService;
    private final JobMapper jobMapper;
    private final double THRESHOLD = 0.7;
    private final OpenAiFileService openAiFileService;
    private final AssistantRunService assistantRunService;
    private final AuthenticationManager authenticationManager;

    @Value("${ai.ner-url}")
    private String nerUrl;

    private final HttpClientService httpClientService;

    public ChatbotService(HttpClientService httpClientService, UserService userService, JobService jobService, FuzzyMatchService fuzzyMatchService, JobMapper jobMapper, OpenAiFileService openAiFileService, AssistantRunService assistantRunService, AuthenticationManager authenticationManager) {
        this.httpClientService = httpClientService;
        this.userService = userService;
        this.jobService = jobService;
        this.fuzzyMatchService = fuzzyMatchService;
        this.jobMapper = jobMapper;
        this.openAiFileService = openAiFileService;
        this.assistantRunService = assistantRunService;
        this.authenticationManager = authenticationManager;
    }
    public List<JobDetailDTO> findJob(ChatbotRequestDTO request) {

        Map data = Map.of("text", request.getMessage());


        Map response = httpClientService.post(nerUrl + "/predict_json", data, Map.class);

        Map result = (Map)response.get("result");

//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String email = auth.getName();
//        User user = userService.getUserByEmail(email);
        List<String> skills = (List)result.get("SKILL");
        List<String> locations = (List)result.get("LOC");
        List<JobDetailDTO> jobs = new ArrayList<>(jobService.getAllJobs().stream().map(jobMapper::toDTO).toList());


        jobs = new ArrayList<>(jobs.stream().filter(job -> {
            boolean skillFound = false;
            boolean locationFound = false;

            if(locations == null || locations.isEmpty()) {
                locationFound = true;
            }
            if(skills == null || skills.isEmpty()) {
                skillFound = true;
            }
            if(skills != null) {
                List<String> jobSkills = job.getSkills().stream().map(SkillSummaryDTO::getName).toList();
                for(String skill : skills) {
                    for(String jobSkill : jobSkills) {
                        if(fuzzyMatchService.isSimilar(skill, jobSkill, THRESHOLD))
                            skillFound = true;
                    }
                }
            }
            if(locations != null) {
                for (String location : locations) {
                    if(fuzzyMatchService.isSimilar(location, job.getAddress(), THRESHOLD))
                        locationFound = true;
                }
            }
            return skillFound && locationFound;
        }).toList());

        Collections.shuffle(jobs);
        jobs = jobs.subList(0, Math.min(jobs.size(), 5));
        return jobs;
    }
    public String analyzeResume(ChatbotRequestDTO request) {

        String prompt = request.getMessage();

        String fileId;
        if(request.getResume() != null && !request.getResume().isEmpty())
            fileId = openAiFileService.uploadFile(request.getResume());
        else {
            fileId = request.getResumeId();
        }
        String threadId = assistantRunService.createThread();

        assistantRunService.postMessageWithFile(threadId, fileId, prompt);

        String runId = assistantRunService.runAssistant(threadId);

        assistantRunService.pollRunUntilDone(threadId, runId, 60_000);

        List<Map> messages = assistantRunService.fetchMessages(threadId);

        return assistantRunService.extractAnswerFromMessages(messages);
    }
}
