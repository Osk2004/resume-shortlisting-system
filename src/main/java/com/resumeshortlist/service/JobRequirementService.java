package com.resumeshortlist.service;

import com.resumeshortlist.entity.JobRequirement;
import com.resumeshortlist.repository.JobRequirementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobRequirementService {

    @Autowired
    private JobRequirementRepository jobRequirementRepository;

    public JobRequirement createJobRequirement(JobRequirement jobRequirement) {
        return jobRequirementRepository.save(jobRequirement);
    }

    public List<JobRequirement> getAllJobRequirements() {
        return jobRequirementRepository.findAll();
    }

    public List<JobRequirement> getActiveJobRequirements() {
        return jobRequirementRepository.findByIsActiveTrue();
    }

    public Optional<JobRequirement> getJobRequirementById(Long id) {
        return jobRequirementRepository.findById(id);
    }

    public JobRequirement updateJobRequirement(Long id, JobRequirement updatedJob) {
        return jobRequirementRepository.findById(id)
                .map(job -> {
                    job.setJobTitle(updatedJob.getJobTitle());
                    job.setJobDescription(updatedJob.getJobDescription());
                    job.setRequiredSkills(updatedJob.getRequiredSkills());
                    job.setMinExperience(updatedJob.getMinExperience());
                    job.setMaxExperience(updatedJob.getMaxExperience());
                    job.setRequiredEducation(updatedJob.getRequiredEducation());
                    job.setIsActive(updatedJob.getIsActive());
                    return jobRequirementRepository.save(job);
                })
                .orElse(null);
    }

    public void deleteJobRequirement(Long id) {
        jobRequirementRepository.deleteById(id);
    }

    public void deactivateJobRequirement(Long id) {
        jobRequirementRepository.findById(id)
                .ifPresent(job -> {
                    job.setIsActive(false);
                    jobRequirementRepository.save(job);
                });
    }
}