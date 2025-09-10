package com.resumeshortlist.service;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ResumeParsingService {

    private static final List<String> COMMON_SKILLS = Arrays.asList(
        "java", "python", "javascript", "react", "angular", "spring", "hibernate",
        "mysql", "postgresql", "mongodb", "docker", "kubernetes", "aws", "azure",
        "git", "jenkins", "maven", "gradle", "html", "css", "node.js", "express",
        "microservices", "rest api", "graphql", "junit", "mockito", "redis",
        "elasticsearch", "kafka", "rabbitmq", "linux", "agile", "scrum"
    );

    public String extractTextFromPDF(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    public List<String> extractSkills(String resumeText) {
        List<String> extractedSkills = new ArrayList<>();
        String lowerCaseText = resumeText.toLowerCase();
        
        for (String skill : COMMON_SKILLS) {
            if (lowerCaseText.contains(skill.toLowerCase())) {
                extractedSkills.add(skill);
            }
        }
        
        return extractedSkills;
    }

    public Integer extractExperience(String resumeText) {
        // Look for patterns like "3 years experience", "5+ years", etc.
        Pattern experiencePattern = Pattern.compile("(\\d+)\\+?\\s*(years?|yrs?)\\s*(of\\s*)?(experience|exp)", 
                                                   Pattern.CASE_INSENSITIVE);
        Matcher matcher = experiencePattern.matcher(resumeText);
        
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        
        return 0; // Default to 0 if not found
    }

    public String extractEducation(String resumeText) {
        String[] educationKeywords = {"bachelor", "master", "phd", "b.tech", "m.tech", 
                                    "b.sc", "m.sc", "mba", "diploma"};
        String lowerCaseText = resumeText.toLowerCase();
        
        for (String keyword : educationKeywords) {
            if (lowerCaseText.contains(keyword)) {
                return keyword.toUpperCase();
            }
        }
        
        return "Not Specified";
    }

    public String extractEmail(String resumeText) {
        Pattern emailPattern = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b");
        Matcher matcher = emailPattern.matcher(resumeText);
        
        if (matcher.find()) {
            return matcher.group();
        }
        
        return null;
    }

    public String extractPhone(String resumeText) {
        Pattern phonePattern = Pattern.compile("(\\+\\d{1,3}\\s?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}");
        Matcher matcher = phonePattern.matcher(resumeText);
        
        if (matcher.find()) {
            return matcher.group();
        }
        
        return null;
    }
}