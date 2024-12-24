package com.capstone.project.chronos.jobscheduling.controller;


import com.capstone.project.chronos.jobscheduling.model.JobSubmissionRequest;
import com.capstone.project.chronos.jobscheduling.model.JobSubmissionResponse;
import com.capstone.project.chronos.jobscheduling.model.RescheduleJobRequest;
import com.capstone.project.chronos.jobscheduling.model.ViewJobResponse;
import com.capstone.project.chronos.jobscheduling.service.JobsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
public class JobsController {

    @Autowired
    private JobsService jobsService;

    /**
     * 1. Job Submission API
     */
    @PostMapping
    public ResponseEntity<JobSubmissionResponse> submitJob(@Valid @RequestBody JobSubmissionRequest request) {
        JobSubmissionResponse response = jobsService.submitJob(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * 2. View Job Status API
     */
    @GetMapping("/{job_id}")
    public ResponseEntity<ViewJobResponse> viewJobStatus(@PathVariable("job_id") Integer jobId) {
        ViewJobResponse response = jobsService.viewJobStatus(jobId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 3. Cancel Job API
     */
    @PostMapping("/{job_id}/cancel")
    public ResponseEntity<JobSubmissionResponse> cancelJob(@PathVariable("job_id") Integer jobId) {
        JobSubmissionResponse response = jobsService.cancelJob(jobId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 4. Reschedule Job API
     */
    @PostMapping("/{job_id}/reschedule")
    public ResponseEntity<JobSubmissionResponse> rescheduleJob(
            @PathVariable("job_id") Integer jobId,
            @Valid @RequestBody RescheduleJobRequest request) {
        JobSubmissionResponse response = jobsService.rescheduleJob(jobId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

