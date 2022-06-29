package com.example.mileage.event.repository;

import com.example.mileage.event.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlaceRepository extends JpaRepository<Place, UUID> {
}
