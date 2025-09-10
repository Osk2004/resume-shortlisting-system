package com.resumeshortlist.service;

import com.resumeshortlist.dto.ResumeDTO;
import com.resumeshortlist.entity.JobRequirement;
import com.resumeshortlist.entity.Resume;
import com.resumeshortlist.repository.JobRequirementRepository;
import com.resumeshortlist.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ResumeService {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private JobRequirementRepository jobRequirementRepository;

    @Autowired
    private ResumeParsingService parsingService;

    @Autowired
    private ShortlistingService shortlistingService;

    public Resume uploadAndProcessResume(MultipartFile file, ResumeDTO resumeDTO) throws IOException {
        String resumeText = "";
        
        if (file != null && !file.isEmpty()) {
            if (file.getContentType().equals("application/pdf")) {
                resumeText = parsingService.extractTextFromPDF(file);
            } else {
                resumeText = new String(file.getBytes());
            }
        }

        Resume resume = new Resume();
        resume.setCandidateName(resumeDTO.getCandidateName());
        resume.setEmail(resumeDTO.getEmail());
        resume.setPhone(resumeDTO.getPhone());
        resume.setResumeText(resumeText);
        resume.setFileName(file != null ? file.getOriginalFilename() : null);
        resume.setSkills(resumeDTO.getSkills());
        resume.setExperienceYears(resumeDTO.getExperienceYears());
        resume.setEducation(resumeDTO.getEducation());

        // Auto-extract information if not provided
        if (resumeText != null && !resumeText.isEmpty()) {
            if (resume.getSkills() == null || resume.getSkills().isEmpty()) {
                resume.setSkills(parsingService.extractSkills(resumeText));
            }
            
            if (resume.getExperienceYears() == null || resume.getExperienceYears() == 0) {
                resume.setExperienceYears(parsingService.extractExperience(resumeText));
            }
            
            if (resume.getEducation() == null) {
                resume.setEducation(parsingService.extractEducation(resumeText));
            }
        }

        return resumeRepository.save(resume);
    }

    public void processShortlisting(Long resumeId, Long jobRequirementId) {
        Optional<Resume> resumeOpt = resumeRepository.findById(resumeId);
        Optional<JobRequirement> jobReqOpt = jobRequirementRepository.findById(jobRequirementId);

        if (resumeOpt.isPresent() && jobReqOpt.isPresent()) {
            Resume resume = resumeOpt.get();
            JobRequirement jobRequirement = jobReqOpt.get();

            double matchScore = shortlistingService.calculateMatchScore(resume, jobRequirement);
            String status = shortlistingService.determineShortlistStatus(matchScore);

            resume.setMatchScore(matchScore);
            resume.setShortlistStatus(status);
            
            resumeRepository.save(resume);
        }
    }

    public List<Resume> getShortlistedResumes() {
        return resumeRepository.findByShortlistStatus("SHORTLISTED");
    }

    public List<Resume> getAllResumes() {
        return resumeRepository.findAll();
    }

    public List<Resume> getResumesByScore(Double minScore) {
        return resumeRepository.findByMatchScoreGreaterThanEqual(minScore);
    }

    public Optional<Resume> getResumeById(Long id) {
        return resumeRepository.findById(id);
    }

    public void deleteResume(Long id) {
        resumeRepository.deleteById(id);
    }

    public List<Resume> getResumesSortedByScore() {
        return resumeRepository.findByOrderByMatchScoreDesc();
    }
}