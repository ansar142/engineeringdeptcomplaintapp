package com.UOK.engineeringDeptComplaintApp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class SubEngineer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String contact;

    @Enumerated(EnumType.STRING)
    private ComplaintType specialization;

    @OneToMany(mappedBy = "subEngineer")
    private List<Complaint> assignedComplaints;
}