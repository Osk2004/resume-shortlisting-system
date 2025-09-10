package com.resumeshortlist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class ResumeDTO {
    @NotBlank(message = "Candidate name is required")
    private String candidateName;
    
    @NotBlank(message = "Email is required")
    private String email;
    
    private String phone;
    private List<String> skills;
    
    @NotNull(message = "Experience years is required")
    private Integer experienceYears;
    
    private String education;

    // Constructors
    public ResumeDTO() {}

    public ResumeDTO(String candidateName, String email, String phone, 
                    List<String> skills, Integer experienceYears, String education) {
        this.candidateName = candidateName;
        this.email = email;
        this.phone = phone;
        this.skills = skills;
        this.experienceYears = experienceYears;
        this.education = education;
    }

    // Getters and Setters
    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }
}
