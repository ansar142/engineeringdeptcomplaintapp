package com.UOK.engineeringDeptComplaintApp.repository;

import com.UOK.engineeringDeptComplaintApp.model.Report;
import com.UOK.engineeringDeptComplaintApp.model.SubEngineer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubEngineerRepository extends JpaRepository<SubEngineer, Long> {


}