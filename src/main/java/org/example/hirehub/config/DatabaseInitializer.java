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
            // Check and add duration_minutes column
            addColumnIfNotExists(
                "interview_room",
                "duration_minutes",
                "ALTER TABLE interview_room ADD COLUMN duration_minutes INTEGER NOT NULL DEFAULT 60",
                "duration_minutes"
            );
            
            // Check and add evaluation column to interview_question
            addColumnIfNotExists(
                "interview_question",
                "evaluation",
                "ALTER TABLE interview_question ADD COLUMN evaluation VARCHAR(20)",
                "evaluation"
            );
            
        } catch (Exception e) {
            System.err.println("⚠️ Error in database initialization: " + e.getMessage());
            // Don't throw exception to prevent application startup failure
        }
    }
    
    private void addColumnIfNotExists(String tableName, String columnName, String alterSql, String displayName) {
        try {
            String checkSql = "SELECT column_name FROM information_schema.columns " +
                             "WHERE table_name = ? AND column_name = ?";
            
            var existingColumns = jdbcTemplate.queryForList(checkSql, tableName, columnName);
            
            if (existingColumns.isEmpty()) {
                System.out.println("⚙️ Adding missing column '" + displayName + "' to " + tableName + " table...");
                jdbcTemplate.execute(alterSql);
                System.out.println("✅ Column '" + displayName + "' added successfully!");
            } else {
                System.out.println("✅ Column '" + displayName + "' already exists.");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error checking/adding " + displayName + " column: " + e.getMessage());
        }
    }
}

