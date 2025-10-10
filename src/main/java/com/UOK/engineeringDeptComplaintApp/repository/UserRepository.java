package com.UOK.engineeringDeptComplaintApp.repository;

import com.UOK.engineeringDeptComplaintApp.model.User;
import com.UOK.engineeringDeptComplaintApp.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByRole(UserRole role);
    boolean existsByUsername(String username);
    List<User> findByDepartmentId(Long departmentId);
    @Query("SELECT u FROM User u WHERE u.subEngineer.id = :subEngineerId")
    List<User> findBySubEngineerId(@Param("subEngineerId") Long subEngineerId);
}