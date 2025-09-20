package com.UOK.engineeringDeptComplaintApp.service;

import com.UOK.engineeringDeptComplaintApp.model.ComplaintStatus;
import com.UOK.engineeringDeptComplaintApp.model.Report;
import com.UOK.engineeringDeptComplaintApp.repository.ReportRepository;
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

    // Finance: Get all reports that have been forwarded for review
    public List<Report> getAllForwardedReports() {
        return reportRepository.findByComplaintStatus(ComplaintStatus.FORWARDED_TO_FINANCE);
    }
}