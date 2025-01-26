package com.capstone.project.chronos.jobscheduling.service;


import com.capstone.project.chronos.jobscheduling.exception.JobNotFoundException;
import com.capstone.project.chronos.jobscheduling.model.JobSubmissionRequest;
import com.capstone.project.chronos.jobscheduling.model.JobSubmissionResponse;
import com.capstone.project.chronos.jobscheduling.model.RescheduleJobRequest;
import com.capstone.project.chronos.jobscheduling.model.ViewJobResponse;

import java.util.List;

public interface JobsService {

    JobSubmissionResponse submitJob(JobSubmissionRequest request) throws Exception;

    ViewJobResponse viewJobStatus(Long jobId)throws JobNotFoundException;

    List<ViewJobResponse> viewAllJobs()  throws Exception;

    JobSubmissionResponse cancelJob(Long jobId);

    JobSubmissionResponse rescheduleJob(Integer jobId, RescheduleJobRequest request);
}

