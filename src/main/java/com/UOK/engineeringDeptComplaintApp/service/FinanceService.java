package com.UOK.engineeringDeptComplaintApp.service;

import com.UOK.engineeringDeptComplaintApp.model.ComplaintStatus;
import com.UOK.engineeringDeptComplaintApp.model.Report;
import com.UOK.engineeringDeptComplaintApp.repository.ReportRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinanceService {

    private final ReportRepository reportRepository;

    @Autowired
    public FinanceService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }


    public List<Report> searchApprovedReports(String dept, String dateStr) {
        // 1. Get all approved reports first
        List<Report> reports = reportRepository.findByComplaintStatus(ComplaintStatus.APPROVED);

        // 2. Filter them manually using Java Streams (Simple and effective)
        return reports.stream()
                .filter(r -> (dept == null || dept.isEmpty() ||
                        r.getComplaint().getDepartment().getName().toLowerCase().contains(dept.toLowerCase())))
                .filter(r -> (dateStr == null || dateStr.isEmpty() ||
                        r.getInspectionDate().toLocalDate().toString().equals(dateStr)))
                .toList(); // Using .toList() for Java 16+ or .collect(Collectors.toList()) for older
    }

    public List<Report> getAllApprovedReports() {
        return reportRepository.findByComplaintStatus(ComplaintStatus.APPROVED);
    }

    @Transactional
    public void approveReport(Long reportId) {
        // 1. Find the report
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found with ID: " + reportId));

        // 2. Explicitly check if the complaint exists
        if (report.getComplaint() == null) {
            throw new IllegalStateException("This report is not linked to any complaint!");
        }

        // 3. Update status
        report.getComplaint().setStatus(ComplaintStatus.APPROVED);

        // 4. Save the report (Ensure cascading is working)
        reportRepository.save(report);
    }
    // Finance: Get all reports that have been forwarded for review
    public List<Report> getAllForwardedReports() {
        return reportRepository.findByComplaintStatus(ComplaintStatus.FORWARDED_TO_FINANCE);
    }

    public Report getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Report not found with id: " + id));
    }

}
