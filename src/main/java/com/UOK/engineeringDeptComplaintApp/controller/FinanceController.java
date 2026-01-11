package com.UOK.engineeringDeptComplaintApp.controller;

import com.UOK.engineeringDeptComplaintApp.model.Report;
import com.UOK.engineeringDeptComplaintApp.service.FinanceService;
import com.UOK.engineeringDeptComplaintApp.util.FileMimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/finance")
public class FinanceController {

    private final FinanceService financeService;

    // Use the external upload directory from properties
    @Value("${file.upload-dir}")
    private String uploadDirectory;

    @Autowired
    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    // Displays a list of all reports that have been forwarded to Finance

    @GetMapping("/reports")
    public String viewForwardedReports(Model model) {
        // Reports needing action
        List<Report> pending = financeService.getAllForwardedReports();
        // Reports already approved
        List<Report> approved = financeService.getAllApprovedReports();

        model.addAttribute("reports", pending);
        model.addAttribute("approvedReports", approved);
        return "finance/reports";
    }
    @GetMapping("/history")
    public String viewFinanceHistory(
            @RequestParam(value = "dept", required = false) String dept,
            @RequestParam(value = "date", required = false) String date,
            Model model) {

        // Use the service to get approved reports (filtered or unfiltered)
        List<Report> history = financeService.searchApprovedReports(dept, date);

        model.addAttribute("history", history);
        model.addAttribute("selectedDept", dept);
        model.addAttribute("selectedDate", date);

        return "finance/history"; // This points to history.html
    }
    // View detailed report
    @GetMapping("/report/{id}")
    public String viewReportDetails(@PathVariable Long id, Model model) {
        Report report = financeService.getReportById(id);
        model.addAttribute("report", report);

        // Pass the utility class for Thymeleaf logic
        model.addAttribute("FileMimeUtil", FileMimeUtil.class);

        // Process file paths from the database
        if (report.getImagePath() != null && !report.getImagePath().isEmpty()) {
            String combinedPaths = report.getImagePath();

            // Split the comma-separated string from the database
            String[] rawFilePaths = combinedPaths.split(",");
            String[] cleanedFileNames = new String[rawFilePaths.length];

            for (int i = 0; i < rawFilePaths.length; i++) {
                String path = rawFilePaths[i].trim();

                // Strip the "/images/" prefix to get only the unique filename
                if (path.startsWith("/images/")) {
                    cleanedFileNames[i] = path.substring("/images/".length());
                } else {
                    cleanedFileNames[i] = path;
                }
            }

            // Pass the array of clean filenames to the HTML template
            model.addAttribute("reportFiles", cleanedFileNames);
        } else {
            // If no files, provide an empty array
            model.addAttribute("reportFiles", new String[0]);
        }

        return "finance/report-details";
    }
    @PostMapping("/report/approve/{id}")
    public String approveReport(@PathVariable Long id) {
        try {
            financeService.approveReport(id);
            // Redirect back to the SAME detail page, just like the Chief Engineer controller does
            return "redirect:/finance/report/" + id + "?success=true";
        } catch (Exception e) {
            // This prevents the 500 error page and shows you the error in logs
            e.printStackTrace();
            return "redirect:/finance/report/" + id + "?error=processing_failed";
        }
}}