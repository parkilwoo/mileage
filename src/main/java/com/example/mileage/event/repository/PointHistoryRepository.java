package com.example.mileage.event.repository;

import com.example.mileage.event.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PointHistoryRepository extends JpaRepository<PointHistory, UUID> {
    List<PointHistory> findAllByReviewIdAndTag(UUID reviewId, char tag);
    List<PointHistory> findAllByUserIdOrderByCreatedAtDesc(UUID userId);
}
