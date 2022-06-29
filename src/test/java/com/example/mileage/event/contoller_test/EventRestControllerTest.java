package com.example.mileage.event.contoller_test;

import com.example.mileage.common.Result;
import com.example.mileage.common.exception.CustomExceptionEnum;
import com.example.mileage.event.dto.PointReqDto;
import com.example.mileage.event.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@ExtendWith(SpringExtension.class)
//@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("EventRestController 테스트")
public class EventRestControllerTest {
    @Autowired
    private WebApplicationContext ctx;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print())
                .build();
    }

    @Test
    @DisplayName("accumulatePoint() Test")
    void accumulatePointTest() throws Exception {
        // given
        PointReqDto pointReqDto =
                PointReqDto.builder()
                        .type("REVIEW")
                        .action("ADD")
                        .reviewId("240a0658-dc5f-4878-9381-ebb7b2667772")
                        .content("좋아요!")
                        .attachedPhotoIds(new String[]{"e4d1a64e-a531-46de-88d0-ff0ed70c0bb8", "afb0cef2-851d-4a50-bb07-9cc15cbdc332"})
                        .userId("3ede0ef2-92b7-4817-a5f3-0c575361f745")
                        .placeId("2e4baf1c-5acb-4efb-a1af-eddada31b00f")
                        .build();
        String reqBody = objectMapper.writeValueAsString(pointReqDto);

        // when & then
        this.mockMvc.perform(post("/event")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andDo(print());
    }

    @Test
    @DisplayName("Annotaion Valid Test")
    void testValid() throws Exception {
        // given
        PointReqDto typeEnumTest =
                PointReqDto.builder()
                        .type("TEST")
                        .action("ADD")
                        .reviewId("240a0658-dc5f-4878-9381-ebb7b2667772")
                        .content("좋아요!")
                        .attachedPhotoIds(new String[]{"e4d1a64e-a531-46de-88d0-ff0ed70c0bb8", "afb0cef2-851d-4a50-bb07-9cc15cbdc332"})
                        .userId("3ede0ef2-92b7-4817-a5f3-0c575361f745")
                        .placeId("2e4baf1c-5acb-4efb-a1af-eddada31b00f")
                        .build();
        String reqBody = objectMapper.writeValueAsString(typeEnumTest);

        // when & then
        this.mockMvc.perform(post("/event")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(result -> {
                            Result failResult = objectMapper.readValue(result.getResponse().getContentAsString(), Result.class);
                            assertEquals(failResult.getCode(), CustomExceptionEnum.VALIDATE_EXCEPTION.getCode());
                            assertEquals(failResult.getMsg(), "type값은 REVIEW 값만 가능합니다.");
                        })
                        .andDo(print());

        // given
        PointReqDto actionEnumTest =
                PointReqDto.builder()
                        .type("REVIEW")
                        .action("TEST")
                        .reviewId("240a0658-dc5f-4878-9381-ebb7b2667772")
                        .content("좋아요!")
                        .attachedPhotoIds(new String[]{"e4d1a64e-a531-46de-88d0-ff0ed70c0bb8", "afb0cef2-851d-4a50-bb07-9cc15cbdc332"})
                        .userId("3ede0ef2-92b7-4817-a5f3-0c575361f745")
                        .placeId("2e4baf1c-5acb-4efb-a1af-eddada31b00f")
                        .build();
        reqBody = objectMapper.writeValueAsString(actionEnumTest);
        // when & then
        this.mockMvc.perform(post("/event")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Result failResult = objectMapper.readValue(result.getResponse().getContentAsString(), Result.class);
                    assertEquals(failResult.getCode(), CustomExceptionEnum.VALIDATE_EXCEPTION.getCode());
                    assertEquals(failResult.getMsg(), "action 값은 ADD, MOD, DELETE 값만 가능합니다.");
                })
                .andDo(print());

        // given
        PointReqDto reviewEnumTest =
                PointReqDto.builder()
                        .type("REVIEW")
                        .action("ADD")
                        .reviewId("TEST")
                        .content("좋아요!")
                        .attachedPhotoIds(new String[]{"e4d1a64e-a531-46de-88d0-ff0ed70c0bb8", "afb0cef2-851d-4a50-bb07-9cc15cbdc332"})
                        .userId("3ede0ef2-92b7-4817-a5f3-0c575361f745")
                        .placeId("2e4baf1c-5acb-4efb-a1af-eddada31b00f")
                        .build();
        reqBody = objectMapper.writeValueAsString(reviewEnumTest);
        // when & then
        this.mockMvc.perform(post("/event")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Result failResult = objectMapper.readValue(result.getResponse().getContentAsString(), Result.class);
                    assertEquals(failResult.getCode(), CustomExceptionEnum.VALIDATE_EXCEPTION.getCode());
                    assertEquals(failResult.getMsg(), "reviewId 값은 36자의 UUID형식 값만 가능합니다.");
                })
                .andDo(print());

        // given
        PointReqDto userEnumTest =
                PointReqDto.builder()
                        .type("REVIEW")
                        .action("ADD")
                        .reviewId("240a0658-dc5f-4878-9381-ebb7b2667772")
                        .content("좋아요!")
                        .attachedPhotoIds(new String[]{"e4d1a64e-a531-46de-88d0-ff0ed70c0bb8", "afb0cef2-851d-4a50-bb07-9cc15cbdc332"})
                        .userId("TEST")
                        .placeId("2e4baf1c-5acb-4efb-a1af-eddada31b00f")
                        .build();
        reqBody = objectMapper.writeValueAsString(userEnumTest);
        // when & then
        this.mockMvc.perform(post("/event")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Result failResult = objectMapper.readValue(result.getResponse().getContentAsString(), Result.class);
                    assertEquals(failResult.getCode(), CustomExceptionEnum.VALIDATE_EXCEPTION.getCode());
                    assertEquals(failResult.getMsg(), "userId 값은 36자의 UUID형식 값만 가능합니다.");
                })
                .andDo(print());

        // given
        PointReqDto placeEnumTest =
                PointReqDto.builder()
                        .type("REVIEW")
                        .action("ADD")
                        .reviewId("240a0658-dc5f-4878-9381-ebb7b2667772")
                        .content("좋아요!")
                        .attachedPhotoIds(new String[]{"e4d1a64e-a531-46de-88d0-ff0ed70c0bb8", "afb0cef2-851d-4a50-bb07-9cc15cbdc332"})
                        .userId("3ede0ef2-92b7-4817-a5f3-0c575361f745")
                        .placeId("Test")
                        .build();
        reqBody = objectMapper.writeValueAsString(placeEnumTest);
        // when & then
        this.mockMvc.perform(post("/event")
                        .content(reqBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Result failResult = objectMapper.readValue(result.getResponse().getContentAsString(), Result.class);
                    assertEquals(failResult.getCode(), CustomExceptionEnum.VALIDATE_EXCEPTION.getCode());
                    assertEquals(failResult.getMsg(), "placeId 값은 36자의 UUID형식 값만 가능합니다.");
                })
                .andDo(print());

    }
}
