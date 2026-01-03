-- Extended Interview Room System - Migration Script for New Features
-- Run this script AFTER the initial interview tables migration

-- Add new columns to interview_room table
ALTER TABLE interview_room
ADD COLUMN IF NOT EXISTS interview_type VARCHAR(20) NOT NULL DEFAULT 'CHAT',
ADD COLUMN IF NOT EXISTS interview_mode VARCHAR(20) NOT NULL DEFAULT 'LIVE',
ADD COLUMN IF NOT EXISTS round_number INTEGER NOT NULL DEFAULT 1,
ADD COLUMN IF NOT EXISTS previous_room_id BIGINT,
ADD COLUMN IF NOT EXISTS email_sent BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS notification_sent BOOLEAN NOT NULL DEFAULT FALSE;

-- Add check constraints
ALTER TABLE interview_room
ADD CONSTRAINT chk_interview_room_interview_type CHECK (interview_type IN ('CHAT', 'VIDEO'));

ALTER TABLE interview_room
ADD CONSTRAINT chk_interview_room_interview_mode CHECK (interview_mode IN ('LIVE', 'ASYNC'));

-- Add foreign key for previous_room_id
ALTER TABLE interview_room
ADD CONSTRAINT fk_interview_room_previous FOREIGN KEY (previous_room_id) 
    REFERENCES interview_room(id) ON DELETE SET NULL;

-- Create question_bank table
CREATE TABLE IF NOT EXISTS question_bank (
    id BIGSERIAL PRIMARY KEY,
    recruiter_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    
    CONSTRAINT fk_question_bank_recruiter FOREIGN KEY (recruiter_id) 
        REFERENCES "user"(id) ON DELETE CASCADE
);

-- Create question table
CREATE TABLE IF NOT EXISTS question (
    id BIGSERIAL PRIMARY KEY,
    question_bank_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    order_index INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    
    CONSTRAINT fk_question_question_bank FOREIGN KEY (question_bank_id) 
        REFERENCES question_bank(id) ON DELETE CASCADE
);

-- Create interview_question table (for async interviews)
CREATE TABLE IF NOT EXISTS interview_question (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL,
    question_id BIGINT,
    question_content TEXT NOT NULL,
    answer TEXT,
    order_index INTEGER NOT NULL DEFAULT 0,
    asked_at TIMESTAMP NOT NULL DEFAULT NOW(),
    answered_at TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    
    CONSTRAINT fk_interview_question_room FOREIGN KEY (room_id) 
        REFERENCES interview_room(id) ON DELETE CASCADE,
    CONSTRAINT fk_interview_question_question FOREIGN KEY (question_id) 
        REFERENCES question(id) ON DELETE SET NULL,
    CONSTRAINT chk_interview_question_status CHECK (status IN ('PENDING', 'ANSWERED'))
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_question_bank_recruiter ON question_bank(recruiter_id);
CREATE INDEX IF NOT EXISTS idx_question_bank_category ON question_bank(category);
CREATE INDEX IF NOT EXISTS idx_question_bank_is_deleted ON question_bank(is_deleted);

CREATE INDEX IF NOT EXISTS idx_question_question_bank ON question(question_bank_id);
CREATE INDEX IF NOT EXISTS idx_question_order ON question(order_index);
CREATE INDEX IF NOT EXISTS idx_question_is_deleted ON question(is_deleted);

CREATE INDEX IF NOT EXISTS idx_interview_question_room ON interview_question(room_id);
CREATE INDEX IF NOT EXISTS idx_interview_question_status ON interview_question(status);
CREATE INDEX IF NOT EXISTS idx_interview_question_order ON interview_question(order_index);

CREATE INDEX IF NOT EXISTS idx_interview_room_type ON interview_room(interview_type);
CREATE INDEX IF NOT EXISTS idx_interview_room_mode ON interview_room(interview_mode);
CREATE INDEX IF NOT EXISTS idx_interview_room_round ON interview_room(round_number);
CREATE INDEX IF NOT EXISTS idx_interview_room_previous ON interview_room(previous_room_id);

-- Add comments
COMMENT ON TABLE question_bank IS 'Stores question banks created by recruiters for reuse in interviews';
COMMENT ON TABLE question IS 'Stores individual questions within a question bank';
COMMENT ON TABLE interview_question IS 'Stores questions asked in a specific interview room with applicant answers';

COMMENT ON COLUMN interview_room.interview_type IS 'Type of interview: CHAT or VIDEO';
COMMENT ON COLUMN interview_room.interview_mode IS 'Mode: LIVE (recruiter present) or ASYNC (automated)';
COMMENT ON COLUMN interview_room.round_number IS 'Which round of interview this is (1, 2, 3, etc.)';
COMMENT ON COLUMN interview_room.previous_room_id IS 'ID of previous interview room if this is a follow-up round';

COMMENT ON COLUMN interview_question.status IS 'Status: PENDING (not answered) or ANSWERED';
COMMENT ON COLUMN interview_question.answer IS 'Applicant answer to the question';

-- Sample data for testing (optional - remove in production)
-- INSERT INTO question_bank (recruiter_id, title, description, category)
-- VALUES (1, 'Technical Interview Questions', 'Questions for backend developer position', 'TECHNICAL');

-- INSERT INTO question (question_bank_id, content, order_index)
-- VALUES 
--     (1, 'Explain the difference between SQL and NoSQL databases', 0),
--     (1, 'What is RESTful API and how does it work?', 1),
--     (1, 'Describe your experience with Spring Boot', 2);

COMMIT;

