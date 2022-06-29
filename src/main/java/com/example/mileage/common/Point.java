package com.example.mileage.common;

import lombok.Getter;

@Getter
public enum Point {
    ContentAdd(1, "내용 작성으로 1점 추가"),
    ContentDelete(-1, "내용 삭제로 1점 삭제"),
    ImageAdd(1, "이미지 첨부로 1점 추가"),
    ImageDelete(-1, "이미지 삭제로 1점 삭제"),
    BonusAdd(1, "장소 첫 리뷰 작성으로 보너스 1점 추가"),
    BonusDelete(-1, "장소 첫 리뷰 삭제로 보너스 1점 삭제");

    private final int score;
    private final String reason;

    Point(int score, String reason) {
        this.score = score;
        this.reason = reason;
    }
}
