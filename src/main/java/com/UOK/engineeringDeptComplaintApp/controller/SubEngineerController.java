package com.UOK.engineeringDeptComplaintApp.controller;

import com.UOK.engineeringDeptComplaintApp.model.Complaint;
import com.UOK.engineeringDeptComplaintApp.service.SubEngineerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList; // Added
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
                               // KEY CHANGE: Accepts a list of files with the name "files"
                               @RequestParam("files") List<MultipartFile> files) {

        List<String> filePaths = new ArrayList<>();
        String uploadDirectory = System.getProperty("user.dir") + "/src/main/resources/static/images/";

        // 1. Process all uploaded files
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue; // Skip empty file entries
            }

            try {
                Path uploadPath = Paths.get(uploadDirectory);

                // Create the directory if it doesn't exist
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Create a unique file name to prevent overwrites
                String originalFileName = file.getOriginalFilename();
                // Replace special characters and prepend timestamp for uniqueness
                String uniqueFileName = System.currentTimeMillis() + "_" + originalFileName.replaceAll("[^a-zA-Z0-9.\\-]", "_");

                Path destPath = Paths.get(uploadDirectory + uniqueFileName);

                // Save the file to the local directory
                file.transferTo(destPath.toFile());
                filePaths.add("/images/" + uniqueFileName);

            } catch (IOException e) {
                e.printStackTrace();
                return "redirect:/sub-engineer/error"; // Handle file save error
            }
        }

        // 2. Combine paths into a single string for the Report model
        String combinedFilePaths = String.join(",", filePaths);

        try {
            // Call the updated service method with the combined file paths
            subEngineerService.submitReport(complaintId, reportDetails, combinedFilePaths);
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