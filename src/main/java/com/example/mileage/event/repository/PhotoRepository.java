package com.example.mileage.event.repository;

import com.example.mileage.event.entity.Photo;
import com.example.mileage.event.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PhotoRepository extends JpaRepository<Photo, UUID> {
    List<Photo> findAllByReview(Review review);
}
