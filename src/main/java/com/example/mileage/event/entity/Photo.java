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
 * Photo Entity
 */
@Entity
@Table(name = "PHOTO")
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Photo {

    @Id
    @Column(name = "PHOTO_ID", columnDefinition = "binary(16)", nullable = false)
    private UUID photoId;           //  포토아이디(UUID)

    @ManyToOne
    @JoinColumn(name = "REVIEW_ID")
    private Review review;         //  리뷰정보(FK)

    @Column(name = "CREATED_AT", columnDefinition = "timestamp default current_timestamp", nullable = false)
    @CreationTimestamp
    private Timestamp createdAt;    //  생성일시

    public Photo() {

    }
}
