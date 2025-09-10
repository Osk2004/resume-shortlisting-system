package com.resumeshortlist.service;

import com.resumeshortlist.entity.JobRequirement;
import com.resumeshortlist.entity.Resume;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShortlistingServiceTest {

    /**
     * Calculate match score based on:
     *  - Skills (40%)
     *  - Experience (35%)
     *  - Education (25%)
     */
    public double calculateMatchScore(Resume resume, JobRequirement jobRequirement) {
        double skillScore = calculateSkillScore(resume.getSkills(), jobRequirement.getRequiredSkills());
        double experienceScore = calculateExperienceScore(resume.getExperienceYears(),
                                                         jobRequirement.getMinExperience(),
                                                         jobRequirement.getMaxExperience());
        double educationScore = calculateEducationScore(resume.getEducation(), jobRequirement.getRequiredEducation());

        // Weighted scoring
        return (skillScore * 0.4) + (experienceScore * 0.35) + (educationScore * 0.25);
    }

    /**
     * Determine shortlist status based on match score.
     */
    public String determineShortlistStatus(double score) {
        if (score >= 75.0) {
            return "SHORTLISTED";
        } else if (score >= 50.0) {
            return "PENDING";
        } else {
            return "REJECTED";
        }
    }

    // =======================
    // Helper Methods
    // =======================

    private double calculateSkillScore(List<String> candidateSkills, List<String> requiredSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) return 100.0; // No skill requirement
        if (candidateSkills == null || candidateSkills.isEmpty()) return 0.0;

        long matches = candidateSkills.stream()
                .map(String::toLowerCase)
                .filter(requiredSkills.stream().map(String::toLowerCase).toList()::contains)
                .count();

        return ((double) matches / requiredSkills.size()) * 100.0;
    }

    private double calculateExperienceScore(int candidateYears, int minYears, int maxYears) {
        if (candidateYears < minYears) {
            // Candidate is under-qualified
            return Math.max(0.0, ((double) candidateYears / minYears) * 100.0);
        } else if (candidateYears > maxYears) {
            // Candidate is over-qualified (slight penalty)
            double diff = candidateYears - maxYears;
            return Math.max(70.0, 100.0 - (diff * 5)); // lose 5 points per extra year
        } else {
            return 100.0; // Within range
        }
    }

    private double calculateEducationScore(String candidateEducation, String requiredEducation) {
        if (requiredEducation == null || requiredEducation.isBlank()) return 100.0;
        if (candidateEducation == null) return 0.0;

        return candidateEducation.equalsIgnoreCase(requiredEducation) ? 100.0 : 0.0;
    }
}
