package com.resumeshortlist.repository;

import com.resumeshortlist.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    
    List<Resume> findByShortlistStatus(String status);
    
    List<Resume> findByMatchScoreGreaterThanEqual(Double minScore);
    
    @Query("SELECT r FROM Resume r WHERE r.experienceYears BETWEEN :minExp AND :maxExp")
    List<Resume> findByExperienceRange(@Param("minExp") Integer minExp, @Param("maxExp") Integer maxExp);
    
    @Query("SELECT r FROM Resume r JOIN r.skills s WHERE LOWER(s) IN :skills")
    List<Resume> findBySkillsIn(@Param("skills") List<String> skills);
    
    List<Resume> findByOrderByMatchScoreDesc();
}
