package com.UOK.engineeringDeptComplaintApp.repository;

import com.UOK.engineeringDeptComplaintApp.model.Complaint;
import com.UOK.engineeringDeptComplaintApp.model.SubEngineer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    // Find all complaints assigned to a specific Sub-Engineer
    List<Complaint> findBySubEngineer(SubEngineer subEngineer);
    List<Complaint> findByDepartmentId(Long departmentId);
}