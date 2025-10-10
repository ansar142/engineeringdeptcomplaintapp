package com.UOK.engineeringDeptComplaintApp.controller;

import com.UOK.engineeringDeptComplaintApp.model.Complaint;
import com.UOK.engineeringDeptComplaintApp.model.Department;
import com.UOK.engineeringDeptComplaintApp.model.SubEngineer;
import com.UOK.engineeringDeptComplaintApp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;
    private final DepartmentService departmentService;
    private final SubEngineerService subEngineerService;

    @Autowired
    public ComplaintController(ComplaintService complaintService, DepartmentService departmentService, SubEngineerService subEngineerService, UserService userService,
                               AuthenticationService authenticationService) {
        this.complaintService = complaintService;
        this.departmentService = departmentService;
        this.subEngineerService = subEngineerService;

    }
    // Chief Engineer: Displays the detail of a single complaint
    @GetMapping("/chief/{id}")
    public String chiefViewComplaintDetail(@PathVariable Long id, Model model) {
        Complaint complaint = complaintService.getComplaintById(id)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));

        List<SubEngineer> subEngineers = subEngineerService.getAllSubEngineers();

        model.addAttribute("complaint", complaint);
        model.addAttribute("subEngineers", subEngineers);
        return "chief/complaints/detail";
    }

    // Chief Engineer: Handles the delegation of a complaint
    @PostMapping("/chief/delegate")
    public String delegateComplaint(@RequestParam("complaintId") Long complaintId,
                                    @RequestParam("subEngineerId") Long subEngineerId) {
        complaintService.delegateComplaint(complaintId, subEngineerId);
        return "redirect:/complaints/chief/" + complaintId;
    }

    // Chief Engineer: Handles forwarding a report to Finance
    @PostMapping("/chief/forward-to-finance")
    public String forwardToFinance(@RequestParam("complaintId") Long complaintId) {
        complaintService.forwardToFinance(complaintId);
        return "redirect:/complaints/chief/" + complaintId;
    }

    // Displays the complaint registration form (for a Department user)
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        List<Department> departments = departmentService.getAllDepartments();
        model.addAttribute("complaint", new Complaint());
        model.addAttribute("departments", departments);
        return "departments/complaints/form";
    }

    // Handles the submission of a new complaint
    @PostMapping("/register")
    public String registerComplaint(@ModelAttribute Complaint complaint, Model model) {
        Complaint savedComplaint = complaintService.registerComplaint(complaint);
        // You can add a success message or redirect to a confirmation page
        return "redirect:/complaints/success";

    }


    @GetMapping("/department/{departmentId}")
    public String viewDepartmentComplaints(@PathVariable Long departmentId, Model model) {
        List<Complaint> complaints = complaintService.getComplaintsByDepartmentId(departmentId);
        model.addAttribute("complaints", complaints);
        // You can use the same view or create a new one, e.g., "department_complaints/list"
        return "departments/complaints/list";
    }
    @GetMapping("/success")
    public String showSuccessPage() {
        return "complaints/success";
    }

    // Chief Engineer: Displays the list of all complaints

}