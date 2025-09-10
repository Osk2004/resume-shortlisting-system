package com.resumeshortlist.controller;

import com.resumeshortlist.dto.ResumeDTO;
import com.resumeshortlist.entity.Resume;
import com.resumeshortlist.service.ResumeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/resumes")
@CrossOrigin(origins = "*")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @PostMapping("/upload")
    public ResponseEntity<Resume> uploadResume(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @Valid @ModelAttribute ResumeDTO resumeDTO) {
        try {
            Resume resume = resumeService.uploadAndProcessResume(file, resumeDTO);
            return ResponseEntity.ok(resume);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{resumeId}/shortlist/{jobId}")
    public ResponseEntity<String> processShortlisting(
            @PathVariable Long resumeId,
            @PathVariable Long jobId) {
        resumeService.processShortlisting(resumeId, jobId);
        return ResponseEntity.ok("Shortlisting process completed");
    }

    @GetMapping
    public ResponseEntity<List<Resume>> getAllResumes() {
        List<Resume> resumes = resumeService.getAllResumes();
        return ResponseEntity.ok(resumes);
    }

    @GetMapping("/shortlisted")
    public ResponseEntity<List<Resume>> getShortlistedResumes() {
        List<Resume> resumes = resumeService.getShortlistedResumes();
        return ResponseEntity.ok(resumes);
    }

    @GetMapping("/by-score")
    public ResponseEntity<List<Resume>> getResumesByScore(@RequestParam Double minScore) {
        List<Resume> resumes = resumeService.getResumesByScore(minScore);
        return ResponseEntity.ok(resumes);
    }

    @GetMapping("/sorted-by-score")
    public ResponseEntity<List<Resume>> getResumesSortedByScore() {
        List<Resume> resumes = resumeService.getResumesSortedByScore();
        return ResponseEntity.ok(resumes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resume> getResumeById(@PathVariable Long id) {
        Optional<Resume> resume = resumeService.getResumeById(id);
        return resume.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResume(@PathVariable Long id) {
        resumeService.deleteResume(id);
        return ResponseEntity.ok().build();
    }
}