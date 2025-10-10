package com.UOK.engineeringDeptComplaintApp.controller;

import com.UOK.engineeringDeptComplaintApp.model.Complaint;
import com.UOK.engineeringDeptComplaintApp.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/vc")
public class VCDashboardController {

    private final ComplaintService complaintService;

    @Autowired
    public VCDashboardController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @GetMapping("/dashboard")
    public String vcDashboard(Model model) {
        List<Complaint> complaints = complaintService.getAllComplaints();

        // Calculate statistics
        long totalComplaints = complaints.size();
        long pendingComplaints = complaints.stream()
                .filter(c -> c.getStatus() != com.UOK.engineeringDeptComplaintApp.model.ComplaintStatus.FORWARDED_TO_FINANCE)
                .count();
        long completedComplaints = complaints.stream()
                .filter(c -> c.getStatus() == com.UOK.engineeringDeptComplaintApp.model.ComplaintStatus.FORWARDED_TO_FINANCE)
                .count();

        model.addAttribute("totalComplaints", totalComplaints);
        model.addAttribute("pendingComplaints", pendingComplaints);
        model.addAttribute("completedComplaints", completedComplaints);
        model.addAttribute("complaints", complaints);

        return "vc/dashboard";
    }
}