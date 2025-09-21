package com.UOK.engineeringDeptComplaintApp.controller;

import com.UOK.engineeringDeptComplaintApp.model.Complaint;
import com.UOK.engineeringDeptComplaintApp.service.SubEngineerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        try {
            List<Complaint> assignedComplaints = subEngineerService.getAssignedComplaints(id);
            model.addAttribute("complaints", assignedComplaints);
            return "sub-engineer/complaints";
        } catch (IllegalArgumentException e) {
            // Handle the case where the sub-engineer ID is not found
            model.addAttribute("errorMessage", "Sub-Engineer with ID " + id + " not found.");
            return "error"; // A generic error page or a specific one for this error
        }
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
                               @RequestParam("imageFile") MultipartFile imageFile) {
        String imagePath = null;
        if (!imageFile.isEmpty()) {
            try {
                // Get the project's root directory and define the static images path
                String uploadDirectory = System.getProperty("user.dir") + "/src/main/resources/static/images/";
                Path uploadPath = Paths.get(uploadDirectory);

                // Create the directory if it doesn't exist
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Get the file name and create a unique file name to prevent overwrites
                String fileName = imageFile.getOriginalFilename();
                Path destPath = Paths.get(uploadDirectory + fileName);

                // Save the file to the local directory
                imageFile.transferTo(destPath.toFile());
                imagePath = "/images/" + fileName;

            } catch (IOException e) {
                // Handle file-related exceptions
                e.printStackTrace();
                // Add an error message to the model for the view
                // For now, redirect to a simple error page
                return "redirect:/sub-engineer/error";
            }
        }

        try {
            // Save the report with the image path
            subEngineerService.submitReport(complaintId, reportDetails, imagePath);
            return "redirect:/sub-engineer/success";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/sub-engineer/error"; // Redirect to a generic error page
        }
    }

    // Handles the success page for sub-engineer
    @GetMapping("/success")
    public String showSuccessPage() {
        return "sub-engineer/success";
    }

    // Handle a generic error page
    @GetMapping("/error")
    public String showErrorPage(Model model) {
        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again.");
        return "error";
    }
}
