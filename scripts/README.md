# Database Scripts - Lookup Tables

## Overview
Scripts để quản lý dữ liệu cho các bảng lookup (CompanyDomain, JobLevel, WorkType, JobType).

## Files

### 1. `insert-lookup-data.sql`
**Mục đích**: Insert dữ liệu ban đầu vào các bảng lookup

**Cách sử dụng**:
```bash
# Option 1: Chạy qua psql
psql -U your_username -d hirehub_db -f scripts/insert-lookup-data.sql

# Option 2: Chạy qua Spring Boot (thêm vào application.properties)
spring.jpa.defer-datasource-initialization=true
spring.sql.init.mode=always
spring.sql.init.data-locations=classpath:data-init.sql
```

**Dữ liệu được insert**:
- **Company Domains (15 items)**: Công nghệ thông tin, Tài chính, Y tế, Giáo dục...
- **Job Levels (8 items)**: Intern, Fresher, Junior, Middle, Senior, Lead, Manager, Director
- **Work Types (3 items)**: Remote, Onsite, Hybrid
- **Job Types (5 items)**: Full-time, Part-time, Contract, Internship, Freelance

### 2. `cleanup-lookup-data.sql`
**Mục đích**: Xóa dữ liệu từ các bảng lookup (soft delete hoặc hard delete)

**Cách sử dụng**:
```bash
psql -U your_username -d hirehub_db -f scripts/cleanup-lookup-data.sql
```

**Lưu ý**: 
- Mặc định sử dụng soft delete (set `is_deleted = true`)
- Uncomment phần "Hard delete" nếu muốn xóa vĩnh viễn

## Data Details

### Company Domains (Lĩnh vực công ty)
```
1. Công nghệ thông tin
2. Tài chính – Ngân hàng
3. Sản xuất & Chế tạo
4. Xây dựng & Bất động sản
5. Vận tải & Logistics
6. Du lịch & Dịch vụ
7. Y tế & Chăm sóc sức khỏe
8. Giáo dục & Đào tạo
9. Truyền thông & Marketing
10. Năng lượng & Môi trường
11. Bán lẻ & Thương mại điện tử
12. Khách sạn & Nhà hàng
13. Pháp lý & Tư vấn
14. Nông nghiệp & Thủy sản
15. Khác
```

### Job Levels (Cấp độ)
```
1. Intern (Thực tập sinh)
2. Fresher (Mới ra trường)
3. Junior (1-2 năm kinh nghiệm)
4. Middle (3-5 năm kinh nghiệm)
5. Senior (5+ năm kinh nghiệm)
6. Lead (Team Lead)
7. Manager (Quản lý)
8. Director (Giám đốc)
```

### Work Types (Hình thức làm việc)
```
1. Remote (Từ xa)
2. Onsite (Tại văn phòng)
3. Hybrid (Kết hợp)
```

### Job Types (Loại hình công việc)
```
1. Full-time (Toàn thời gian)
2. Part-time (Bán thời gian)
3. Contract (Hợp đồng)
4. Internship (Thực tập)
5. Freelance (Tự do)
```

## Quick Commands

### Check current data:
```sql
SELECT * FROM company_domain WHERE is_deleted = false;
SELECT * FROM job_level WHERE is_deleted = false;
SELECT * FROM work_type WHERE is_deleted = false;
SELECT * FROM job_type WHERE is_deleted = false;
```

### Count records:
```sql
SELECT COUNT(*) FROM company_domain WHERE is_deleted = false;
SELECT COUNT(*) FROM job_level WHERE is_deleted = false;
SELECT COUNT(*) FROM work_type WHERE is_deleted = false;
SELECT COUNT(*) FROM job_type WHERE is_deleted = false;
```

### Add new item (example):
```sql
INSERT INTO company_domain (domain, is_deleted, created_at) 
VALUES ('Viễn thông', false, NOW());
```

## Notes
- Tất cả các bảng đều có cột `is_deleted` cho soft delete
- Sử dụng `ON CONFLICT DO NOTHING` để tránh duplicate entries
- Có thể chỉnh sửa dữ liệu qua Admin UI sau khi insert

