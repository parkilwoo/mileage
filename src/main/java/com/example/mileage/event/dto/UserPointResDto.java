package com.example.mileage.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;

/**
 * 현재 포인트 조회 Dto
 */
@Builder
@Getter
public class UserPointResDto {
    private int totalPoint;                     //  총 포인트
    private List<PointDetail> pointDetails;     //  포인트 내역 리스트

    /**
     * Point 내역 Class
     */
    @Builder
    @Getter
     public static class PointDetail {
        String reviewId;        //  리뷰아이디
        String reason;          //  포인트 증감 이유
        int point;              //  포인트 증감값
        Timestamp createdAt;    //  포인트 증감 생성일시
    }
}
