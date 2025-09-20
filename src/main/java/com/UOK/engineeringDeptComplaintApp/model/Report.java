package com.UOK.engineeringDeptComplaintApp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String details;
    private String imagePath;
    private LocalDateTime inspectionDate;

    // One-to-one relationship with Complaint (bi-directional)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id")
    private Complaint complaint;

    // Many-to-one relationship with SubEngineer (uni-directional for simplicity)
    @ManyToOne
    @JoinColumn(name = "sub_engineer_id")
    private SubEngineer subEngineer;
}