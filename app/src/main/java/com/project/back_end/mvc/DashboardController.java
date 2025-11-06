package com.project.back_end.mvc;

import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class DashboardController {

    private final Service service;

    @Autowired
    public DashboardController(Service service) {
        this.service = service;
    }

    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        ResponseEntity<Map<String, String>> validationResult = service.validateToken(token, "admin");
        
        if (validationResult.getStatusCode() == HttpStatus.OK) {
            // Token is valid, return the admin dashboard view
            return "admin/adminDashboard";
        } else {
            // Token is invalid, redirect to login page
            return "redirect:http://localhost:8080";
        }
    }

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        ResponseEntity<Map<String, String>> validationResult = service.validateToken(token, "doctor");
        
        if (validationResult.getStatusCode() == HttpStatus.OK) {
            // Token is valid, return the doctor dashboard view
            return "doctor/doctorDashboard";
        } else {
            // Token is invalid, redirect to login page
            return "redirect:http://localhost:8080";
        }
    }
}
