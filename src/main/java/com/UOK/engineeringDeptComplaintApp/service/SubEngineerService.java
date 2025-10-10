package com.UOK.engineeringDeptComplaintApp.service;

import org.springframework.transaction.annotation.Transactional;
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

    public void submitReport(Long complaintId, String reportDetails, String combinedFilePaths) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found with ID: " + complaintId));

        if (reportRepository.findByComplaintId(complaintId).isPresent()) {
            throw new IllegalStateException("A report for this complaint already exists.");
        }

        Report report = new Report();
        report.setDetails(reportDetails);
        report.setImagePath(combinedFilePaths);
        report.setInspectionDate(LocalDateTime.now());
        report.setComplaint(complaint);
        report.setSubEngineer(complaint.getSubEngineer());
        reportRepository.save(report);

        complaint.setStatus(ComplaintStatus.INSPECTION_DONE);
        complaintRepository.save(complaint);
    }

    public List<SubEngineer> getAllSubEngineers() {
        return subEngineerRepository.findAll();
    }


    @Transactional
    public void deleteSubEngineer(Long id) {
        // Get the sub-engineer
        SubEngineer subEngineer = subEngineerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sub-Engineer not found"));

        // 1. Handle reports first - set subEngineer to null
        List<Report> reports = reportRepository.findBySubEngineerId(id);
        for (Report report : reports) {
            report.setSubEngineer(null);
            reportRepository.save(report);
        }

        // 2. Handle complaints - set subEngineer to null and update status
        List<Complaint> complaints = complaintRepository.findBySubEngineerId(id);
        for (Complaint complaint : complaints) {
            complaint.setSubEngineer(null);
            // If complaint has report, keep status as INSPECTION_DONE, otherwise reset
            if (complaint.getReport() == null) {
                complaint.setStatus(ComplaintStatus.VIEWED_BY_CHIEF);
            }
            complaintRepository.save(complaint);
        }

        // 3. Clear the sub-engineer's collection
        subEngineer.getAssignedComplaints().clear();

        // 4. Delete the sub-engineer
        subEngineerRepository.delete(subEngineer);
    }


    public Optional<SubEngineer> getSubEngineerById(Long id) {
        return subEngineerRepository.findById(id);
    }

    // Add this method to fix the error
    public SubEngineer saveSubEngineer(SubEngineer subEngineer) {
        return subEngineerRepository.save(subEngineer);
    }
}