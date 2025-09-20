package com.UOK.engineeringDeptComplaintApp.controller;

import com.UOK.engineeringDeptComplaintApp.model.Complaint;
import com.UOK.engineeringDeptComplaintApp.service.SubEngineerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/sub-engineer")
public class SubEngineerController {

    private final SubEngineerService subEngineerService;

    @Autowired
    public SubEngineerController(SubEngineerService subEngineerService) {
        this.subEngineerService = subEngineerService;
    }

    // Displays the list of complaints assigned to a specific Sub-Engineer
    @GetMapping("/{id}/complaints")
    public String viewAssignedComplaints(@PathVariable Long id, Model model) {
        List<Complaint> assignedComplaints = subEngineerService.getAssignedComplaints(id);
        model.addAttribute("complaints", assignedComplaints);
        return "sub-engineer/complaints";
    }

    // Displays the report submission form for a specific complaint
    @GetMapping("/{id}/report/{complaintId}")
    public String showReportForm(@PathVariable Long id, @PathVariable Long complaintId, Model model) {
        model.addAttribute("complaintId", complaintId);
        // You can also add other necessary data to the model
        return "sub-engineer/report-form";
    }

    // Handles the submission of an inspection report
    @PostMapping("/submit-report")
    public String submitReport(@RequestParam("complaintId") Long complaintId,
                               @RequestParam("reportDetails") String reportDetails,
                               @RequestParam("imagePath") String imagePath) {
        subEngineerService.submitReport(complaintId, reportDetails, imagePath);
        return "redirect:/sub-engineer/success";
    }
}