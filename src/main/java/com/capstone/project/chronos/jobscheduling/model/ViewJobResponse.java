package com.capstone.project.chronos.jobscheduling.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ViewJobResponse {
    private Long id;
    private Long userId;
    private String jobName;
    private String jobDescription;
    private String jobType;
    private String recurringType;
    private String scheduleType;
    private String status;
    private LocalDateTime cronExpressionDate;
    private Integer retryCount;
    private LocalDate createdAt;
    private LocalDate updatedAt;


    public ViewJobResponse(String s) {
    }
}

