-- ============================================
-- CLEANUP SCRIPT: REMOVE ALL LOOKUP DATA
-- WARNING: This will delete all data from lookup tables
-- ============================================

-- Soft delete (set is_deleted = true)
UPDATE company_domain SET is_deleted = true WHERE is_deleted = false;
UPDATE job_level SET is_deleted = true WHERE is_deleted = false;
UPDATE work_type SET is_deleted = true WHERE is_deleted = false;
UPDATE job_type SET is_deleted = true WHERE is_deleted = false;

-- OR Hard delete (permanently remove)
-- DELETE FROM company_domain;
-- DELETE FROM job_level;
-- DELETE FROM work_type;
-- DELETE FROM job_type;

-- Verification
SELECT 'Active Company Domains:' as info, COUNT(*) as count FROM company_domain WHERE is_deleted = false
UNION ALL
SELECT 'Active Job Levels:', COUNT(*) FROM job_level WHERE is_deleted = false
UNION ALL
SELECT 'Active Work Types:', COUNT(*) FROM work_type WHERE is_deleted = false
UNION ALL
SELECT 'Active Job Types:', COUNT(*) FROM job_type WHERE is_deleted = false;




