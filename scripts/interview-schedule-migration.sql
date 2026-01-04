-- Migration script for flexible interview scheduling feature
-- Creates tables for interview schedule requests and time slots

-- Create interview_schedule_request table
CREATE TABLE interview_schedule_request (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id BIGINT NOT NULL,
    applicant_id BIGINT NOT NULL,
    recruiter_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    interview_type VARCHAR(20) NOT NULL DEFAULT 'CHAT',
    interview_mode VARCHAR(20) NOT NULL DEFAULT 'LIVE',
    round_number INT NOT NULL DEFAULT 1,
    previous_room_id BIGINT,
    selected_time_slot_id BIGINT,
    request_code VARCHAR(36) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL,
    expires_at DATETIME,
    responded_at DATETIME,
    FOREIGN KEY (job_id) REFERENCES job(id),
    FOREIGN KEY (applicant_id) REFERENCES user(id),
    FOREIGN KEY (recruiter_id) REFERENCES user(id),
    INDEX idx_request_code (request_code),
    INDEX idx_applicant_status (applicant_id, status),
    INDEX idx_recruiter_status (recruiter_id, status),
    INDEX idx_created_at (created_at)
);

-- Create interview_time_slot table
CREATE TABLE interview_time_slot (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    schedule_request_id BIGINT NOT NULL,
    proposed_time DATETIME NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    conflict_reason VARCHAR(255),
    created_at DATETIME NOT NULL,
    FOREIGN KEY (schedule_request_id) REFERENCES interview_schedule_request(id) ON DELETE CASCADE,
    INDEX idx_schedule_request (schedule_request_id),
    INDEX idx_proposed_time (proposed_time)
);
