package com.UOK.engineeringDeptComplaintApp.service;

import com.UOK.engineeringDeptComplaintApp.model.*;
import com.UOK.engineeringDeptComplaintApp.repository.ComplaintRepository;
import com.UOK.engineeringDeptComplaintApp.repository.SubEngineerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final SubEngineerRepository subEngineerRepository;

    @Autowired
    public ComplaintService(ComplaintRepository complaintRepository, SubEngineerRepository subEngineerRepository) {
        this.complaintRepository = complaintRepository;
        this.subEngineerRepository = subEngineerRepository;
    }

    // Department: Register a new complaint
    public Complaint registerComplaint(Complaint complaint) {
        complaint.setStatus(ComplaintStatus.REGISTERED);
        return complaintRepository.save(complaint);
    }

    // Chief Engineer: View a single complaint
    public Optional<Complaint> getComplaintById(Long id) {
        Optional<Complaint> complaint = complaintRepository.findById(id);
        if (complaint.isPresent() && complaint.get().getStatus() == ComplaintStatus.REGISTERED) {
            complaint.get().setStatus(ComplaintStatus.VIEWED_BY_CHIEF);
            complaintRepository.save(complaint.get());
        }
        return complaint;
    }

    // Chief Engineer: Delegate a complaint to a Sub-Engineer
    public Complaint delegateComplaint(Long complaintId, Long subEngineerId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));
        SubEngineer subEngineer = subEngineerRepository.findById(subEngineerId)
                .orElseThrow(() -> new IllegalArgumentException("Sub-Engineer not found"));

        complaint.setSubEngineer(subEngineer);
        complaint.setStatus(ComplaintStatus.FORWARDED_TO_SUB_ENGINEER);
        return complaintRepository.save(complaint);
    }

    // Chief Engineer: Forward a report to Finance
    public Complaint forwardToFinance(Long complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));

        complaint.setStatus(ComplaintStatus.FORWARDED_TO_FINANCE);
        return complaintRepository.save(complaint);
    }

    // Get all complaints for the Chief Engineer and VC dashboards
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }
}