package com.UOK.engineeringDeptComplaintApp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dateRegistered;

    @Enumerated(EnumType.STRING)
    private ComplaintType type;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus status;

    // Many-to-one relationship with Department (the registrar)
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    // Many-to-one relationship with the assigned SubEngineer
    @ManyToOne
    @JoinColumn(name = "sub_engineer_id")
    private SubEngineer subEngineer;

    // One-to-one relationship with Report (bi-directional)
    @OneToOne(mappedBy = "complaint", cascade = CascadeType.ALL)
    private Report report;

    // Constructor for registering a new complaint
    public Complaint(String title, String description, ComplaintType type, Department department) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.department = department;
        this.dateRegistered = LocalDateTime.now();
        this.status = ComplaintStatus.REGISTERED;
    }
}