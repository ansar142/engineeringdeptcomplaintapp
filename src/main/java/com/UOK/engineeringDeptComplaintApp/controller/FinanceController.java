package com.UOK.engineeringDeptComplaintApp.controller;

import com.UOK.engineeringDeptComplaintApp.model.Report;
import com.UOK.engineeringDeptComplaintApp.service.FinanceService;
import com.UOK.engineeringDeptComplaintApp.util.FileMimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
        List<Report> forwardedReports = financeService.getAllForwardedReports();
        model.addAttribute("reports", forwardedReports);
        return "finance/reports";
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
}