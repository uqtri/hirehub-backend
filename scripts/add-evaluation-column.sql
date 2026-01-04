-- Add evaluation column to interview_question table
-- Allows recruiter to evaluate each question answer as PASS or FAIL

ALTER TABLE interview_question 
ADD COLUMN IF NOT EXISTS evaluation VARCHAR(20);

-- Add check constraint for evaluation values
ALTER TABLE interview_question
ADD CONSTRAINT IF NOT EXISTS chk_interview_question_evaluation 
CHECK (evaluation IS NULL OR evaluation IN ('PASS', 'FAIL'));

-- Add comment
COMMENT ON COLUMN interview_question.evaluation IS 'Evaluation result: PASS, FAIL, or NULL (not evaluated yet)';

