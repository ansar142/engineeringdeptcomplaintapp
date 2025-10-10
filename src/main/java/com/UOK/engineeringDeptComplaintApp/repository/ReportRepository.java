package com.UOK.engineeringDeptComplaintApp.repository;

import com.UOK.engineeringDeptComplaintApp.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    // Find reports based on the status of their associated complaint
    List<Report> findByComplaintStatus(com.UOK.engineeringDeptComplaintApp.model.ComplaintStatus status);

    Optional<Report> findByComplaintId(Long complaintId);
    @Query("SELECT r FROM Report r WHERE r.subEngineer.id = :subEngineerId")
    List<Report> findBySubEngineerId(@Param("subEngineerId") Long subEngineerId);

}