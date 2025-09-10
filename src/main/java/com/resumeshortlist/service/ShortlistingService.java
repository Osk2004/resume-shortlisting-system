package com.resumeshortlist.service;

import com.resumeshortlist.entity.JobRequirement;
import com.resumeshortlist.entity.Resume;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShortlistingService {

    public double calculateMatchScore(Resume resume, JobRequirement jobRequirement) {
        // CORRECTED LOGIC: If the resume text is null or empty, it's an automatic 0.
        // This is the definitive check that prevents blank resumes from being scored.
        if (resume.getResumeText() == null || resume.getResumeText().trim().isEmpty()) {
            return 0.0;
        }

        double totalScore = 0.0;
        double maxScore = 100.0;
        
        // Skills matching (40% weight)
        double skillsScore = calculateSkillsMatch(resume.getSkills(), jobRequirement.getRequiredSkills());
        totalScore += skillsScore * 0.4;
        
        // Experience matching (35% weight)
        double experienceScore = calculateExperienceMatch(resume.getExperienceYears(), 
                                                        jobRequirement.getMinExperience(), 
                                                        jobRequirement.getMaxExperience());
        totalScore += experienceScore * 0.35;
        
        // Education matching (25% weight)
        double educationScore = calculateEducationMatch(resume.getEducation(), 
                                                      jobRequirement.getRequiredEducation());
        totalScore += educationScore * 0.25;
        
        return Math.min(totalScore, maxScore);
    }

    private double calculateSkillsMatch(List<String> candidateSkills, List<String> requiredSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            return 100.0;
        }
        
        if (candidateSkills == null || candidateSkills.isEmpty()) {
            return 0.0;
        }
        
        long matchedSkills = requiredSkills.stream()
            .filter(reqSkill -> candidateSkills.stream()
                .anyMatch(candSkill -> candSkill.toLowerCase().contains(reqSkill.toLowerCase())))
            .count();
        
        return ((double) matchedSkills / requiredSkills.size()) * 100.0;
    }

    private double calculateExperienceMatch(Integer candidateExp, Integer minExp, Integer maxExp) {
        if (minExp == null) {
            return 100.0;
        }
        
        if (candidateExp == null) {
            candidateExp = 0;
        }
        
        if (candidateExp >= minExp) {
            if (maxExp == null || candidateExp <= maxExp) {
                return 100.0;
            } else {
                // Penalize over-qualification slightly
                return Math.max(70.0, 100.0 - ((candidateExp - maxExp) * 5.0));
            }
        } else {
            // Under-qualified
            return Math.max(0.0, 100.0 - ((minExp - candidateExp) * 20.0));
        }
    }

    private double calculateEducationMatch(String candidateEducation, String requiredEducation) {
        if (requiredEducation == null || requiredEducation.trim().isEmpty()) {
            return 100.0;
        }
        
        if (candidateEducation == null || candidateEducation.equalsIgnoreCase("Not Specified")) {
            return 0.0; // Job requires education, but candidate has none specified.
        }
        
        if (candidateEducation.toLowerCase().contains(requiredEducation.toLowerCase())) {
            return 100.0;
        }
        
        return 30.0; // Partial match for having some education, but not the required one.
    }

    public String determineShortlistStatus(double matchScore) {
        if (matchScore >= 75.0) {
            return "SHORTLISTED";
        } else if (matchScore >= 50.0) {
            return "PENDING";
        } else {
            return "REJECTED";
        }
    }
}

