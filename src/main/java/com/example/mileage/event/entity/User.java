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
 * User Entity
 */
@Entity
@Table(name = "USER_TABLE")
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @Column(name = "USER_ID", columnDefinition = "binary(16)", nullable = false)
    private UUID userId;            //  유저아이디(UUID)

    @Column(name = "CREATED_AT", columnDefinition = "timestamp default current_timestamp", nullable = false)
    @CreationTimestamp
    private Timestamp createdAt;    //  생성일시

    public User() {}
}
