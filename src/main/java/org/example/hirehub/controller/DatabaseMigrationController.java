package org.example.hirehub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/migration")
public class DatabaseMigrationController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/add-duration-column")
    public ResponseEntity<Map<String, Object>> addDurationMinutesColumn() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if column already exists
            String checkSql = "SELECT column_name FROM information_schema.columns " +
                             "WHERE table_name = 'interview_room' AND column_name = 'duration_minutes'";
            
            var existingColumns = jdbcTemplate.queryForList(checkSql);
            
            if (!existingColumns.isEmpty()) {
                response.put("success", true);
                response.put("message", "Column 'duration_minutes' already exists");
                return ResponseEntity.ok(response);
            }
            
            // Add the column
            String alterSql = "ALTER TABLE interview_room " +
                            "ADD COLUMN duration_minutes INTEGER NOT NULL DEFAULT 60";
            
            jdbcTemplate.execute(alterSql);
            
            response.put("success", true);
            response.put("message", "Column 'duration_minutes' added successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}

