/*
package com.UOK.engineeringDeptComplaintApp.controller;

import com.UOK.engineeringDeptComplaintApp.model.Complaint;
import com.UOK.engineeringDeptComplaintApp.model.Department;
import com.UOK.engineeringDeptComplaintApp.model.SubEngineer;
import com.UOK.engineeringDeptComplaintApp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;
    private final DepartmentService departmentService;
    private final SubEngineerService subEngineerService;

    @Autowired
    public ComplaintController(ComplaintService complaintService, DepartmentService departmentService, SubEngineerService subEngineerService, UserService userService,
                               AuthenticationService authenticationService) {
        this.complaintService = complaintService;
        this.departmentService = departmentService;
        this.subEngineerService = subEngineerService;

    }
    // Chief Engineer: Displays the detail of a single complaint
    @GetMapping("/chief/{id}")
    public String chiefViewComplaintDetail(@PathVariable Long id, Model model) {
        Complaint complaint = complaintService.getComplaintById(id)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));

        List<SubEngineer> subEngineers = subEngineerService.getAllSubEngineers();

        model.addAttribute("complaint", complaint);
        model.addAttribute("subEngineers", subEngineers);
        return "chief/complaints/detail";
    }

    // Chief Engineer: Handles the delegation of a complaint
    @PostMapping("/chief/delegate")
    public String delegateComplaint(@RequestParam("complaintId") Long complaintId,
                                    @RequestParam("subEngineerId") Long subEngineerId) {
        complaintService.delegateComplaint(complaintId, subEngineerId);
        return "redirect:/complaints/chief/" + complaintId;
    }

    // Chief Engineer: Handles forwarding a report to Finance
    @PostMapping("/chief/forward-to-finance")
    public String forwardToFinance(@RequestParam("complaintId") Long complaintId) {
        complaintService.forwardToFinance(complaintId);
        return "redirect:/complaints/chief/" + complaintId;
    }

    // Displays the complaint registration form (for a Department user)
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        List<Department> departments = departmentService.getAllDepartments();
        model.addAttribute("complaint", new Complaint());
        model.addAttribute("departments", departments);
        return "complaints/form";
    }

    // Handles the submission of a new complaint
    @PostMapping("/register")
    public String registerComplaint(@ModelAttribute Complaint complaint, Model model) {
        Complaint savedComplaint = complaintService.registerComplaint(complaint);
        // You can add a success message or redirect to a confirmation page
        return "redirect:/complaints/success";

    }


    @GetMapping("/department/{departmentId}")
    public String viewDepartmentComplaints(@PathVariable Long departmentId, Model model) {
        List<Complaint> complaints = complaintService.getComplaintsByDepartmentId(departmentId);
        model.addAttribute("complaints", complaints);
        // You can use the same view or create a new one, e.g., "department_complaints/list"
        return "complaints/list";
    }
    @GetMapping("/success")
    public String showSuccessPage() {
        return "complaints/success";
    }

    // Chief Engineer: Displays the list of all complaints

}*/

package com.UOK.engineeringDeptComplaintApp.controller;

