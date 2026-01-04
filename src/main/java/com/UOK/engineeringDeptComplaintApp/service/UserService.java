package com.UOK.engineeringDeptComplaintApp.service;

import com.UOK.engineeringDeptComplaintApp.model.*;
import com.UOK.engineeringDeptComplaintApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentService departmentService;
    private final SubEngineerService subEngineerService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       DepartmentService departmentService, SubEngineerService subEngineerService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.departmentService = departmentService;
        this.subEngineerService = subEngineerService;
    }

    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> findUsersBySubEngineerId(Long subEngineerId) {
        return userRepository.findBySubEngineerId(subEngineerId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setFullName(userDetails.getFullName());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());
        user.setEnabled(userDetails.isEnabled());

        if (userDetails.getRole() == UserRole.ROLE_DEPARTMENT && userDetails.getDepartment() != null) {
            user.setDepartment(userDetails.getDepartment());
        }

        if (userDetails.getRole() == UserRole.ROLE_SUB_ENGINEER && userDetails.getSubEngineer() != null) {
            user.setSubEngineer(userDetails.getSubEngineer());
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}