package com.capstone.project.chronos.jobscheduling.repository;

import com.capstone.project.chronos.jobscheduling.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
