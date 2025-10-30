package org.example.hirehub.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Report {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long resourceId;
    String resourceName;
//    Object resource;
    String reason;
    String status = "pending";
    @ManyToOne
    User reporter;
}
