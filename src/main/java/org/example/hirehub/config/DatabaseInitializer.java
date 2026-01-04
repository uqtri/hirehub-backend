package org.example.hirehub.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements InitializingBean {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            // Check if duration_minutes column exists
            String checkSql = "SELECT column_name FROM information_schema.columns " +
                             "WHERE table_name = 'interview_room' AND column_name = 'duration_minutes'";
            
            var existingColumns = jdbcTemplate.queryForList(checkSql);
            
            if (existingColumns.isEmpty()) {
                // Add the column if it doesn't exist
                System.out.println("⚙️ Adding missing column 'duration_minutes' to interview_room table...");
                
                String alterSql = "ALTER TABLE interview_room " +
                                "ADD COLUMN duration_minutes INTEGER NOT NULL DEFAULT 60";
                
                jdbcTemplate.execute(alterSql);
                
                System.out.println("✅ Column 'duration_minutes' added successfully!");
            } else {
                System.out.println("✅ Column 'duration_minutes' already exists.");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error checking/adding duration_minutes column: " + e.getMessage());
            // Don't throw exception to prevent application startup failure
        }
    }
}

