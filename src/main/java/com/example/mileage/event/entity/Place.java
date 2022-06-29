package com.example.mileage.event.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * PLACE Entity
 */
@Entity
@Table(name = "PLACE")
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    @Id
    @Column(name = "PLACE_ID", columnDefinition = "binary(16)", nullable = false)
    private UUID placeId;           //  장소아이디(UUID)

    @Column(name = "CREATED_AT", columnDefinition = "timestamp default current_timestamp", nullable = false)
    @CreationTimestamp
    private Timestamp createdAt;    //  생성일시

    public Place() {

    }
}
