package com.example.mileage.event.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * REVIEW Entity
 */
@Entity
@Table(name = "REVIEW")
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class Review {
    @Id
    @Column(name = "REVIEW_ID", columnDefinition = "binary(16)", nullable = false)
    private UUID reviewId;        // 리뷰아이디(UUID)

    @Lob
    @Column(name = "CONTENT", columnDefinition = "text")
    private String content;       // 리뷰내용

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;          // 유저정보(FK)

    @ManyToOne
    @JoinColumn(name = "PLACE_ID")
    private Place place;       // 장소정보(FK)

    @Column(name = "CREATED_AT", columnDefinition = "timestamp default current_timestamp", nullable = false)
    @CreationTimestamp
    private Timestamp createdAt;  // 생성일시

    @Column(name = "UPDATED_AT", columnDefinition = "timestamp")
    private Timestamp updateAt;   // 수정일시

    public Review() {

    }
}
