package com.UOK.engineeringDeptComplaintApp.controller;

import com.UOK.engineeringDeptComplaintApp.model.Complaint;
import com.UOK.engineeringDeptComplaintApp.service.ComplaintService;
import com.UOK.engineeringDeptComplaintApp.util.FileMimeUtil; // NEW: Needed for Thymeleaf file checks
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource; // NEW: For serving files
import org.springframework.core.io.UrlResource; // NEW: For resolving file path to a URL resource
import org.springframework.http.HttpHeaders; // NEW: For setting download headers
import org.springframework.http.MediaType; // NEW: For setting file MIME type
import org.springframework.http.ResponseEntity; // NEW: For file streaming response
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody; // NEW: To return file data, not a view

import jakarta.servlet.http.HttpServletRequest; // FIXED: Changed from 'javax' to 'jakarta'
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files; // NEW: For file I/O operations
import java.nio.file.Path; // NEW: For handling file paths
import java.nio.file.Paths; // NEW: For creating Path objects
import java.util.List;

@Controller
@RequestMapping("/vc")
public class ViceChancellorController {

    private final ComplaintService complaintService;

    /*
     * CRITICAL: Define the absolute root directory where files are stored.
     * This path MUST EXACTLY MATCH the Sub-Engineer's upload location:
     * System.getProperty("user.dir") + "/src/main/resources/static/images/"
     */
    private final Path fileStorageLocation = Paths.get(System.getProperty("user.dir") + "/src/main/resources/static/images/").toAbsolutePath().normalize();

    @Autowired
    public ViceChancellorController(ComplaintService complaintService) {
        this.complaintService = complaintService;
        // Ensure the directory exists (basic file system setup)
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            System.err.println("Could not create file upload directory: " + e.getMessage());
        }
    }

    // Displays a detailed list of all complaints for the Vice-Chancellor
    @GetMapping("/complaints")
    public String viewAllComplaints(Model model) {
        List<Complaint> allComplaints = complaintService.getAllComplaints();
        model.addAttribute("complaints", allComplaints);
        return "vc/complaints";
    }

    /**
     * UPDATED: Displays the full details, processing the comma-separated file paths
     * and passing necessary utilities to the Thymeleaf template.
     */
    @GetMapping("/complaint/{id}")
    public String viewComplaintDetail(@PathVariable Long id, Model model) {
        Complaint complaint = complaintService.getComplaintById(id)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));
        model.addAttribute("complaint", complaint);

        // 1. Pass the utility class for Thymeleaf logic (FileMimeUtil.isImage())
        model.addAttribute("FileMimeUtil", FileMimeUtil.class);

        // 2. Process file paths from the database
        if (complaint.getReport() != null && complaint.getReport().getImagePath() != null && !complaint.getReport().getImagePath().isEmpty()) {

            String combinedPaths = complaint.getReport().getImagePath();

            // Split the comma-separated string from the database
            String[] rawFilePaths = combinedPaths.split(",");
            String[] cleanedFileNames = new String[rawFilePaths.length];

            for (int i = 0; i < rawFilePaths.length; i++) {
                String path = rawFilePaths[i].trim();

                // CRITICAL FIX: The sub-engineer saves the path with the "/images/" prefix.
                // We strip this prefix (e.g., "/images/123.jpg" -> "123.jpg")
                // to get ONLY the unique filename for the download endpoint.
                if (path.startsWith("/images/")) {
                    cleanedFileNames[i] = path.substring("/images/".length());
                } else {
                    cleanedFileNames[i] = path;
                }
            }

            // Pass the array of clean filenames to the HTML template as 'reportFiles'
            model.addAttribute("reportFiles", cleanedFileNames);
        } else {
            // If no files, provide an empty array to prevent Thymeleaf errors
            model.addAttribute("reportFiles", new String[0]);
        }

        return "vc/complaintDetail";
    }

    /**
     * **NEW ENDPOINT**: Streams the requested file content from the server's disk to the browser.
     * The Thymeleaf template links to this endpoint using the clean filename.
     */
    @GetMapping("/download/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {

        Resource resource;
        String contentType = null;
        try {
            // Resolve the clean filename against the absolute storage location
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                System.err.println("File not found in storage: " + fileName);
                return ResponseEntity.notFound().build();
            }

            // Determine content type (MIME type)
            contentType = Files.probeContentType(resource.getFile().toPath());

        } catch (IOException ex) {
            contentType = "application/octet-stream";
            if (ex instanceof MalformedURLException) {
                return ResponseEntity.notFound().build();
            }
            System.err.println("Error reading file: " + ex.getMessage());
            return ResponseEntity.status(500).build();
        }

        if (contentType == null || contentType.isEmpty()) {
            contentType = "application/octet-stream";
        }

        // Stream the file back to the browser
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                // Use Content-Disposition: attachment; for download prompting, or the browser will display if possible
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
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
