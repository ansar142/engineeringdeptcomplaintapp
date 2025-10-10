package com.UOK.engineeringDeptComplaintApp.config;

import com.UOK.engineeringDeptComplaintApp.model.*;
import com.UOK.engineeringDeptComplaintApp.service.UserService;
import com.UOK.engineeringDeptComplaintApp.service.DepartmentService;
import com.UOK.engineeringDeptComplaintApp.service.SubEngineerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConditionalOnProperty(name = "app.init-default-data", havingValue = "true")
public class DataInitializer {

    @Bean
    public CommandLineRunner initDefaultData(UserService userService,
                                             DepartmentService departmentService,
                                             SubEngineerService subEngineerService) {
        return args -> {
            initDepartments(departmentService);
            initSubEngineers(subEngineerService);
            initUsers(userService, departmentService, subEngineerService);
        };
    }

    private void initDepartments(DepartmentService departmentService) {
        if (departmentService.getAllDepartments().isEmpty()) {
            // Create default departments
            List<Department> departments = List.of(
                    new Department("Computer Science", "cs@uok.edu", "Science Faculty"),
                    new Department("Electrical Engineering", "ee@uok.edu", "Engineering Faculty"),
                    new Department("Civil Engineering", "ce@uok.edu", "Engineering Faculty"),
                    new Department("Mechanical Engineering", "me@uok.edu", "Engineering Faculty"),
                    new Department("Administration", "admin_dept@uok.edu", "Administrative")
            );

            for (Department dept : departments) {
                departmentService.saveDepartment(dept);
            }
            System.out.println("Default departments created");
        }
    }

    private void initSubEngineers(SubEngineerService subEngineerService) {
        if (subEngineerService.getAllSubEngineers().isEmpty()) {
            // Create default sub-engineers
            List<SubEngineer> subEngineers = List.of(
                    createSubEngineer("John Smith", "john.smith@uok.edu", ComplaintType.ELECTRICAL),
                    createSubEngineer("Sarah Johnson", "sarah.j@uok.edu", ComplaintType.CIVIL),
                    createSubEngineer("Mike Davis", "mike.davis@uok.edu", ComplaintType.MECHANICAL),
                    createSubEngineer("Lisa Brown", "lisa.brown@uok.edu", ComplaintType.PUMPING)
            );

            for (SubEngineer se : subEngineers) {
                subEngineerService.saveSubEngineer(se);
            }
            System.out.println("Default sub-engineers created");
        }
    }

    private SubEngineer createSubEngineer(String name, String contact, ComplaintType specialization) {
        SubEngineer se = new SubEngineer();
        se.setName(name);
        se.setContact(contact);
        se.setSpecialization(specialization);
        return se;
    }

    private void initUsers(UserService userService, DepartmentService departmentService, SubEngineerService subEngineerService) {
        // Create Admin user
        if (userService.findByUsername("admin").isEmpty()) {
            User admin = createUser("admin", "admin123", "admin@uok.edu", "System Administrator", UserRole.ROLE_ADMIN, null, null);
            userService.createUser(admin);
            System.out.println("Admin user created: admin/admin123");
        }

        // Create Chief Engineer user
        if (userService.findByUsername("chief").isEmpty()) {
            User chief = createUser("chief", "chief123", "chief.engineer@uok.edu", "Chief Engineer", UserRole.ROLE_CHIEF_ENGINEER, null, null);
            userService.createUser(chief);
            System.out.println("Chief Engineer user created: chief/chief123");
        }

        // Create VC user
        if (userService.findByUsername("vc").isEmpty()) {
            User vc = createUser("vc", "vc123", "vc@uok.edu", "Vice Chancellor", UserRole.ROLE_VC, null, null);
            userService.createUser(vc);
            System.out.println("VC user created: vc/vc123");
        }

        // Create Finance user
        if (userService.findByUsername("finance").isEmpty()) {
            User finance = createUser("finance", "finance123", "finance@uok.edu", "Finance Officer", UserRole.ROLE_FINANCE, null, null);
            userService.createUser(finance);
            System.out.println("Finance user created: finance/finance123");
        }

        // Create Department users
        List<Department> departments = departmentService.getAllDepartments();
        System.out.println(departments.get(0).getName());
        System.out.println(departments.get(1).getName());
        System.out.println(departments.get(2).getName());
        if (!departments.isEmpty() && userService.findByUsername("ee_dept").isEmpty()) {
            User eeDept = createUser("ee_dept", "ee123", "ee.dept@uok.edu", "EE Department", UserRole.ROLE_DEPARTMENT, departments.get(1), null);
            userService.createUser(eeDept);
            System.out.println("ME Department user created: ee_dept/ee123");
        }
        if (!departments.isEmpty() && userService.findByUsername("cs_dept").isEmpty()) {
            User csDept = createUser("cs_dept", "cs123", "cs.dept@uok.edu", "CS Department", UserRole.ROLE_DEPARTMENT, departments.get(0), null);
            userService.createUser(csDept);
            System.out.println("CS Department user created: cs_dept/cs123");
        }

        // Create Sub-Engineer users
        List<SubEngineer> subEngineers = subEngineerService.getAllSubEngineers();
        if (!subEngineers.isEmpty()) {
            if (userService.findByUsername("john_engineer").isEmpty()) {
                User johnEngineer = createUser("john_engineer", "engineer123", "john.engineer@uok.edu", "John Engineer", UserRole.ROLE_SUB_ENGINEER, null, subEngineers.get(0));
                userService.createUser(johnEngineer);
                System.out.println("Sub-Engineer user created: john_engineer/engineer123");
            }

            if (userService.findByUsername("sarah_engineer").isEmpty()) {
                User sarahEngineer = createUser("sarah_engineer", "engineer123", "sarah.engineer@uok.edu", "Sarah Engineer", UserRole.ROLE_SUB_ENGINEER, null, subEngineers.get(1));
                userService.createUser(sarahEngineer);
                System.out.println("Sub-Engineer user created: sarah_engineer/engineer123");
            }
        }
    }

    private User createUser(String username, String password, String email, String fullName,
                            UserRole role, Department department, SubEngineer subEngineer) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // Will be encoded by service
        user.setEmail(email);
        user.setFullName(fullName);
        user.setRole(role);
        user.setEnabled(true);
        user.setDepartment(department);
        user.setSubEngineer(subEngineer);
        return user;
    }
}