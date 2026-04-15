package com.learnhub.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class CourseRequestDTO {
    private String name;
    private String desc;
    private Integer duration;
    private UUID createdBy;
    private List<SectionDTO> sections;
    private List<QuestionDTO> questions;

    @Data
    public static class SectionDTO {
        private String name;
        private String fileName;
        private Integer durationMins;
    }

    @Data
    public static class QuestionDTO {
        private String text;
        private String a;
        private String b;
        private String c;
        private String d;
        private String correct;
    }
}
