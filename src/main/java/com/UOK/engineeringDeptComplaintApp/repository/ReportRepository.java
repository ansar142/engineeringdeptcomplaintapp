package com.UOK.engineeringDeptComplaintApp.repository;

import com.UOK.engineeringDeptComplaintApp.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    // Find reports based on the status of their associated complaint
    List<Report> findByComplaintStatus(com.UOK.engineeringDeptComplaintApp.model.ComplaintStatus status);
}