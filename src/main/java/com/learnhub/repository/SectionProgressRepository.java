package com.learnhub.repository;

import com.learnhub.model.SectionProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SectionProgressRepository extends JpaRepository<SectionProgress, UUID> {
    List<SectionProgress> findByEnrollmentId(UUID enrollmentId);
    Optional<SectionProgress> findByEnrollmentIdAndSectionId(UUID enrollmentId, UUID sectionId);
}
