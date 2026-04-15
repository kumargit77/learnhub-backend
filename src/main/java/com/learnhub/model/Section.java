package com.learnhub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "sections")
@Data
@NoArgsConstructor
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnore
    private Course course;

    @Column(nullable = false)
    private String name;

    @Column(name = "ppt_url", nullable = false)
    private String pptUrl;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Column(name = "duration_mins", nullable = false)
    private Integer durationMins;
}
