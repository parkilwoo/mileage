package com.example.mileage;

import com.example.mileage.common.MessageSourceToStr;
import com.example.mileage.common.Point;
import com.example.mileage.common.Result;
import com.example.mileage.common.exception.CustomException;
import com.example.mileage.common.exception.CustomExceptionEnum;
import com.example.mileage.event.dto.PointReqDto;
import com.example.mileage.event.dto.UserPointResDto;
import com.example.mileage.event.service.EventService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("E2E Test")
class MileageApplicationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext ctx;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // ?????? ??????
                .alwaysDo(print())
                .build();
    }

    @Test
    @DisplayName("????????? ??????, ??????, ?????? & ????????? ?????? ?????????")
    void e2eTest() throws Exception {
        //  1. Message Source Test
        assert MessageSourceToStr.RESULT_SUCCESS_CODE.equals("000") : "MessageSource.RESULT_SUCCESS_CODE ??????!";
        assert MessageSourceToStr.RESULT_SUCCESS_MSG.equals("??????") : "MessageSource.RESULT_SUCCESS_MSG ??????!";
        assert MessageSourceToStr.RESULT_FAIL_CODE.equals("999") : "MessageSource.RESULT_FAIL_CODE ??????!";
        assert MessageSourceToStr.RESULT_FAIL_MSG.equals("??????") : "MessageSource.RESULT_FAIL_MSG ??????!";
        assert MessageSourceToStr.DUPLICATE_REVIEW_ID.equals("?????? ????????? reviewId ?????????.") : "MessageSource.DUPLICATE_REVIEW_ID ??????!";
        assert MessageSourceToStr.MOD_ONLY_WRITER.equals("???????????? ????????? ??? ????????????.") : "MessageSource.MOD_ONLY_WRITER ??????!";
        assert MessageSourceToStr.DELETE_ONLY_WRITER.equals("???????????? ????????? ??? ????????????.") : "MessageSource.DELETE_ONLY_WRITER ??????!";
        assert MessageSourceToStr.NOT_REGISTERED_REVIEW_ID.equals("???????????? ?????? reviewId ?????????.") : "MessageSource.NOT_REGISTERED_REVIEW_ID ??????!";

        // 2. ????????? ?????? Test
        // 2-1. ?????? ??? ?????? ?????? Test(Bonus??????)
        // given
        String bonusUserId = "3ede0ef2-92b7-4817-a5f3-0c575361f745";
        PointReqDto bonusRequestDto =
                PointReqDto.builder()
                        .type("REVIEW")
                        .action("ADD")
                        .reviewId("240a0658-dc5f-4878-9381-ebb7b2667772")
                        .content("?????????!")
                        .attachedPhotoIds(new String[]{"e4d1a64e-a531-46de-88d0-ff0ed70c0bb8", "afb0cef2-851d-4a50-bb07-9cc15cbdc332"})
                        .userId(bonusUserId)
                        .placeId("2e4baf1c-5acb-4efb-a1af-eddada31b00f")
                        .build();
        String reqBody = objectMapper.writeValueAsString(bonusRequestDto);


        // when & then
        this.mockMvc.perform(post("/event")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        // given
        List<UserPointResDto.PointDetail> bonusCheckPointDetails = new ArrayList<>();
        bonusCheckPointDetails.add(
                UserPointResDto.PointDetail.builder()
                        .reviewId("240a0658-dc5f-4878-9381-ebb7b2667772")
                        .point(Point.BonusAdd.getScore())
                        .reason(Point.BonusAdd.getReason())
                        .build()
        );
        bonusCheckPointDetails.add(
                UserPointResDto.PointDetail.builder()
                        .reviewId("240a0658-dc5f-4878-9381-ebb7b2667772")
                        .point(Point.ImageAdd.getScore())
                        .reason(Point.ImageAdd.getReason())
                        .build()
        );
        bonusCheckPointDetails.add(
                UserPointResDto.PointDetail.builder()
                        .reviewId("240a0658-dc5f-4878-9381-ebb7b2667772")
                        .point(Point.ContentAdd.getScore())
                        .reason(Point.ContentAdd.getReason())
                        .build()
        );

        UserPointResDto bonusCheckResDto =
                UserPointResDto.builder()
                        .totalPoint(3)
                        .pointDetails(bonusCheckPointDetails)
                        .build();
        // when & then
        this.mockMvc.perform(get("/event/point/{userId}", bonusUserId))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Result successResult = objectMapper.readValue(result.getResponse().getContentAsString(), Result.class);
                    String successResultToStr = objectMapper.writeValueAsString(successResult.getData());
                    UserPointResDto pointResDto = objectMapper.readValue(successResultToStr, UserPointResDto.class);
                    assertThat(pointResDto.getTotalPoint()).usingRecursiveComparison().isEqualTo(bonusCheckResDto.getTotalPoint());
                    assertThat(pointResDto.getPointDetails()).usingRecursiveComparison().ignoringFields("createdAt").isEqualTo(bonusCheckResDto.getPointDetails());
                })
                .andDo(print());

        // 2-2 ?????? ?????? ?????? ?????? (Bonus & ????????? ??????)
        // given
        String notBonusUserId = "3ede0ef2-1b26-5921-a5f3-0c575361f745";
        String notBonusRandomUUID = UUID.randomUUID().toString();
        PointReqDto firsRequestDto =
                PointReqDto.builder()
                        .type("REVIEW")
                        .action("ADD")
                        .reviewId(UUID.randomUUID().toString())
                        .content("?????? ??????!!")
                        .userId(UUID.randomUUID().toString())
                        .placeId("2e4baf1c-5acb-4efb-a1af-eddada31b11f")
                        .build();

        reqBody = objectMapper.writeValueAsString(firsRequestDto);
        // when & then
        this.mockMvc.perform(post("/event")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        // given
        PointReqDto notBonusRequestDto =
                PointReqDto.builder()
                        .type("REVIEW")
                        .action("ADD")
                        .reviewId(notBonusRandomUUID)
                        .content("???????????? ??????!!")
                        .userId(notBonusUserId)
                        .placeId("2e4baf1c-5acb-4efb-a1af-eddada31b11f")
                        .build();
        reqBody = objectMapper.writeValueAsString(notBonusRequestDto);
        // when & then
        this.mockMvc.perform(post("/event")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        // given
        List<UserPointResDto.PointDetail> notBonusCheckPointDetails = new ArrayList<>();
        notBonusCheckPointDetails.add(
                UserPointResDto.PointDetail.builder()
                        .reviewId(notBonusRandomUUID)
                        .point(Point.ContentAdd.getScore())
                        .reason(Point.ContentAdd.getReason())
                        .build()
        );
        UserPointResDto notBonusCheckResDto =
                UserPointResDto.builder()
                        .totalPoint(1)
                        .pointDetails(notBonusCheckPointDetails)
                        .build();
        // when & then
        this.mockMvc.perform(get("/event/point/{userId}", notBonusUserId))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Result successResult = objectMapper.readValue(result.getResponse().getContentAsString(), Result.class);
                    String successResultToStr = objectMapper.writeValueAsString(successResult.getData());
                    UserPointResDto pointResDto = objectMapper.readValue(successResultToStr, UserPointResDto.class);
                    assertThat(pointResDto.getTotalPoint()).usingRecursiveComparison().isEqualTo(notBonusCheckResDto.getTotalPoint());
                    assertThat(pointResDto.getPointDetails()).usingRecursiveComparison().ignoringFields("createdAt").isEqualTo(notBonusCheckResDto.getPointDetails());
                })
                .andDo(print());

        // 2-3 ????????? ???????????? ????????? ?????? ?????? ????????? Exception
        // given
        PointReqDto duplicateUserPlaceDto =
                PointReqDto.builder()
                        .type("REVIEW")
                        .action("ADD")
                        .reviewId(UUID.randomUUID().toString())
                        .content("?????????!!")
                        .userId(notBonusUserId)
                        .placeId("2e4baf1c-5acb-4efb-a1af-eddada31b11f")
                        .build();
        reqBody = objectMapper.writeValueAsString(duplicateUserPlaceDto);

        // when & then
        this.mockMvc.perform(post("/event")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Result failResult = objectMapper.readValue(result.getResponse().getContentAsString(), Result.class);
                    assertEquals(failResult.getCode(), CustomExceptionEnum.BUSINESS_EXCEPTION.getCode());
                    assertEquals(failResult.getMsg(), MessageSourceToStr.ONLY_ONE_USER_PLACE);
                })
                .andDo(print());

        // 3. ????????? ?????? Test
        // 3-1. ?????? ???,????????? ?????? ???????????? ???,????????? ?????? Test
        // given
        PointReqDto bonusModRequestDto =
                PointReqDto.builder()
                        .type("REVIEW")
                        .action("MOD")
                        .reviewId("240a0658-dc5f-4878-9381-ebb7b2667772")
                        .userId(bonusUserId)
                        .placeId("2e4baf1c-5acb-4efb-a1af-eddada31b00f")
                        .build();
        reqBody = objectMapper.writeValueAsString(bonusModRequestDto);

        // when & then
        this.mockMvc.perform(post("/event")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        // given
        List<UserPointResDto.PointDetail> modCheckPointDetails = new ArrayList<>();
        modCheckPointDetails.add(
                UserPointResDto.PointDetail.builder()
                        .reviewId("240a0658-dc5f-4878-9381-ebb7b2667772")
                        .point(Point.ImageDelete.getScore())
                        .reason(Point.ImageDelete.getReason())
                        .build()
        );
        modCheckPointDetails.add(
                UserPointResDto.PointDetail.builder()
                        .reviewId("240a0658-dc5f-4878-9381-ebb7b2667772")
                        .point(Point.ContentDelete.getScore())
                        .reason(Point.ContentDelete.getReason())
                        .build()
        );
        modCheckPointDetails.addAll(bonusCheckPointDetails);
        UserPointResDto bonusModCheckResDto =
                UserPointResDto.builder()
                        .totalPoint(1)
                        .pointDetails(modCheckPointDetails)
                        .build();

        // when & then
        this.mockMvc.perform(get("/event/point/{userId}", bonusUserId))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Result successResult = objectMapper.readValue(result.getResponse().getContentAsString(), Result.class);
                    String successResultToStr = objectMapper.writeValueAsString(successResult.getData());
                    UserPointResDto pointResDto = objectMapper.readValue(successResultToStr, UserPointResDto.class);
                    assertThat(pointResDto.getTotalPoint()).usingRecursiveComparison().isEqualTo(bonusModCheckResDto.getTotalPoint());
                    assertThat(pointResDto.getPointDetails()).usingRecursiveComparison().ignoringFields("createdAt").isEqualTo(bonusModCheckResDto.getPointDetails());
                })
                .andDo(print());

        // 3-2. ?????? ???,????????? ?????? ???????????? ???,????????? ?????? Test
        // given
        PointReqDto bonusModRequestDto2 =
                PointReqDto.builder()
                        .type("REVIEW")
                        .action("MOD")
                        .content("????????????!")
                        .attachedPhotoIds(new String[]{"e4d1a64e-a531-46de-88d0-ff0ed70c0bb8", "afb0cef2-851d-4a50-bb07-9cc15cbdc332"})
                        .reviewId("240a0658-dc5f-4878-9381-ebb7b2667772")
                        .userId(bonusUserId)
                        .placeId("2e4baf1c-5acb-4efb-a1af-eddada31b00f")
                        .build();
        reqBody = objectMapper.writeValueAsString(bonusModRequestDto2);

        // when & then
        this.mockMvc.perform(post("/event")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        // given
        List<UserPointResDto.PointDetail> modCheckPointDetails2 = new ArrayList<>();
        modCheckPointDetails2.add(
                UserPointResDto.PointDetail.builder()
                        .reviewId("240a0658-dc5f-4878-9381-ebb7b2667772")
                        .point(Point.ImageAdd.getScore())
                        .reason(Point.ImageAdd.getReason())
                        .build()
        );
        modCheckPointDetails2.add(
                UserPointResDto.PointDetail.builder()
                        .reviewId("240a0658-dc5f-4878-9381-ebb7b2667772")
                        .point(Point.ContentAdd.getScore())
                        .reason(Point.ContentAdd.getReason())
                        .build()
        );
        modCheckPointDetails2.addAll(modCheckPointDetails);
        UserPointResDto bonusModCheckResDto2 =
                UserPointResDto.builder()
                        .totalPoint(3)
                        .pointDetails(modCheckPointDetails2)
                        .build();

        // when & then
        this.mockMvc.perform(get("/event/point/{userId}", bonusUserId))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Result successResult = objectMapper.readValue(result.getResponse().getContentAsString(), Result.class);
                    String successResultToStr = objectMapper.writeValueAsString(successResult.getData());
                    UserPointResDto pointResDto = objectMapper.readValue(successResultToStr, UserPointResDto.class);
                    assertThat(pointResDto.getTotalPoint()).usingRecursiveComparison().isEqualTo(bonusModCheckResDto2.getTotalPoint());
                    assertThat(pointResDto.getPointDetails()).usingRecursiveComparison().ignoringFields("createdAt").isEqualTo(bonusModCheckResDto2.getPointDetails());
                })
                .andDo(print());

        // 4. ?????? ?????? Test
        // 4-1. ??? ?????? ????????? ?????? ?????? ?????? test
        // given
        PointReqDto deleteRequestDto =
                PointReqDto.builder()
                        .type("REVIEW")
                        .action("DELETE")
                        .reviewId("240a0658-dc5f-4878-9381-ebb7b2667772")
                        .userId(bonusUserId)
                        .placeId("2e4baf1c-5acb-4efb-a1af-eddada31b00f")
                        .build();
        reqBody = objectMapper.writeValueAsString(deleteRequestDto);
        // when & then
        this.mockMvc.perform(post("/event")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        // given
        List<UserPointResDto.PointDetail> deleteCheckPointDetails = new ArrayList<>();
        deleteCheckPointDetails.add(
                UserPointResDto.PointDetail.builder()
                        .reviewId("240a0658-dc5f-4878-9381-ebb7b2667772")
                        .point(Point.BonusDelete.getScore())
                        .reason(Point.BonusDelete.getReason())
                        .build()
        );
        deleteCheckPointDetails.add(
                UserPointResDto.PointDetail.builder()
                        .reviewId("240a0658-dc5f-4878-9381-ebb7b2667772")
                        .point(Point.ImageDelete.getScore())
                        .reason(Point.ImageDelete.getReason())
                        .build()
        );
        deleteCheckPointDetails.add(
                UserPointResDto.PointDetail.builder()
                        .reviewId("240a0658-dc5f-4878-9381-ebb7b2667772")
                        .point(Point.ContentDelete.getScore())
                        .reason(Point.ContentDelete.getReason())
                        .build()
        );
        deleteCheckPointDetails.addAll(modCheckPointDetails2);
        UserPointResDto deleteCheckResDto =
                UserPointResDto.builder()
                        .totalPoint(0)
                        .pointDetails(deleteCheckPointDetails)
                        .build();
        // when & then
        this.mockMvc.perform(get("/event/point/{userId}", bonusUserId))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Result successResult = objectMapper.readValue(result.getResponse().getContentAsString(), Result.class);
                    String successResultToStr = objectMapper.writeValueAsString(successResult.getData());
                    UserPointResDto pointResDto = objectMapper.readValue(successResultToStr, UserPointResDto.class);
                    assertThat(pointResDto.getTotalPoint()).usingRecursiveComparison().isEqualTo(deleteCheckResDto.getTotalPoint());
                    assertThat(pointResDto.getPointDetails()).usingRecursiveComparison().ignoringFields("createdAt").isEqualTo(deleteCheckResDto.getPointDetails());
                })
                .andDo(print());

        // 4-2. ??? ?????? ????????? ?????? ?????? ??? ?????? ????????? ?????? ???????????? Bonus ????????? ?????? ??????
        // given
        String afterDeleteFirstsPlaceUserId = UUID.randomUUID().toString();
        String afterDeleteFirstsPlaceReviewId= UUID.randomUUID().toString();
        PointReqDto afterDeleteFirstsPlaceDto =
                PointReqDto.builder()
                        .type("REVIEW")
                        .action("ADD")
                        .reviewId(afterDeleteFirstsPlaceReviewId)
                        .content("??? ?????? ????????? ?????? ?????? ??? ?????? ????????? ?????? ???????????? Bonus ????????? ?????? ")
                        .attachedPhotoIds(new String[]{"e4d1a64e-a531-46de-88d0-ff0ed70c0bb8", "afb0cef2-851d-4a50-bb07-9cc15cbdc332"})
                        .userId(afterDeleteFirstsPlaceUserId)
                        .placeId("2e4baf1c-5acb-4efb-a1af-eddada31b00f")
                        .build();
        reqBody = objectMapper.writeValueAsString(afterDeleteFirstsPlaceDto);
        // when & then
        this.mockMvc.perform(post("/event")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        // given
        List<UserPointResDto.PointDetail> afterDeleteFirstsPlaceCheckPointDetails = new ArrayList<>();
        afterDeleteFirstsPlaceCheckPointDetails.add(
                UserPointResDto.PointDetail.builder()
                        .reviewId(afterDeleteFirstsPlaceReviewId)
                        .point(Point.BonusAdd.getScore())
                        .reason(Point.BonusAdd.getReason())
                        .build()
        );
        afterDeleteFirstsPlaceCheckPointDetails.add(
                UserPointResDto.PointDetail.builder()
                        .reviewId(afterDeleteFirstsPlaceReviewId)
                        .point(Point.ImageAdd.getScore())
                        .reason(Point.ImageAdd.getReason())
                        .build()
        );
        afterDeleteFirstsPlaceCheckPointDetails.add(
                UserPointResDto.PointDetail.builder()
                        .reviewId(afterDeleteFirstsPlaceReviewId)
                        .point(Point.ContentAdd.getScore())
                        .reason(Point.ContentAdd.getReason())
                        .build()
        );

        UserPointResDto afterDeleteFirstsPlaceCheckResDto =
                UserPointResDto.builder()
                        .totalPoint(3)
                        .pointDetails(afterDeleteFirstsPlaceCheckPointDetails)
                        .build();

        // when & then
        this.mockMvc.perform(get("/event/point/{userId}", afterDeleteFirstsPlaceUserId))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Result successResult = objectMapper.readValue(result.getResponse().getContentAsString(), Result.class);
                    String successResultToStr = objectMapper.writeValueAsString(successResult.getData());
                    UserPointResDto pointResDto = objectMapper.readValue(successResultToStr, UserPointResDto.class);
                    assertThat(pointResDto.getTotalPoint()).usingRecursiveComparison().isEqualTo(afterDeleteFirstsPlaceCheckResDto.getTotalPoint());
                    assertThat(pointResDto.getPointDetails()).usingRecursiveComparison().ignoringFields("createdAt").isEqualTo(afterDeleteFirstsPlaceCheckResDto.getPointDetails());
                });
    }

}