import com.UOK.engineeringDeptComplaintApp.model.*;
import com.UOK.engineeringDeptComplaintApp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;
    private final DepartmentService departmentService;
    private final SubEngineerService subEngineerService;
    private final UserService userService; // Add this

    @Autowired
    public ComplaintController(ComplaintService complaintService,
                               DepartmentService departmentService,
                               SubEngineerService subEngineerService,
                               UserService userService) {
        this.complaintService = complaintService;
        this.departmentService = departmentService;
        this.subEngineerService = subEngineerService;
        this.userService = userService; // Initialize
    }

    @GetMapping("/department/{departmentId}")
    public String viewDepartmentComplaints(
            @PathVariable Long departmentId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status,
            Model model) {

        List<Complaint> complaints = complaintService.getFilteredDepartmentComplaints(departmentId, title, status);

        model.addAttribute("complaints", complaints);
        model.addAttribute("departmentId", departmentId);
        model.addAttribute("selectedTitle", title);
        model.addAttribute("selectedStatus", status);

        return "complaints/list";
    }

    @PostMapping("/department/complete/{id}")
    public String completeComplaint(@PathVariable Long id, @RequestParam Long departmentId) {
        complaintService.markAsCompleted(id);
        return "redirect:/complaints/department/" + departmentId;
    }

    // Displays the complaint registration form (for a Department user)
    @GetMapping("/register")
    public String showRegistrationForm(Model model, Authentication authentication) {
        // Get the logged-in user
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if user is a department user
        if (currentUser.getRole() != UserRole.ROLE_DEPARTMENT) {
            return "redirect:/access-denied";
        }

        // Create new complaint and auto-set department
        Complaint complaint = new Complaint();
        complaint.setDepartment(currentUser.getDepartment());

        model.addAttribute("complaint", complaint);
        model.addAttribute("userDepartment", currentUser.getDepartment());

        return "complaints/form";
    }

    // Handles the submission of a new complaint
    @PostMapping("/register")
    public String registerComplaint(@ModelAttribute Complaint complaint,
                                    Authentication authentication,
                                    Model model) {

        // Get current user
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Auto-set user's department (overrides any client-side tampering)
        complaint.setDepartment(currentUser.getDepartment());

        Complaint savedComplaint = complaintService.registerComplaint(complaint);

        // Pass data to success page
        model.addAttribute("departmentId", currentUser.getDepartment().getId());
        model.addAttribute("complaintTitle", savedComplaint.getTitle());
        model.addAttribute("userDepartment", currentUser.getDepartment());

        return "complaints/success";
    }

    @GetMapping("/chief")
    public String showChiefDashboard(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String dept,
            @RequestParam(required = false) String status,
            Model model) {

        // Call the new filtered service method
        List<Complaint> complaints = complaintService.getFilteredComplaints(title, dept, status);

        model.addAttribute("complaints", complaints);

        // Keep the search values in the input boxes after the page reloads
        model.addAttribute("selectedTitle", title);
        model.addAttribute("selectedDept", dept);
        model.addAttribute("selectedStatus", status);

        return "chief/complaints/list"; // Ensure this matches your HTML file path
    }

    // All your other methods remain the same...
    @GetMapping("/chief/{id}")
    public String chiefViewComplaintDetail(@PathVariable Long id, Model model) {
        Complaint complaint = complaintService.getComplaintById(id)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));

        List<SubEngineer> subEngineers = subEngineerService.getAllSubEngineers();

        model.addAttribute("complaint", complaint);
        model.addAttribute("subEngineers", subEngineers);
        return "chief/complaints/detail";
    }

    @PostMapping("/chief/delegate")
    public String delegateComplaint(@RequestParam("complaintId") Long complaintId,
                                    @RequestParam("subEngineerId") Long subEngineerId) {
        complaintService.delegateComplaint(complaintId, subEngineerId);
        return "redirect:/complaints/chief/" + complaintId;
    }

    @PostMapping("/chief/forward-to-finance")
    public String forwardToFinance(@RequestParam("complaintId") Long complaintId) {
        complaintService.forwardToFinance(complaintId);
        return "redirect:/complaints/chief/" + complaintId;
    }
/*

    @GetMapping("/department/{departmentId}")
    public String viewDepartmentComplaints(@PathVariable Long departmentId, Model model) {
        List<Complaint> complaints = complaintService.getComplaintsByDepartmentId(departmentId);
        model.addAttribute("complaints", complaints);
        return "complaints/list";
    }
*/

    @GetMapping("/success")
    public String showSuccessPage(Model model, Authentication authentication) {
        // Get user for department info
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        model.addAttribute("userDepartment", currentUser.getDepartment());
        return "complaints/success";
    }
}
