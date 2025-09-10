package com.resumeshortlist.controller;


import com.resumeshortlist.entity.JobRequirement;
import com.resumeshortlist.entity.Resume;
import com.resumeshortlist.service.JobRequirementService;
import com.resumeshortlist.service.ResumeService;
import com.resumeshortlist.service.ShortlistingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/shortlisting")
@CrossOrigin(origins = "*")
public class ShortlistingController {

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private JobRequirementService jobRequirementService;

    @Autowired
    private ShortlistingService shortlistingService;

    @PostMapping("/batch-process/{jobId}")
    public ResponseEntity<Map<String, Object>> batchProcessShortlisting(@PathVariable Long jobId) {
        Optional<JobRequirement> jobOpt = jobRequirementService.getJobRequirementById(jobId);
        
        if (!jobOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        JobRequirement job = jobOpt.get();
        List<Resume> allResumes = resumeService.getAllResumes();
        
        int processed = 0;
        int shortlisted = 0;
        int rejected = 0;
        
        for (Resume resume : allResumes) {
            double matchScore = shortlistingService.calculateMatchScore(resume, job);
            String status = shortlistingService.determineShortlistStatus(matchScore);
            
            resume.setMatchScore(matchScore);
            resume.setShortlistStatus(status);
            
            processed++;
            if ("SHORTLISTED".equals(status)) shortlisted++;
            else if ("REJECTED".equals(status)) rejected++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("jobTitle", job.getJobTitle());
        result.put("totalProcessed", processed);
        result.put("shortlisted", shortlisted);
        result.put("rejected", rejected);
        result.put("pending", processed - shortlisted - rejected);
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getShortlistingStatistics() {
        List<Resume> allResumes = resumeService.getAllResumes();
        
        long total = allResumes.size();
        long shortlisted = allResumes.stream().filter(r -> "SHORTLISTED".equals(r.getShortlistStatus())).count();
        long pending = allResumes.stream().filter(r -> "PENDING".equals(r.getShortlistStatus())).count();
        long rejected = allResumes.stream().filter(r -> "REJECTED".equals(r.getShortlistStatus())).count();
        
        double avgScore = allResumes.stream()
                .filter(r -> r.getMatchScore() != null)
                .mapToDouble(Resume::getMatchScore)
                .average()
                .orElse(0.0);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalResumes", total);
        stats.put("shortlisted", shortlisted);
        stats.put("pending", pending);
        stats.put("rejected", rejected);
        stats.put("averageMatchScore", Math.round(avgScore * 100.0) / 100.0);
        stats.put("shortlistingRate", total > 0 ? Math.round((shortlisted * 100.0 / total) * 100.0) / 100.0 : 0);
        
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/calculate-score")
    public ResponseEntity<Map<String, Object>> calculateMatchScore(
            @RequestParam Long resumeId, 
            @RequestParam Long jobId) {
        
        Optional<Resume> resumeOpt = resumeService.getResumeById(resumeId);
        Optional<JobRequirement> jobOpt = jobRequirementService.getJobRequirementById(jobId);
        
        if (!resumeOpt.isPresent() || !jobOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Resume resume = resumeOpt.get();
        JobRequirement job = jobOpt.get();
        
        double matchScore = shortlistingService.calculateMatchScore(resume, job);
        String status = shortlistingService.determineShortlistStatus(matchScore);
        
        Map<String, Object> result = new HashMap<>();
        result.put("candidateName", resume.getCandidateName());
        result.put("jobTitle", job.getJobTitle());
        result.put("matchScore", Math.round(matchScore * 100.0) / 100.0);
        result.put("recommendedStatus", status);
        result.put("skillsMatch", calculateSkillsMatchPercentage(resume.getSkills(), job.getRequiredSkills()));
        result.put("experienceMatch", resume.getExperienceYears() >= job.getMinExperience());
        
        return ResponseEntity.ok(result);
    }
    
    private double calculateSkillsMatchPercentage(List<String> candidateSkills, List<String> requiredSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) return 100.0;
        if (candidateSkills == null || candidateSkills.isEmpty()) return 0.0;
        
        long matchedSkills = requiredSkills.stream()
            .mapToLong(required -> candidateSkills.stream()
                .mapToLong(candidate -> candidate.toLowerCase().contains(required.toLowerCase()) ? 1 : 0)
                .sum())
            .sum();
        
        return Math.round((matchedSkills * 100.0 / requiredSkills.size()) * 100.0) / 100.0;
    }
}
