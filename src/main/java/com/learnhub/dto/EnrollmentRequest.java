package com.learnhub.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class EnrollmentRequest {
    private UUID studentId;
    private UUID courseId;
}
