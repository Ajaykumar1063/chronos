package com.capstone.project.chronos.jobscheduling.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobSubmissionRequest {

    private Long userId;

    private String jobName;

    private String jobType;

    private String scheduleType; // Expected values: "ONCE" or "RECURRING"

    private String scheduleExpression; // ISO date-time for one-time or cron expression for recurring

    private Map<String, Object> payload;


}

