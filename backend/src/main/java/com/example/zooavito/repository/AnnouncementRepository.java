package com.example.zooavito.repository;

import com.example.zooavito.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends
        JpaRepository<Announcement, Long>,
        JpaSpecificationExecutor<Announcement> {
    List<Announcement> findByUserId(Long userId);
}
