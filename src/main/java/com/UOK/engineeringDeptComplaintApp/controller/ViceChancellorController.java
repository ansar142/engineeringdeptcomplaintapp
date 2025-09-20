package com.UOK.engineeringDeptComplaintApp.controller;

import com.UOK.engineeringDeptComplaintApp.model.Complaint;
import com.UOK.engineeringDeptComplaintApp.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/vc")
public class ViceChancellorController {

    private final ComplaintService complaintService;

    @Autowired
    public ViceChancellorController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    // Displays a detailed list of all complaints for the Vice-Chancellor
    @GetMapping("/complaints")
    public String viewAllComplaints(Model model) {
        List<Complaint> allComplaints = complaintService.getAllComplaints();
        model.addAttribute("complaints", allComplaints);
        return "vc/complaints";
    }

    // Displays the full details of a single complaint, including the report
    @GetMapping("/complaint/{id}")
    public String viewComplaintDetail(@PathVariable Long id, Model model) {
        Complaint complaint = complaintService.getComplaintById(id)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));
        model.addAttribute("complaint", complaint);
        return "vc/complaintDetail";
    }
}