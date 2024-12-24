package com.capstone.project.chronos.jobscheduling.entity;

import com.capstone.project.chronos.auth.entity.User;
import com.capstone.project.chronos.jobscheduling.enums.ScheduledType;
import com.capstone.project.chronos.jobscheduling.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "jobs")
public class Jobs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "job_name")
    private String jobName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "job_type", nullable = false)
    private String jobType;

    @Column(name = "parameters", columnDefinition = "JSON")
    private String parameters;

    @Column(name = "cron_expression", nullable = false)
    private String cronExpression;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", nullable = false)
    private ScheduledType scheduleType;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "max_retries", nullable = false)
    private Integer maxRetries;


    @PrePersist
    public void setDefaultMaxRetries() {
        if (this.maxRetries == null) {
            this.maxRetries = 3; // Default value
        }
    }

}