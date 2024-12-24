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
public class ViewJobResponse {
    private Integer id;
    private Long userId;
    private String jobName;
    private String jobType;
    private String scheduleType;
    private String status;
    private String scheduleExpression;
    private Integer retryCount;
    private LocalDate createdAt;
    private LocalDate updatedAt;


}

