-- Add isDraft and updatedAt columns to interview_result table
-- This allows saving evaluation drafts and tracking updates

ALTER TABLE interview_result 
ADD COLUMN IF NOT EXISTS is_draft BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE interview_result 
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- Update existing results to be non-draft
UPDATE interview_result 
SET is_draft = FALSE 
WHERE is_draft IS NULL;

-- Add comments
COMMENT ON COLUMN interview_result.is_draft IS 'True if saved as draft, false if submitted';
COMMENT ON COLUMN interview_result.updated_at IS 'Last update timestamp';


