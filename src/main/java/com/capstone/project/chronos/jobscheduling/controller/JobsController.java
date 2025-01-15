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

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

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
        try {
            JobSubmissionResponse response = jobsService.submitJob(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new JobSubmissionResponse("Invalid input: " + ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new JobSubmissionResponse("Failed to submit the job."));
        }
    }

    /**
     * 2. View Job Status API
     */
    @GetMapping("/{job_id}")
    public ResponseEntity<ViewJobResponse> viewJobStatus(@PathVariable("job_id") Long jobId) {
        try {
            ViewJobResponse response = jobsService.viewJobStatus(jobId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ViewJobResponse("Job not found for ID: " + jobId));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ViewJobResponse("Failed to fetch job status."));
        }
    }

    /**
     * 3. View All Jobs Status API By User
     */
    @GetMapping
    public ResponseEntity<List<ViewJobResponse>> viewJobStatusByUser() {
        try {
            List<ViewJobResponse> response = jobsService.viewAllJobs();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonList(new ViewJobResponse("Failed to fetch all jobs.")));
        }
    }

    /**
     * 4. Cancel Job API
     */
    @PostMapping("/{job_id}/cancel")
    public ResponseEntity<JobSubmissionResponse> cancelJob(@PathVariable("job_id") Long jobId) {
        try {
            JobSubmissionResponse response = jobsService.cancelJob(jobId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new JobSubmissionResponse("Job not found for ID: " + jobId));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new JobSubmissionResponse("Failed to cancel the job."));
        }
    }

    /**
     * 5. Reschedule Job API
     */
    @PostMapping("/{job_id}/reschedule")
    public ResponseEntity<JobSubmissionResponse> rescheduleJob(
            @PathVariable("job_id") Integer jobId,
            @Valid @RequestBody RescheduleJobRequest request) {
        try {
            JobSubmissionResponse response = jobsService.rescheduleJob(jobId, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new JobSubmissionResponse("Job not found for ID: " + jobId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new JobSubmissionResponse("Invalid input: " + ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new JobSubmissionResponse("Failed to reschedule the job."));
        }
    }
}

