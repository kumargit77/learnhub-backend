package com.learnhub.dto;

import lombok.Data;
import java.util.Map;
import java.util.UUID;

@Data
public class AssessmentSubmissionRequest {
    private UUID studentId;
    private UUID courseId;
    private Map<Integer, String> answers;
}
