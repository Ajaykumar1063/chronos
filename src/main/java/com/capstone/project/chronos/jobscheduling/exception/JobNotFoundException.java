package com.capstone.project.chronos.jobscheduling.exception;


public class JobNotFoundException extends RuntimeException {
    public JobNotFoundException(String message) {
        super(message);
    }
}

