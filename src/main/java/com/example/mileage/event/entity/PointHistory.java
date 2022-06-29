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
 * PointHistory Entity
 */
@Entity
@Table(name = "POINT_HISTORY")
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory {

    @Id
    @Column(name = "HISTORY_ID", columnDefinition = "binary(16)", nullable = false)
    private UUID historyId;         //  히스토리아이디(UUID)

    @Column(name = "USER_ID", columnDefinition = "binary(16)", nullable = false)
    private UUID userId;            //  유저아이디(UUID)

    @Column(name = "REVIEW_ID", columnDefinition = "binary(16)", nullable = false)
    private UUID reviewId;          //  리뷰아이디(UUID)

    @Column(name = "POINT", columnDefinition = "int", nullable = false)
    private int point;              //  포인트 변동 값

    @Column(name = "TAG", columnDefinition = "char(1)", nullable = false)
    private char tag;               // 포인트 변동 태그(C:내용, I:이미지, B:첫글)

    @Column(name = "REASON", columnDefinition = "varchar(50)", nullable = false)
    private String reason;          //  포인트 변동 이유(작성,삭제 등..)

    @Column(name = "CREATED_AT", columnDefinition = "timestamp default current_timestamp", nullable = false)
    @CreationTimestamp
    private Timestamp createdAt;    //  생성일시

    public PointHistory() {

    }
}

