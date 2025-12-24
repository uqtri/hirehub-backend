package org.example.hirehub.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import org.example.hirehub.entity.Job;
import org.example.hirehub.entity.JobEmbedding;
import org.example.hirehub.entity.User;
import org.example.hirehub.entity.UserEmbedding;
import org.example.hirehub.repository.JobEmbeddingRepository;
import org.example.hirehub.repository.UserEmbeddingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RAGService {

    private static final Logger log = LoggerFactory.getLogger(RAGService.class);
    private static final int TOP_K = 5; // Number of similar items to retrieve

    private final GoogleAiEmbeddingModel embeddingModel;
    private final JobEmbeddingRepository jobEmbeddingRepository;
    private final UserEmbeddingRepository userEmbeddingRepository;
    private final ObjectMapper objectMapper;

    public RAGService(GoogleAiEmbeddingModel embeddingModel,
            JobEmbeddingRepository jobEmbeddingRepository,
            UserEmbeddingRepository userEmbeddingRepository,
            ObjectMapper objectMapper) {
        this.embeddingModel = embeddingModel;
        this.jobEmbeddingRepository = jobEmbeddingRepository;
        this.userEmbeddingRepository = userEmbeddingRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Search for similar jobs based on user query
     */
    public List<Job> searchSimilarJobs(String query, int topK) {
        try {
            // Generate embedding for the query
            float[] queryVector = embeddingModel.embed(query).content().vector();

            // Get all job embeddings
            List<JobEmbedding> allEmbeddings = jobEmbeddingRepository.findAllByEmbeddingIsNotNull();

            // Calculate similarity and sort
            List<ScoredJob> scoredJobs = new ArrayList<>();
            for (JobEmbedding jobEmbedding : allEmbeddings) {
                float[] jobVector = jsonToVector(jobEmbedding.getEmbedding());
                if (jobVector.length > 0) {
                    double similarity = cosineSimilarity(queryVector, jobVector);
                    scoredJobs.add(new ScoredJob(jobEmbedding.getJob(), similarity));
                }
            }

            // Sort by similarity (descending) and return top K
            return scoredJobs.stream()
                    .sorted(Comparator.comparingDouble(ScoredJob::score).reversed())
                    .limit(topK)
                    .map(ScoredJob::job)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error searching similar jobs: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Search for similar users based on query
     */
    public List<User> searchSimilarUsers(String query, int topK) {
        try {
            float[] queryVector = embeddingModel.embed(query).content().vector();

            List<UserEmbedding> allEmbeddings = userEmbeddingRepository.findAllByEmbeddingIsNotNull();

            List<ScoredUser> scoredUsers = new ArrayList<>();
            for (UserEmbedding userEmbedding : allEmbeddings) {
                float[] userVector = jsonToVector(userEmbedding.getEmbedding());
                if (userVector.length > 0) {
                    double similarity = cosineSimilarity(queryVector, userVector);
                    scoredUsers.add(new ScoredUser(userEmbedding.getUser(), similarity));
                }
            }

            return scoredUsers.stream()
                    .sorted(Comparator.comparingDouble(ScoredUser::score).reversed())
                    .limit(topK)
                    .map(ScoredUser::user)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error searching similar users: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Build RAG context from similar jobs for the chatbot
     */
    public String buildJobContext(String query) {
        List<Job> similarJobs = searchSimilarJobs(query, TOP_K);

        if (similarJobs.isEmpty()) {
            return "";
        }

        StringBuilder context = new StringBuilder();
        context.append("\n\n=== THÔNG TIN VIỆC LÀM LIÊN QUAN ===\n");
        context.append("Dưới đây là một số việc làm phù hợp với yêu cầu của người dùng:\n\n");

        for (int i = 0; i < similarJobs.size(); i++) {
            Job job = similarJobs.get(i);
            context.append(String.format("**%d. %s**\n", i + 1, job.getTitle()));

            if (job.getRecruiter() != null && job.getRecruiter().getName() != null) {
                context.append("   - Công ty: ").append(job.getRecruiter().getName()).append("\n");
            }
            if (job.getLevel() != null) {
                context.append("   - Level: ").append(job.getLevel()).append("\n");
            }
            if (job.getAddress() != null) {
                context.append("   - Địa điểm: ").append(job.getAddress()).append("\n");
            }
            if (job.getWorkspace() != null) {
                context.append("   - Hình thức: ").append(job.getWorkspace()).append("\n");
            }
            if (job.getSkills() != null && !job.getSkills().isEmpty()) {
                String skills = job.getSkills().stream()
                        .map(js -> js.getSkill().getName())
                        .collect(Collectors.joining(", "));
                context.append("   - Skills: ").append(skills).append("\n");
            }
            if (job.getDescription() != null) {
                String shortDesc = job.getDescription().length() > 200
                        ? job.getDescription().substring(0, 200) + "..."
                        : job.getDescription();
                context.append("   - Mô tả: ").append(shortDesc).append("\n");
            }
            context.append("   - ID: ").append(job.getId()).append("\n\n");
        }

        context.append("=== KẾT THÚC THÔNG TIN ===\n\n");
        context.append("Hãy sử dụng thông tin trên để trả lời câu hỏi của người dùng. ");
        context.append("Nếu người dùng hỏi về việc làm, hãy giới thiệu các công việc phù hợp từ danh sách trên.\n");

        return context.toString();
    }

    /**
     * Build RAG context from similar users/candidates for the chatbot
     */
    public String buildUserContext(String query) {
        List<User> similarUsers = searchSimilarUsers(query, TOP_K);

        if (similarUsers.isEmpty()) {
            return "";
        }

        StringBuilder context = new StringBuilder();
        context.append("\n\n=== THÔNG TIN ỨNG VIÊN LIÊN QUAN ===\n");
        context.append("Dưới đây là một số ứng viên phù hợp với yêu cầu:\n\n");

        for (int i = 0; i < similarUsers.size(); i++) {
            User user = similarUsers.get(i);
            context.append(String.format("**%d. %s**\n", i + 1, user.getName()));

            if (user.getEmail() != null) {
                context.append("   - Email: ").append(user.getEmail()).append("\n");
            }
            if (user.getUserSkills() != null && !user.getUserSkills().isEmpty()) {
                String skills = user.getUserSkills().stream()
                        .map(us -> us.getSkill().getName())
                        .collect(Collectors.joining(", "));
                context.append("   - Skills: ").append(skills).append("\n");
            }
            if (user.getExperiences() != null && !user.getExperiences().isEmpty()) {
                context.append("   - Kinh nghiệm: ");
                user.getExperiences().forEach(exp -> {
                    context.append(exp.getPosition());
                    if (exp.getCompany() != null) {
                        context.append(" tại ").append(exp.getCompany().getName());
                    }
                    context.append("; ");
                });
                context.append("\n");
            }
            if (user.getStudies() != null && !user.getStudies().isEmpty()) {
                context.append("   - Học vấn: ");
                user.getStudies().forEach(study -> {
                    if (study.getUniversity() != null) {
                        context.append(study.getUniversity().getName());
                    }
                    if (study.getMajor() != null) {
                        context.append(" - ").append(study.getMajor());
                    }
                    context.append("; ");
                });
                context.append("\n");
            }
            context.append("   - ID: ").append(user.getId()).append("\n\n");
        }

        context.append("=== KẾT THÚC THÔNG TIN ===\n\n");
        context.append("Hãy sử dụng thông tin trên để giới thiệu các ứng viên phù hợp.\n");

        return context.toString();
    }

    /**
     * Build combined context based on query type (auto-detect)
     */
    public String buildContext(String query, boolean includeJobs, boolean includeUsers) {
        StringBuilder context = new StringBuilder();

        if (includeJobs) {
            String jobContext = buildJobContext(query);
            context.append(jobContext);
        }

        if (includeUsers) {
            String userContext = buildUserContext(query);
            context.append(userContext);
        }

        return context.toString();
    }

    /**
     * Calculate cosine similarity between two vectors
     */
    private double cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private float[] jsonToVector(String json) {
        try {
            return objectMapper.readValue(json, float[].class);
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON to vector: {}", e.getMessage());
            return new float[0];
        }
    }

    // Record classes for sorting
    private record ScoredJob(Job job, double score) {
    }

    private record ScoredUser(User user, double score) {
    }
}
