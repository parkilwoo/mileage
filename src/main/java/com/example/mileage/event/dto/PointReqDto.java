package com.example.mileage.event.dto;

import com.example.mileage.common.Action;
import com.example.mileage.common.Type;
import com.example.mileage.common.validator.EnumValid;
import com.example.mileage.common.validator.UUIDValid;
import com.example.mileage.event.entity.Place;
import com.example.mileage.event.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * Point 적립 Request DTO
 */
@Builder
@Getter
@ToString
public class PointReqDto {
    @NotBlank(message = "type은 필수 값 입니다.")
    @EnumValid(enumClass = Type.class, message = "type값은 REVIEW 값만 가능합니다.")
    private String type;                    //  타입은 REVIEW 고정

    @NotBlank(message = "action은 필수 값 입니다.")
    @EnumValid(enumClass = Action.class, message = "action 값은 ADD, MOD, DELETE 값만 가능합니다.")
    private String action;                  //  액션(ADD, MOD, DELETE)

    @NotBlank(message = "reviewId는 필수 값 입니다.")
    @UUIDValid(message = "reviewId 값은 36자의 UUID형식 값만 가능합니다.")
    private String reviewId;                //  리뷰아이디(UUID)

    @Length(max = 2000, message = "content는 최대 2000자 입니다.")
    private String content;                 //  내용

    private String[] attachedPhotoIds;      //  첨부 이미지아이디 배열

    @NotBlank(message = "userId는 필수 값 입니다.")
    @UUIDValid(message = "userId 값은 36자의 UUID형식 값만 가능합니다.")
    private String userId;                  //  유저아이디(UUID)

    @NotBlank(message = "placeId는 필수 값 입니다.")
    @UUIDValid(message = "placeId 값은 36자의 UUID형식 값만 가능합니다.")
    private String placeId;                 //  장소아이디(UUID)

    private User user;          //  userID To User
    private Place place;        //  placeID TO Place

    public void setUser(User user) {
        this.user = user;
    }

    public void setPlace(Place place) {
        this.place = place;
    }
}
