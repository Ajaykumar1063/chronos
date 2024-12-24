package com.capstone.project.chronos.jobscheduling.service;


import com.capstone.project.chronos.jobscheduling.model.JobSubmissionRequest;
import com.capstone.project.chronos.jobscheduling.model.JobSubmissionResponse;
import com.capstone.project.chronos.jobscheduling.model.RescheduleJobRequest;
import com.capstone.project.chronos.jobscheduling.model.ViewJobResponse;

public interface JobsService {

    JobSubmissionResponse submitJob(JobSubmissionRequest request);

    ViewJobResponse viewJobStatus(Integer jobId);

    JobSubmissionResponse cancelJob(Integer jobId);

    JobSubmissionResponse rescheduleJob(Integer jobId, RescheduleJobRequest request);
}

