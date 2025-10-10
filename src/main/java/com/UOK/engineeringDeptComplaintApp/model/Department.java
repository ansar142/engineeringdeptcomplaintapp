package com.UOK.engineeringDeptComplaintApp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String contact;
    private String otherMetadata;

    public Department(String name, String contact, String otherMetadata) {
        this.name = name;
        this.contact = contact;
        this.otherMetadata = otherMetadata;
    }
    public Department(Long id ,String name, String contact, String otherMetadata) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.otherMetadata = otherMetadata;
    }
}