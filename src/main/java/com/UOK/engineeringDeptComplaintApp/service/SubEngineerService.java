package com.UOK.engineeringDeptComplaintApp.service;

import com.UOK.engineeringDeptComplaintApp.model.Complaint;
import com.UOK.engineeringDeptComplaintApp.model.ComplaintStatus;
import com.UOK.engineeringDeptComplaintApp.model.Report;
import com.UOK.engineeringDeptComplaintApp.model.SubEngineer;
import com.UOK.engineeringDeptComplaintApp.repository.ComplaintRepository;
import com.UOK.engineeringDeptComplaintApp.repository.ReportRepository;
import com.UOK.engineeringDeptComplaintApp.repository.SubEngineerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SubEngineerService {

    private final ComplaintRepository complaintRepository;
    private final ReportRepository reportRepository;
    private final SubEngineerRepository subEngineerRepository;

    @Autowired
    public SubEngineerService(ComplaintRepository complaintRepository, ReportRepository reportRepository, SubEngineerRepository subEngineerRepository) {
        this.complaintRepository = complaintRepository;
        this.reportRepository = reportRepository;
        this.subEngineerRepository = subEngineerRepository;
    }

    public List<Complaint> getAssignedComplaints(Long subEngineerId) {
        SubEngineer subEngineer = subEngineerRepository.findById(subEngineerId)
                .orElseThrow(() -> new IllegalArgumentException("Sub-Engineer not found"));
        return complaintRepository.findBySubEngineer(subEngineer);
    }

    public void submitReport(Long complaintId, String reportDetails, String imagePath) {
        // Find the existing complaint
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found with ID: " + complaintId));

        // Check if a report for this complaint already exists
        if (reportRepository.findByComplaintId(complaintId).isPresent()) {
            throw new IllegalStateException("A report for this complaint already exists.");
        }

        // Create and save the new report
        Report report = new Report();
        report.setDetails(reportDetails);
        report.setImagePath(imagePath);
        report.setInspectionDate(LocalDateTime.now());
        report.setComplaint(complaint);
        report.setSubEngineer(complaint.getSubEngineer());
        reportRepository.save(report);

        // Update the status of the existing complaint
        complaint.setStatus(ComplaintStatus.INSPECTION_DONE);
        complaintRepository.save(complaint);
    }

    // This is the missing method.
    public List<SubEngineer> getAllSubEngineers() {
        return subEngineerRepository.findAll();
    }

    // A method to find a sub-engineer by ID, needed for other services.
    public Optional<SubEngineer> getSubEngineerById(Long id) {
        return subEngineerRepository.findById(id);
    }

}