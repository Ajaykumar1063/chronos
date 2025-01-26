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
public class RescheduleJobRequest {

    private String jobName;
    private String jobDescription;
    private LocalDateTime newScheduleExpression;
    private String recurringType;
    private String jobType;


}

