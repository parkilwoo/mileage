package com.example.mileage.event.repository_test;

import com.example.mileage.event.entity.Photo;
import com.example.mileage.event.entity.Place;
import com.example.mileage.event.entity.Review;
import com.example.mileage.event.entity.User;
import com.example.mileage.event.repository.PhotoRepository;
import com.example.mileage.event.repository.PlaceRepository;
import com.example.mileage.event.repository.ReviewRepository;
import com.example.mileage.event.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@DisplayName("PhotoRepository 테스트")
public class PhotoRepositoryTest {
    @Autowired
    private PhotoRepository photoRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlaceRepository placeRepository;
    private Review testReview;

    @BeforeEach
    @DisplayName("Review 셋업")
    void setUpReview() {
        User saveUser =
                User.builder()
                        .userId(UUID.fromString("3ede0ef2-92b7-4817-a5f3-0c575361f745"))
                        .build();
        User testUser = userRepository.save(saveUser);

        Place savePlace =
                Place.builder()
                        .placeId(UUID.fromString("2e4baf1c-5acb-4efb-a1af-eddada31b00f"))
                        .build();
        Place testPlace = placeRepository.save(savePlace);

        Review saveReview =
                Review.builder()
                        .reviewId(UUID.fromString("240a0658-dc5f-4878-9381-ebb7b2667772"))
                        .user(testUser)
                        .place(testPlace)
                        .content("Test")
                        .build();
        testReview = reviewRepository.save(saveReview);
    }

    @Test
    @DisplayName("save() 테스트")
    void saveTest() {
        // given
        Photo savePhoto =
                Photo.builder()
                        .photoId(UUID.fromString("e4d1a64e-a531-46de-88d0-ff0ed70c0bb8"))
                        .review(testReview)
                        .build();
        // when
        Photo testPhoto = photoRepository.save(savePhoto);
        // then
        assert testPhoto.getPhotoId().equals(UUID.fromString("e4d1a64e-a531-46de-88d0-ff0ed70c0bb8")) : "save() 실패!";
        assert testPhoto.getCreatedAt() == null : "save() flush 됨!";

        // when
        photoRepository.flush();
        // then
        assert testPhoto.getCreatedAt().getClass().getSimpleName().equals("Timestamp") : "createdAt 컬럼 default value 실패!";
    }

    @Test
    @DisplayName("find() 테스트")
    void findTest() {
        // given
        UUID findUuid = UUID.fromString("e4d1a64e-a531-46de-88d0-ff0ed70c0bb8");
        // when
        Optional<Photo> beforeSaveUser = photoRepository.findById(findUuid);
        // then
        assert beforeSaveUser.isEmpty() : "없는 데이터 find() 됐음!";

        // given
        Photo savePhoto =
                Photo.builder()
                        .photoId(findUuid)
                        .review(testReview)
                        .build();
        // when
        Photo testPhoto = photoRepository.save(savePhoto);
        Optional<Photo> afterSaveUser = photoRepository.findById(testPhoto.getPhotoId());
        // then
        assert afterSaveUser.isPresent() : "find() 실패했음";
        assert afterSaveUser.get().getPhotoId().equals(UUID.fromString("e4d1a64e-a531-46de-88d0-ff0ed70c0bb8")) : "잘못된 데이터 find()";
    }

    @Test
    @DisplayName("findAllByReviewId() 테스트")
    void findAllByReviewId() {
        // given
        Photo savePhoto1 =
                Photo.builder()
                        .review(testReview)
                        .photoId(UUID.fromString("e4d1a64e-a531-46de-88d0-ff0ed70c0bb8"))
                        .build();
        Photo savePhoto2 =
                Photo.builder()
                        .review(testReview)
                        .photoId(UUID.fromString("afb0cef2-851d-4a50-bb07-9cc15cbdc332"))
                        .build();
        List<Photo> savePhotoList = new LinkedList<>();
        savePhotoList.add(savePhoto1);
        savePhotoList.add(savePhoto2);
        photoRepository.saveAllAndFlush(savePhotoList);

        // when
        List<Photo> findAllByReviewIdList = photoRepository.findAllByReview(testReview);

        // then
        assert findAllByReviewIdList.size() == 2 : "findAllByReviewId() 실패";
    }
}
