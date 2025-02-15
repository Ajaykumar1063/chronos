package com.capstone.project.chronos.jobscheduling.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobSubmissionResponse {
    private Long id;
    private String status;
    private String message;

    public JobSubmissionResponse(String s) {
    }
}

