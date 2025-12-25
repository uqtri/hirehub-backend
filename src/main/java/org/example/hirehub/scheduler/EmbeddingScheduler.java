package org.example.hirehub.scheduler;

import org.example.hirehub.entity.Job;
import org.example.hirehub.entity.User;
import org.example.hirehub.repository.JobRepository;
import org.example.hirehub.repository.UserRepository;
import org.example.hirehub.service.EmbeddingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmbeddingScheduler {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingScheduler.class);

    private final EmbeddingService embeddingService;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Value("${embedding.scheduler.enabled:false}")
    private boolean schedulerEnabled;

    @Value("${embedding.scheduler.batch-size:50}")
    private int batchSize;

    public EmbeddingScheduler(EmbeddingService embeddingService,
            JobRepository jobRepository,
            UserRepository userRepository) {
        this.embeddingService = embeddingService;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    /**
     * Regenerate embeddings for all jobs and users
     * Default: every 5 minutes (configurable via embedding.scheduler.cron)
     */
    @Scheduled(cron = "${embedding.scheduler.cron:0 */1 * * * *}")
    public void regenerateEmbeddings() {
        if (!schedulerEnabled) {
            log.debug("Embedding scheduler is disabled");
            return;
        }

        log.info("Starting scheduled embedding regeneration...");

        try {
            regenerateJobEmbeddings();
            regenerateUserEmbeddings();
            log.info("Scheduled embedding regeneration completed");
        } catch (Exception e) {
            log.error("Error during scheduled embedding regeneration: {}", e.getMessage());
        }
    }

    private void regenerateJobEmbeddings() {
        List<Job> jobs = jobRepository.findAllByIsDeletedFalseWithDetails();
        log.info("Regenerating embeddings for {} jobs", jobs.size());

        int count = 0;
        for (Job job : jobs) {
            try {
                // Pass ID instead of entity to avoid session/transaction issues
                embeddingService.generateJobEmbeddingAsync(job.getId());
                count++;

                // Add small delay to avoid rate limiting
                if (count % batchSize == 0) {
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                log.warn("Failed to regenerate embedding for job {}: {}", job.getId(), e.getMessage());
            }
        }
        log.info("Triggered embedding regeneration for {} jobs", count);
    }

    private void regenerateUserEmbeddings() {
        List<User> users = userRepository.findAllByIsDeletedFalseWithDetails();
        log.info("Regenerating embeddings for {} users", users.size());

        int count = 0;
        for (User user : users) {
            try {
                // Pass ID instead of entity to avoid session/transaction issues
                embeddingService.generateUserEmbeddingAsync(user.getId());
                count++;

                // Add small delay to avoid rate limiting
                if (count % batchSize == 0) {
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                log.warn("Failed to regenerate embedding for user {}: {}", user.getId(), e.getMessage());
            }
        }
        log.info("Triggered embedding regeneration for {} users", count);
    }
}
