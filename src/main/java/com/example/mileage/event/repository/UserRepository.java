package com.example.mileage.event.repository;

import com.example.mileage.event.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository <User, UUID>{
}
