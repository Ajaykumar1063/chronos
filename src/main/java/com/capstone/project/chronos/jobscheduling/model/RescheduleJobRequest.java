package com.capstone.project.chronos.jobscheduling.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RescheduleJobRequest {

    private String jobName;
    private String jobDescription;
    private LocalDate newScheduleExpression;
    private String recurringType;
    private String jobType;


}

