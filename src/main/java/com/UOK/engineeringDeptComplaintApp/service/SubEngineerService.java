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

    public Report submitReport(Long complaintId, String reportDetails, String imagePath) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));
        Report report = new Report();
        report.setComplaint(complaint);
        report.setDetails(reportDetails);
        report.setImagePath(imagePath);
        report.setInspectionDate(LocalDateTime.now());
        report.setSubEngineer(complaint.getSubEngineer());
        complaint.setStatus(ComplaintStatus.INSPECTION_DONE);
        complaint.setReport(report);
        complaintRepository.save(complaint);
        return reportRepository.save(report);
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