package com.resumeshortlist.controller;

import com.resumeshortlist.entity.JobRequirement;
import com.resumeshortlist.repository.JobRequirementRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*") // Allow requests from our React frontend
public class JobRequirementController {

    @Autowired
    private JobRequirementRepository jobRequirementRepository;

    @PostMapping
    public ResponseEntity<JobRequirement> createJobRequirement(@Valid @RequestBody JobRequirement jobRequirement) {
        JobRequirement savedJob = jobRequirementRepository.save(jobRequirement);
        return ResponseEntity.ok(savedJob);
    }

    @GetMapping("/active")
    public ResponseEntity<List<JobRequirement>> getActiveJobRequirements() {
        List<JobRequirement> activeJobs = jobRequirementRepository.findByIsActiveTrue();
        return ResponseEntity.ok(activeJobs);
    }
    
    @GetMapping
    public ResponseEntity<List<JobRequirement>> getAllJobRequirements() {
        List<JobRequirement> allJobs = jobRequirementRepository.findAll();
        return ResponseEntity.ok(allJobs);
    }
}