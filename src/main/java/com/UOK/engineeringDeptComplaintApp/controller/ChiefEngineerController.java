package com.UOK.engineeringDeptComplaintApp.controller;

import com.UOK.engineeringDeptComplaintApp.model.Complaint;
import com.UOK.engineeringDeptComplaintApp.model.SubEngineer;
import com.UOK.engineeringDeptComplaintApp.service.ComplaintService;
import com.UOK.engineeringDeptComplaintApp.service.DepartmentService;
import com.UOK.engineeringDeptComplaintApp.service.SubEngineerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ChiefEngineerController {
    private final ComplaintService complaintService;
    private final DepartmentService departmentService;
    private final SubEngineerService subEngineerService;
    @Autowired
    public ChiefEngineerController(ComplaintService complaintService, DepartmentService departmentService, SubEngineerService subEngineerService) {
        this.complaintService = complaintService;
        this.departmentService = departmentService;
        this.subEngineerService = subEngineerService;
    }

    @GetMapping("/chief")
    public String chiefViewAllComplaints(Model model) {
        List<Complaint> complaints = complaintService.getAllComplaints();
        model.addAttribute("complaints", complaints);
        return "chief/complaints/list";
    }


}
