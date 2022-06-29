package com.example.mileage.event.service_test;

import com.example.mileage.common.Point;
import com.example.mileage.event.dto.PointReqDto;
import com.example.mileage.event.dto.UserPointResDto;
import com.example.mileage.event.entity.*;
import com.example.mileage.event.repository.*;
import com.example.mileage.event.service.EventService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@DisplayName("EventService 테스트")
public class EventServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PlaceRepository placeRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private PointHistoryRepository pointHistoryRepository;
    @Mock
    private PhotoRepository photoRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    @DisplayName("addEvent() CustomExceptionTest")
    void addEventTest() throws Exception {
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
        User fakeUser =
                User.builder()
                        .userId(UUID.fromString("3ede0ef2-92b7-4817-a5f3-0c575361f745"))
                        .build();
        given(userRepository.findById(UUID.fromString("3ede0ef2-92b7-4817-a5f3-0c575361f745")))
                .willReturn(Optional.ofNullable(fakeUser));

        Place fakePlace =
                Place.builder()
                        .placeId(UUID.fromString("2e4baf1c-5acb-4efb-a1af-eddada31b00f"))
                        .build();
        given(placeRepository.findById(UUID.fromString("2e4baf1c-5acb-4efb-a1af-eddada31b00f")))
                .willReturn(Optional.ofNullable(fakePlace));

        List<Review> fakeList = new LinkedList<>();
        fakeList.add(new Review());
        given(reviewRepository.findAllByPlace(fakePlace))
                .willReturn(fakeList);

        // when
        eventService.accumulatePoint(pointReqDto);

        // then
        verify(reviewRepository, times(1)).findById(UUID.fromString("240a0658-dc5f-4878-9381-ebb7b2667772"));
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(pointHistoryRepository, times(3)).save(any(PointHistory.class));
        verify(photoRepository, times(2)).save(any(Photo.class));
    }

    @Test
    @DisplayName("addEvent() CustomExceptionTest")
    void addEventCustomExceptionTest() {
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

        given(reviewRepository.findById(UUID.fromString("240a0658-dc5f-4878-9381-ebb7b2667772")))
                .willReturn(Optional.of(new Review()));

        // when & then
        assertThrows(NoClassDefFoundError.class, () -> {
            eventService.accumulatePoint(pointReqDto);
        });

        // given
        User fakeUser =
                User.builder()
                        .userId(UUID.fromString("3ede0ef2-92b7-4817-a5f3-0c575361f745"))
                        .build();
        given(userRepository.findById(UUID.fromString("3ede0ef2-92b7-4817-a5f3-0c575361f745")))
                .willReturn(Optional.ofNullable(fakeUser));

        Place fakePlace =
                Place.builder()
                        .placeId(UUID.fromString("2e4baf1c-5acb-4efb-a1af-eddada31b00f"))
                        .build();
        given(placeRepository.findById(UUID.fromString("2e4baf1c-5acb-4efb-a1af-eddada31b00f")))
                .willReturn(Optional.ofNullable(fakePlace));

        given(reviewRepository.findByUserAndPlace(fakeUser, fakePlace))
                .willReturn(Optional.of(new Review()));

        PointReqDto testDto2 =
                PointReqDto.builder()
                        .type("REVIEW")
                        .action("ADD")
                        .reviewId("240a0658-dc5f-4878-9381-ebb7b2667770")
                        .content("좋아요!")
                        .attachedPhotoIds(new String[]{"e4d1a64e-a531-46de-88d0-ff0ed70c0bb8", "afb0cef2-851d-4a50-bb07-9cc15cbdc332"})
                        .userId("3ede0ef2-92b7-4817-a5f3-0c575361f745")
                        .placeId("2e4baf1c-5acb-4efb-a1af-eddada31b00f")
                        .build();
        // when & then
        assertThrows(NoClassDefFoundError.class, () -> {
            eventService.accumulatePoint(testDto2);
        });
    }

    @Test
    @DisplayName("modEventTest()")
    void modEventTest() throws Exception {
        // given
        PointReqDto pointReqDto =
                PointReqDto.builder()
                        .type("REVIEW")
                        .action("MOD")
                        .reviewId("240a0658-dc5f-4878-9381-ebb7b2667772")
                        .content("좋아요!")
                        .attachedPhotoIds(new String[]{"e4d1a64e-a531-46de-88d0-ff0ed70c0bb8", "afb0cef2-851d-4a50-bb07-9cc15cbdc332"})
                        .userId("3ede0ef2-92b7-4817-a5f3-0c575361f745")
                        .placeId("2e4baf1c-5acb-4efb-a1af-eddada31b00f")
                        .build();

        User fakeUser =
                User.builder()
                        .userId(UUID.fromString("3ede0ef2-92b7-4817-a5f3-0c575361f745"))
                        .build();
        given(userRepository.findById(UUID.fromString("3ede0ef2-92b7-4817-a5f3-0c575361f745")))
                .willReturn(Optional.ofNullable(fakeUser));

        Place fakePlace =
                Place.builder()
                        .placeId(UUID.fromString("2e4baf1c-5acb-4efb-a1af-eddada31b00f"))
                        .build();
        given(placeRepository.findById(UUID.fromString("2e4baf1c-5acb-4efb-a1af-eddada31b00f")))
                .willReturn(Optional.ofNullable(fakePlace));

        Review fakeReview =
                Review.builder()
                        .reviewId(UUID.fromString("240a0658-dc5f-4878-9381-ebb7b2667772"))
                        .user(fakeUser)
                        .place(fakePlace)
                        .build();
        given(reviewRepository.findById(UUID.fromString("240a0658-dc5f-4878-9381-ebb7b2667772")))
                .willReturn(Optional.ofNullable(fakeReview));

        // when
        eventService.accumulatePoint(pointReqDto);

        // then
        verify(reviewRepository, times(1)).findById(UUID.fromString("240a0658-dc5f-4878-9381-ebb7b2667772"));
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(pointHistoryRepository, times(2)).save(any(PointHistory.class));
        verify(photoRepository, times(2)).save(any(Photo.class));
    }

    @Test
    @DisplayName("modEventTest() CustomExceptionTest")
    void modEventCustomExceptionTest() {
        // given
        PointReqDto pointReqDto =
                PointReqDto.builder()
                        .type("REVIEW")
                        .action("MOD")
                        .reviewId("240a0658-dc5f-4878-9381-ebb7b2667772")
                        .content("좋아요!")
                        .attachedPhotoIds(new String[]{"e4d1a64e-a531-46de-88d0-ff0ed70c0bb8", "afb0cef2-851d-4a50-bb07-9cc15cbdc332"})
                        .userId("3ede0ef2-92b7-4817-a5f3-0c575361f745")
                        .placeId("2e4baf1c-5acb-4efb-a1af-eddada31b00f")
                        .build();

        User fakeUser =
                User.builder()
                        .userId(UUID.randomUUID())
                        .build();
        given(userRepository.findById(UUID.fromString("3ede0ef2-92b7-4817-a5f3-0c575361f745")))
                .willReturn(Optional.ofNullable(fakeUser));

        Place fakePlace =
                Place.builder()
                        .placeId(UUID.fromString("2e4baf1c-5acb-4efb-a1af-eddada31b00f"))
                        .build();
        given(placeRepository.findById(UUID.fromString("2e4baf1c-5acb-4efb-a1af-eddada31b00f")))
                .willReturn(Optional.ofNullable(fakePlace));


        // when & then
        assertThrows(NoClassDefFoundError.class, () -> {
            eventService.accumulatePoint(pointReqDto);
        });

        // given
        User testUser =
                User.builder()
                        .userId(UUID.randomUUID())
                        .build();
        Review fakeReview =
                Review.builder()
                        .reviewId(UUID.fromString("240a0658-dc5f-4878-9381-ebb7b2667772"))
                        .user(testUser)
                        .place(fakePlace)
                        .build();
        given(reviewRepository.findById(UUID.fromString("240a0658-dc5f-4878-9381-ebb7b2667772")))
                .willReturn(Optional.ofNullable(fakeReview));
        pointReqDto.setUser(fakeUser);
        pointReqDto.setPlace(fakePlace);

        // when & then
        assertThrows(NoClassDefFoundError.class, () -> {
            eventService.accumulatePoint(pointReqDto);
        });
    }

    @Test
    @DisplayName("deleteEvent() test")
    void deleteEventTest() throws Exception {
        // given
        PointReqDto pointReqDto =
                PointReqDto.builder()
                        .type("REVIEW")
                        .action("DELETE")
                        .reviewId("240a0658-dc5f-4878-9381-ebb7b2667772")
                        .content("좋아요!")
                        .attachedPhotoIds(new String[]{"e4d1a64e-a531-46de-88d0-ff0ed70c0bb8", "afb0cef2-851d-4a50-bb07-9cc15cbdc332"})
                        .userId("3ede0ef2-92b7-4817-a5f3-0c575361f745")
                        .placeId("2e4baf1c-5acb-4efb-a1af-eddada31b00f")
                        .build();

        User fakeUser =
                User.builder()
                        .userId(UUID.randomUUID())
                        .build();
        given(userRepository.findById(UUID.fromString("3ede0ef2-92b7-4817-a5f3-0c575361f745")))
                .willReturn(Optional.ofNullable(fakeUser));

        Place fakePlace =
                Place.builder()
                        .placeId(UUID.fromString("2e4baf1c-5acb-4efb-a1af-eddada31b00f"))
                        .build();
        given(placeRepository.findById(UUID.fromString("2e4baf1c-5acb-4efb-a1af-eddada31b00f")))
                .willReturn(Optional.ofNullable(fakePlace));

        Review fakeReview =
                Review.builder()
                        .reviewId(UUID.fromString("240a0658-dc5f-4878-9381-ebb7b2667772"))
                        .user(fakeUser)
                        .place(fakePlace)
                        .content("TEST Contenet")
                        .build();
        given(reviewRepository.findById(UUID.fromString("240a0658-dc5f-4878-9381-ebb7b2667772")))
                .willReturn(Optional.ofNullable(fakeReview));

        List<Photo> fakePhotoList = new ArrayList<>();
        fakePhotoList.add(new Photo());
        given(photoRepository.findAllByReview(any(Review.class)))
                .willReturn(fakePhotoList);

        List<PointHistory> fakePointHistories = new ArrayList<>();
        fakePointHistories.add(new PointHistory());
        given(pointHistoryRepository.findAllByReviewIdAndTag(UUID.fromString("240a0658-dc5f-4878-9381-ebb7b2667772"), 'B'))
                .willReturn(fakePointHistories);

        // when
        eventService.accumulatePoint(pointReqDto);

        // then
        verify(reviewRepository, times(1)).findById(UUID.fromString("240a0658-dc5f-4878-9381-ebb7b2667772"));
        verify(photoRepository, times(1)).findAllByReview(fakeReview);
        verify(photoRepository, times(1)).deleteAll(fakePhotoList);
        verify(pointHistoryRepository, times(3)).save(any(PointHistory.class));
        verify(pointHistoryRepository, times(1)).findAllByReviewIdAndTag(UUID.fromString("240a0658-dc5f-4878-9381-ebb7b2667772"), 'B');
        verify(reviewRepository, times(1)).delete(fakeReview);
    }

    @Test
    @DisplayName("deleteEvent() CustomExceptionTest")
    void deleteEventCustomExceptionTest() {
        // given
        PointReqDto pointReqDto =
                PointReqDto.builder()
                        .type("REVIEW")
                        .action("DELETE")
                        .reviewId("240a0658-dc5f-4878-9381-ebb7b2667772")
                        .content("좋아요!")
                        .attachedPhotoIds(new String[]{"e4d1a64e-a531-46de-88d0-ff0ed70c0bb8", "afb0cef2-851d-4a50-bb07-9cc15cbdc332"})
                        .userId("3ede0ef2-92b7-4817-a5f3-0c575361f745")
                        .placeId("2e4baf1c-5acb-4efb-a1af-eddada31b00f")
                        .build();

        User fakeUser =
                User.builder()
                        .userId(UUID.randomUUID())
                        .build();
        given(userRepository.findById(UUID.fromString("3ede0ef2-92b7-4817-a5f3-0c575361f745")))
                .willReturn(Optional.ofNullable(fakeUser));

        Place fakePlace =
                Place.builder()
                        .placeId(UUID.fromString("2e4baf1c-5acb-4efb-a1af-eddada31b00f"))
                        .build();
        given(placeRepository.findById(UUID.fromString("2e4baf1c-5acb-4efb-a1af-eddada31b00f")))
                .willReturn(Optional.ofNullable(fakePlace));

        // when & then
        assertThrows(ExceptionInInitializerError.class, () -> {
            eventService.accumulatePoint(pointReqDto);
        });

        // given
        User testUser =
                User.builder()
                        .userId(UUID.randomUUID())
                        .build();
        Review fakeReview =
                Review.builder()
                        .reviewId(UUID.fromString("240a0658-dc5f-4878-9381-ebb7b2667772"))
                        .user(testUser)
                        .place(fakePlace)
                        .build();
        given(reviewRepository.findById(UUID.fromString("240a0658-dc5f-4878-9381-ebb7b2667772")))
                .willReturn(Optional.ofNullable(fakeReview));
        pointReqDto.setUser(fakeUser);
        pointReqDto.setPlace(fakePlace);

        // when & then
        assertThrows(NoClassDefFoundError.class, () -> {
            eventService.accumulatePoint(pointReqDto);
        });
    }

    @Test
    @DisplayName("getUserPoint() Test")
    void getUserPointTest() {
        // given
        String testUserId = "3ede0ef2-92b7-4817-a5f3-0c575361f745";

        List<PointHistory> fakePointHistories = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            PointHistory history =
                    PointHistory.builder()
                            .reviewId(UUID.randomUUID())
                            .historyId(UUID.randomUUID())
                            .userId(UUID.randomUUID())
                            .tag('C')
                            .point(Point.ContentAdd.getScore())
                            .reason(Point.ContentAdd.getReason())
                            .build();
            fakePointHistories.add(history);
        }
        given(pointHistoryRepository.findAllByUserIdOrderByCreatedAtDesc(UUID.fromString(testUserId)))
                .willReturn(fakePointHistories);

        UserPointResDto.PointDetail testPointDetail =
                UserPointResDto.PointDetail.builder()
                        .reviewId(fakePointHistories.get(0).getReviewId().toString())
                        .reason(Point.ContentAdd.getReason())
                        .point(Point.ContentAdd.getScore())
                        .build();

        // when
        UserPointResDto userPointResDto = eventService.getUserPoint(testUserId);

        // then
        assertEquals(userPointResDto.getTotalPoint(), 3);
        assertEquals(userPointResDto.getPointDetails().size(), 3);
        assertEquals(userPointResDto.getPointDetails().get(0).getReviewId(), testPointDetail.getReviewId());
        assertEquals(userPointResDto.getPointDetails().get(0).getReason(), testPointDetail.getReason());
        assertEquals(userPointResDto.getPointDetails().get(0).getPoint(), testPointDetail.getPoint());

    }



}
