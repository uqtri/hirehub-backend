-- ============================================
-- INSERT DATA FOR LOOKUP TABLES
-- ============================================

-- 1. Company Domain (Lĩnh vực công ty)
INSERT INTO company_domain (domain, is_deleted, created_at) VALUES
('Công nghệ thông tin', false, NOW()),
('Tài chính – Ngân hàng', false, NOW()),
('Sản xuất & Chế tạo', false, NOW()),
('Xây dựng & Bất động sản', false, NOW()),
('Vận tải & Logistics', false, NOW()),
('Du lịch & Dịch vụ', false, NOW()),
('Y tế & Chăm sóc sức khỏe', false, NOW()),
('Giáo dục & Đào tạo', false, NOW()),
('Truyền thông & Marketing', false, NOW()),
('Năng lượng & Môi trường', false, NOW()),
('Bán lẻ & Thương mại điện tử', false, NOW()),
('Khách sạn & Nhà hàng', false, NOW()),
('Pháp lý & Tư vấn', false, NOW()),
('Nông nghiệp & Thủy sản', false, NOW()),
('Khác', false, NOW());

-- 2. Job Level (Cấp độ công việc)
INSERT INTO job_level (level, is_deleted, created_at) VALUES
('Intern', false, NOW()),
('Fresher', false, NOW()),
('Junior', false, NOW()),
('Middle', false, NOW()),
('Senior', false, NOW()),
('Lead', false, NOW()),
('Manager', false, NOW()),
('Director', false, NOW());

-- 3. Work Type (Hình thức làm việc)
INSERT INTO work_type (workspace, is_deleted, created_at) VALUES
('Remote', false, NOW()),
('Onsite', false, NOW()),
('Hybrid', false, NOW());

-- 4. Job Type (Loại hình công việc)
INSERT INTO job_type (type, is_deleted, created_at) VALUES
('Full-time', false, NOW()),
('Part-time', false, NOW()),
('Contract', false, NOW()),
('Internship', false, NOW()),
('Freelance', false, NOW());

-- ============================================
-- VERIFICATION QUERIES (Optional - for testing)
-- ============================================

-- SELECT * FROM company_domain WHERE is_deleted = false;
-- SELECT * FROM job_level WHERE is_deleted = false;
-- SELECT * FROM work_type WHERE is_deleted = false;
-- SELECT * FROM job_type WHERE is_deleted = false;






