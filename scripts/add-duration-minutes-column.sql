-- Add duration_minutes column to interview_room table
-- This column stores the interview duration in minutes (default 60)

ALTER TABLE interview_room 
ADD COLUMN IF NOT EXISTS duration_minutes INTEGER NOT NULL DEFAULT 60;

-- Update existing rooms to have default duration
UPDATE interview_room 
SET duration_minutes = 60 
WHERE duration_minutes IS NULL;

-- Add comment
COMMENT ON COLUMN interview_room.duration_minutes IS 'Interview duration in minutes (default 60)';

