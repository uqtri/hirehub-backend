-- Fix notification_type_check constraint to include all notification types
-- Run this SQL script in your PostgreSQL database

-- Drop the existing constraint
ALTER TABLE notification DROP CONSTRAINT IF EXISTS notification_type_check;

-- Add the updated constraint with all notification types including interview types
ALTER TABLE notification ADD CONSTRAINT notification_type_check 
CHECK (type IN (
    'REQUEST', 
    'MESSAGE', 
    'INTERVIEW', 
    'SYSTEM', 
    'JOB_UPDATE', 
    'APPLICATION_STATUS', 
    'FRIEND_REQUEST', 
    'FRIEND_ACCEPT',
    'INTERVIEW_INVITATION',
    'INTERVIEW_COMPLETED',
    'INTERVIEW_RESULT',
    'INTERVIEW_REMINDER',
    'INTERVIEW_CANCELLED'
));
