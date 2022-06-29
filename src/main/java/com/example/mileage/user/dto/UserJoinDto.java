package com.example.mileage.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 유저 DTO
 */
@Getter
@Setter
@Builder
public class UserJoinDto {
    @NotBlank(message = "유저아이디는 필수 입니다.")
    @Size(min = 36, max = 36, message = "유저아이디는 UUID(36자리) 형태만 가능합니다.")
    private String userId;  //  유저아이디
    @NotBlank(message = "유저이름은 필수 입니다.")
    @Size(min = 2, max = 20, message = "유저이름은 2~20만 가능합니다.")
    private String name;    //  유저이름
}
