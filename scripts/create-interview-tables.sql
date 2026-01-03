-- Interview Room System Database Migration Script
-- Run this script to create all necessary tables for the interview room feature

-- Create interview_room table
CREATE TABLE IF NOT EXISTS interview_room (
    id BIGSERIAL PRIMARY KEY,
    job_id BIGINT NOT NULL,
    applicant_id BIGINT NOT NULL,
    recruiter_id BIGINT NOT NULL,
    room_code VARCHAR(36) UNIQUE NOT NULL,
    scheduled_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    started_at TIMESTAMP,
    ended_at TIMESTAMP,
    
    CONSTRAINT fk_interview_room_job FOREIGN KEY (job_id) REFERENCES job(id) ON DELETE CASCADE,
    CONSTRAINT fk_interview_room_applicant FOREIGN KEY (applicant_id) REFERENCES "user"(id) ON DELETE CASCADE,
    CONSTRAINT fk_interview_room_recruiter FOREIGN KEY (recruiter_id) REFERENCES "user"(id) ON DELETE CASCADE,
    CONSTRAINT chk_interview_room_status CHECK (status IN ('SCHEDULED', 'ONGOING', 'FINISHED', 'CANCELLED'))
);

-- Create interview_message table
CREATE TABLE IF NOT EXISTS interview_message (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    sender_role VARCHAR(20) NOT NULL,
    type VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
    
    CONSTRAINT fk_interview_message_room FOREIGN KEY (room_id) REFERENCES interview_room(id) ON DELETE CASCADE,
    CONSTRAINT fk_interview_message_sender FOREIGN KEY (sender_id) REFERENCES "user"(id) ON DELETE CASCADE,
    CONSTRAINT chk_interview_message_sender_role CHECK (sender_role IN ('RECRUITER', 'APPLICANT', 'SYSTEM')),
    CONSTRAINT chk_interview_message_type CHECK (type IN ('CHAT', 'QUESTION', 'SYSTEM'))
);

-- Create interview_result table
CREATE TABLE IF NOT EXISTS interview_result (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT UNIQUE NOT NULL,
    score INTEGER NOT NULL,
    comment TEXT,
    private_notes TEXT,
    recommendation VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    
    CONSTRAINT fk_interview_result_room FOREIGN KEY (room_id) REFERENCES interview_room(id) ON DELETE CASCADE,
    CONSTRAINT chk_interview_result_score CHECK (score >= 1 AND score <= 10),
    CONSTRAINT chk_interview_result_recommendation CHECK (recommendation IN ('PASS', 'FAIL'))
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_interview_room_room_code ON interview_room(room_code);
CREATE INDEX IF NOT EXISTS idx_interview_room_recruiter ON interview_room(recruiter_id);
CREATE INDEX IF NOT EXISTS idx_interview_room_applicant ON interview_room(applicant_id);
CREATE INDEX IF NOT EXISTS idx_interview_room_job ON interview_room(job_id);
CREATE INDEX IF NOT EXISTS idx_interview_room_status ON interview_room(status);
CREATE INDEX IF NOT EXISTS idx_interview_room_scheduled_time ON interview_room(scheduled_time);

CREATE INDEX IF NOT EXISTS idx_interview_message_room ON interview_message(room_id);
CREATE INDEX IF NOT EXISTS idx_interview_message_sender ON interview_message(sender_id);
CREATE INDEX IF NOT EXISTS idx_interview_message_timestamp ON interview_message(timestamp);
CREATE INDEX IF NOT EXISTS idx_interview_message_type ON interview_message(type);

CREATE INDEX IF NOT EXISTS idx_interview_result_room ON interview_result(room_id);

-- Add comments for documentation
COMMENT ON TABLE interview_room IS 'Stores interview room information for scheduled interviews between recruiters and applicants';
COMMENT ON TABLE interview_message IS 'Stores all messages (chat, questions, system) exchanged during interviews';
COMMENT ON TABLE interview_result IS 'Stores interview evaluation results submitted by recruiters';

COMMENT ON COLUMN interview_room.room_code IS 'Unique UUID identifier for accessing the interview room';
COMMENT ON COLUMN interview_room.status IS 'Current status: SCHEDULED, ONGOING, FINISHED, or CANCELLED';
COMMENT ON COLUMN interview_message.sender_role IS 'Role of message sender: RECRUITER, APPLICANT, or SYSTEM';
COMMENT ON COLUMN interview_message.type IS 'Message type: CHAT (regular message), QUESTION (interview question), or SYSTEM (system notification)';
COMMENT ON COLUMN interview_result.score IS 'Interview score from 1 to 10';
COMMENT ON COLUMN interview_result.recommendation IS 'Hiring recommendation: PASS or FAIL';
COMMENT ON COLUMN interview_result.private_notes IS 'Internal notes visible only to recruiters';

-- Grant necessary permissions (adjust based on your database user setup)
-- GRANT SELECT, INSERT, UPDATE, DELETE ON interview_room TO hirehub_app;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON interview_message TO hirehub_app;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON interview_result TO hirehub_app;
-- GRANT USAGE, SELECT ON SEQUENCE interview_room_id_seq TO hirehub_app;
-- GRANT USAGE, SELECT ON SEQUENCE interview_message_id_seq TO hirehub_app;
-- GRANT USAGE, SELECT ON SEQUENCE interview_result_id_seq TO hirehub_app;

-- Verification queries (optional - run after migration)
-- SELECT COUNT(*) as interview_rooms FROM interview_room;
-- SELECT COUNT(*) as interview_messages FROM interview_message;
-- SELECT COUNT(*) as interview_results FROM interview_result;

-- Sample data for testing (optional - remove in production)
-- INSERT INTO interview_room (job_id, applicant_id, recruiter_id, room_code, scheduled_time)
-- VALUES (1, 2, 3, 'test-room-' || gen_random_uuid(), NOW() + INTERVAL '1 day');

COMMIT;

