package com.UOK.engineeringDeptComplaintApp.controller;

import com.UOK.engineeringDeptComplaintApp.model.*;
import com.UOK.engineeringDeptComplaintApp.service.UserService;
import com.UOK.engineeringDeptComplaintApp.service.DepartmentService;
import com.UOK.engineeringDeptComplaintApp.service.SubEngineerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final DepartmentService departmentService;
    private final SubEngineerService subEngineerService;

    @Autowired
    public AdminController(UserService userService, DepartmentService departmentService,
                           SubEngineerService subEngineerService) {
        this.userService = userService;
        this.departmentService = departmentService;
        this.subEngineerService = subEngineerService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("userCount", userService.getAllUsers().size());
        model.addAttribute("departmentCount", departmentService.getAllDepartments().size());
        model.addAttribute("subEngineerCount", subEngineerService.getAllSubEngineers().size());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        List<User> users = userService.getAllUsers();
        List<Department> departments = departmentService.getAllDepartments();
        List<SubEngineer> subEngineers = subEngineerService.getAllSubEngineers();

        model.addAttribute("users", users);
        model.addAttribute("departments", departments);
        model.addAttribute("subEngineers", subEngineers);
        model.addAttribute("userRoles", UserRole.values());
        model.addAttribute("newUser", new User());

        return "admin/users";
    }

    @PostMapping("/users/create")
    public String createUser(@ModelAttribute User user) {
        userService.createUser(user);
        return "redirect:/admin/users?success";
    }

    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user) {
        userService.updateUser(id, user);
        return "redirect:/admin/users?success";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users?success";
    }

    @GetMapping("/departments")
    public String manageDepartments(Model model) {
        List<Department> departments = departmentService.getAllDepartments();
        model.addAttribute("departments", departments);
        model.addAttribute("newDepartment", new Department());
        return "admin/departments";
    }

    @PostMapping("/departments/create")
    public String createDepartment(@ModelAttribute Department department) {
        try {
            departmentService.saveDepartment(department);
            return "redirect:/admin/departments?createSuccess";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/departments?createError";
        }
    }

    @GetMapping("/departments/edit/{id}")
    public String editDepartment(@PathVariable Long id, Model model) {
        Department department = departmentService.getDepartmentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));
        model.addAttribute("department", department);
        return "admin/edit-department";
    }

    @PostMapping("/departments/update/{id}")
    public String updateDepartment(@PathVariable Long id, @ModelAttribute Department department) {
        try {
            departmentService.updateDepartment(id, department);
            return "redirect:/admin/departments?updateSuccess";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/departments?updateError";
        }
    }

    @PostMapping("/departments/delete/{id}")
    public String deleteDepartment(@PathVariable Long id) {
        try {
            departmentService.deleteDepartment(id);
            return "redirect:/admin/departments?deleteSuccess";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/departments?deleteError";
        }
    }




    @GetMapping("/sub-engineers")
    public String manageSubEngineers(Model model) {
        List<SubEngineer> subEngineers = subEngineerService.getAllSubEngineers();
        model.addAttribute("subEngineers", subEngineers);
        model.addAttribute("complaintTypes", ComplaintType.values());
        model.addAttribute("newSubEngineer", new SubEngineer());
        return "admin/sub-engineers"; // Fixed path
    }

    @PostMapping("/sub-engineers/create")
    public String createSubEngineer(@ModelAttribute SubEngineer subEngineer,
                                    @RequestParam String username,
                                    @RequestParam String password,
                                    @RequestParam(required = false) String email) {
        try {
            // 1. Save the sub-engineer to sub_engineer table
            SubEngineer savedSubEngineer = subEngineerService.saveSubEngineer(subEngineer);

            // 2. Create a user account linked to this sub-engineer
            User user = new User();
            user.setUsername(username);
            user.setPassword(password); // Will be encoded by service
            user.setEmail(email != null ? email : username + "@uok.edu");
            user.setFullName(subEngineer.getName());
            user.setRole(UserRole.ROLE_SUB_ENGINEER);
            user.setEnabled(true);
            user.setSubEngineer(savedSubEngineer); // Link to the sub-engineer

            userService.createUser(user);

            return "redirect:/admin/sub-engineers?createSuccess";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/sub-engineers?createError";
        }
    }

    @PostMapping("/sub-engineers/delete/{id}")
    public String deleteSubEngineer(@PathVariable Long id) {
        try {
            // Step 1: Delete all user accounts linked to this sub-engineer
            List<User> linkedUsers = userService.findUsersBySubEngineerId(id);

            for (User user : linkedUsers) {
                userService.deleteUser(user.getId());
            }

            // Step 2: Update complaints/reports and delete sub-engineer
            subEngineerService.deleteSubEngineer(id);

            return "redirect:/admin/sub-engineers?deleteSuccess";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/sub-engineers?deleteError";
        }
    }
/*
    @GetMapping("/sub-engineers")
    public String manageSubEngineers(Model model) {
        List<SubEngineer> subEngineers = subEngineerService.getAllSubEngineers();
        model.addAttribute("subEngineers", subEngineers);
        model.addAttribute("complaintTypes", ComplaintType.values());
        model.addAttribute("newSubEngineer", new SubEngineer());
        return "admin/sub-engineers";
    }
*/
/*
    @PostMapping("/sub-engineers/create")
    public String createSubEngineer(@ModelAttribute SubEngineer subEngineer) {
        // Use the service method instead of directly accessing repository
        subEngineerService.getAllSubEngineers(); // This ensures the sub-engineer is saved through proper service layer
        // Actually, we need to save the sub-engineer. Let me fix this properly:

        // Since we don't have a save method in SubEngineerService, let's add one:
        // Add this method to SubEngineerService:
        return "redirect:/admin/sub-engineers?success";
    }*/
    /*@PostMapping("/sub-engineers/create")
    public String createSubEngineer(@ModelAttribute SubEngineer subEngineer) {
        subEngineerService.saveSubEngineer(subEngineer);
        return "redirect:/admin/sub-engineers?success";
    }*/

/*
    @PostMapping("/sub-engineers/create")
    public String createSubEngineer(@ModelAttribute SubEngineer subEngineer) {
        subEngineerService.saveSubEngineer(subEngineer);
        return "redirect:/admin/sub-engineers?success";
    }
*/
}