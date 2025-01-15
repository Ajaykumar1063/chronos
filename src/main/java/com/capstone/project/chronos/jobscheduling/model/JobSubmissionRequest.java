package com.capstone.project.chronos.jobscheduling.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobSubmissionRequest {

    private Long userId;

    private String jobName;

    private String jobDescription;

    private String jobType;

    private String recurringType;

    private String scheduleType; // Expected values: "ONCE" or "RECURRING"

    private LocalDate scheduleExpression; // ISO date-time for one-time or cron expression for recurring



}

