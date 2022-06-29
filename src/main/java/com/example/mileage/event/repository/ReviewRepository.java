package com.example.mileage.event.repository;

import com.example.mileage.event.entity.Place;
import com.example.mileage.event.entity.Review;
import com.example.mileage.event.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findAllByPlace(Place place);
    Optional<Review> findByUserAndPlace(User user, Place place);
}
