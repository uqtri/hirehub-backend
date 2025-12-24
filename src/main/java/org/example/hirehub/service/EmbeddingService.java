package org.example.hirehub.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
import jakarta.transaction.Transactional;
import org.example.hirehub.entity.*;
import org.example.hirehub.repository.JobEmbeddingRepository;
import org.example.hirehub.repository.UserEmbeddingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmbeddingService {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingService.class);
    private static final String MODEL_NAME = "text-embedding-004";

    private final GoogleAiEmbeddingModel embeddingModel;
    private final JobEmbeddingRepository jobEmbeddingRepository;
    private final UserEmbeddingRepository userEmbeddingRepository;
    private final ObjectMapper objectMapper;

    public EmbeddingService(GoogleAiEmbeddingModel embeddingModel,
            JobEmbeddingRepository jobEmbeddingRepository,
            UserEmbeddingRepository userEmbeddingRepository,
            ObjectMapper objectMapper) {
        this.embeddingModel = embeddingModel;
        this.jobEmbeddingRepository = jobEmbeddingRepository;
        this.userEmbeddingRepository = userEmbeddingRepository;
        this.objectMapper = objectMapper;
    }

    // ============ Job Embedding ============

    @Async
    public void generateJobEmbeddingAsync(Job job) {
        try {
            generateJobEmbedding(job);
        } catch (Exception e) {
            log.error("Failed to generate embedding for job {}: {}", job.getId(), e.getMessage());
        }
    }

    @Transactional
    public void generateJobEmbedding(Job job) {
        String embeddingText = buildJobEmbeddingText(job);
        float[] vector = callEmbeddingApi(embeddingText);

        if (vector == null) {
            log.warn("Failed to get embedding vector for job {}", job.getId());
            return;
        }

        JobEmbedding jobEmbedding = jobEmbeddingRepository.findByJobId(job.getId())
                .orElse(new JobEmbedding());

        jobEmbedding.setJob(job);
        jobEmbedding.setEmbeddingText(embeddingText);
        jobEmbedding.setEmbedding(vectorToJson(vector));
        jobEmbedding.setDimensions(vector.length);
        jobEmbedding.setModel(MODEL_NAME);

        jobEmbeddingRepository.save(jobEmbedding);
        log.info("Generated embedding for job {} with {} dimensions", job.getId(), vector.length);
    }

    @Transactional
    public void deleteJobEmbedding(Long jobId) {
        if (jobEmbeddingRepository.existsByJobId(jobId)) {
            jobEmbeddingRepository.deleteByJobId(jobId);
            log.info("Deleted embedding for job {}", jobId);
        }
    }

    // ============ User Embedding ============

    @Async
    public void generateUserEmbeddingAsync(User user) {
        try {
            generateUserEmbedding(user);
        } catch (Exception e) {
            log.error("Failed to generate embedding for user {}: {}", user.getId(), e.getMessage());
        }
    }

    @Transactional
    public void generateUserEmbedding(User user) {
        String embeddingText = buildUserEmbeddingText(user);
        float[] vector = callEmbeddingApi(embeddingText);

        if (vector == null) {
            log.warn("Failed to get embedding vector for user {}", user.getId());
            return;
        }

        UserEmbedding userEmbedding = userEmbeddingRepository.findByUserId(user.getId())
                .orElse(new UserEmbedding());

        userEmbedding.setUser(user);
        userEmbedding.setEmbeddingText(embeddingText);
        userEmbedding.setEmbedding(vectorToJson(vector));
        userEmbedding.setDimensions(vector.length);
        userEmbedding.setModel(MODEL_NAME);

        userEmbeddingRepository.save(userEmbedding);
        log.info("Generated embedding for user {} with {} dimensions", user.getId(), vector.length);
    }

    @Transactional
    public void deleteUserEmbedding(Long userId) {
        if (userEmbeddingRepository.existsByUserId(userId)) {
            userEmbeddingRepository.deleteByUserId(userId);
            log.info("Deleted embedding for user {}", userId);
        }
    }

    // ============ Text Building ============

    private String buildJobEmbeddingText(Job job) {
        StringBuilder sb = new StringBuilder();

        sb.append("Job Title: ").append(job.getTitle()).append("\n");

        if (job.getDescription() != null) {
            sb.append("Description: ").append(job.getDescription()).append("\n");
        }

        if (job.getLevel() != null) {
            sb.append("Level: ").append(job.getLevel()).append("\n");
        }

        if (job.getWorkspace() != null) {
            sb.append("Workspace: ").append(job.getWorkspace()).append("\n");
        }

        if (job.getAddress() != null) {
            sb.append("Location: ").append(job.getAddress()).append("\n");
        }

        if (job.getType() != null) {
            sb.append("Type: ").append(job.getType()).append("\n");
        }

        if (job.getSkills() != null && !job.getSkills().isEmpty()) {
            String skills = job.getSkills().stream()
                    .map(js -> js.getSkill().getName())
                    .collect(Collectors.joining(", "));
            sb.append("Required Skills: ").append(skills).append("\n");
        }

        if (job.getRecruiter() != null && job.getRecruiter().getName() != null) {
            sb.append("Company: ").append(job.getRecruiter().getName()).append("\n");
        }

        return sb.toString().trim();
    }

    private String buildUserEmbeddingText(User user) {
        StringBuilder sb = new StringBuilder();

        if (user.getName() != null) {
            sb.append("Name: ").append(user.getName()).append("\n");
        }

        if (user.getPosition() != null) {
            sb.append("Position: ").append(user.getPosition()).append("\n");
        }

        if (user.getField() != null) {
            sb.append("Field: ").append(user.getField()).append("\n");
        }

        if (user.getDescription() != null) {
            sb.append("About: ").append(user.getDescription()).append("\n");
        }

        if (user.getAddress() != null) {
            sb.append("Location: ").append(user.getAddress()).append("\n");
        }

        if (user.getUserSkills() != null && !user.getUserSkills().isEmpty()) {
            String skills = user.getUserSkills().stream()
                    .map(us -> us.getSkill().getName())
                    .collect(Collectors.joining(", "));
            sb.append("Skills: ").append(skills).append("\n");
        }

        if (user.getExperiences() != null && !user.getExperiences().isEmpty()) {
            StringBuilder expBuilder = new StringBuilder();
            for (Experience exp : user.getExperiences()) {
                if (exp.getPosition() != null) {
                    expBuilder.append(exp.getPosition());
                }
                if (exp.getCompany() != null && exp.getCompany().getName() != null) {
                    expBuilder.append(" at ").append(exp.getCompany().getName());
                }
                expBuilder.append("; ");
            }
            sb.append("Experience: ").append(expBuilder).append("\n");
        }

        return sb.toString().trim();
    }

    // ============ Embedding API ============

    private float[] callEmbeddingApi(String text) {
        try {
            Response<Embedding> response = embeddingModel.embed(text);
            return response.content().vector();
        } catch (Exception e) {
            log.error("Error calling embedding API: {}", e.getMessage());
            return null;
        }
    }

    private String vectorToJson(float[] vector) {
        try {
            return objectMapper.writeValueAsString(vector);
        } catch (JsonProcessingException e) {
            log.error("Error converting vector to JSON: {}", e.getMessage());
            return "[]";
        }
    }

    public float[] jsonToVector(String json) {
        try {
            return objectMapper.readValue(json, float[].class);
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON to vector: {}", e.getMessage());
            return new float[0];
        }
    }
}
