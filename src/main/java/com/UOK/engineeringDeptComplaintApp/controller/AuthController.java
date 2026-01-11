package com.UOK.engineeringDeptComplaintApp.controller;

import com.UOK.engineeringDeptComplaintApp.model.User;
import com.UOK.engineeringDeptComplaintApp.service.UserService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }



    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            // Redirect them to the dashboard logic so they don't see the login page again
            return "redirect:/dashboard";
        }
        if (error != null) {
            model.addAttribute("error", "Invalid username or password!");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        return "auth/login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority();

        // Find user by username to get their ID
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        switch (role) {
            case "ROLE_ADMIN":
                return "redirect:/admin/dashboard";
            case "ROLE_CHIEF_ENGINEER":
                return "redirect:/chief";
            case "ROLE_SUB_ENGINEER":
                // Redirect with the sub-engineer's ID
                Long subEngineerId = user.getSubEngineer() != null ? user.getSubEngineer().getId() : user.getId();
                return "redirect:/sub-engineer/" + subEngineerId + "/complaints";
            case "ROLE_DEPARTMENT":
                return "redirect:/complaints/register";
            case "ROLE_FINANCE":
                return "redirect:/finance/reports";
            case "ROLE_VC":
                return "redirect:/vc/dashboard";
            default:
                return "redirect:/access-denied";
        }
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }
}