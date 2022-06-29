package com.example.mileage.event.repository_test;

import com.example.mileage.event.entity.Place;
import com.example.mileage.event.entity.Review;
import com.example.mileage.event.entity.User;
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

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@DisplayName("ReviewRepository 테스트")
public class ReviewRepositoryTest {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlaceRepository placeRepository;
    private User testUser;      //  review 등록할 때 쓸 testUser;
    private Place testPlace;    //  review 등록할 때 쓸 testPlace;

    @BeforeEach
    @DisplayName("유저 Data Insert")
    void userAndPlaceSetUp() {
        User saveUser =
                User.builder()
                        .userId(UUID.fromString("3ede0ef2-92b7-4817-a5f3-0c575361f745"))
                        .build();
        testUser = userRepository.save(saveUser);

        Place savePlace =
                Place.builder()
                        .placeId(UUID.fromString("2e4baf1c-5acb-4efb-a1af-eddada31b00f"))
                        .build();
        testPlace = placeRepository.save(savePlace);
    }

    @Test
    @DisplayName("save() 테스트")
    void saveTest() {
        // given
        Review saveReview =
                Review.builder()
                        .reviewId(UUID.fromString("3ede0ef2-92b7-4817-a5f3-0c575361f745"))
                        .user(testUser)
                        .place(testPlace)
                        .content("Test Content")
                        .build();
        // when
        Review testReview = reviewRepository.save(saveReview);
        // then
        assert testReview.getReviewId().equals(UUID.fromString("3ede0ef2-92b7-4817-a5f3-0c575361f745")) : "save() 실패!";
        assert testReview.getCreatedAt() == null : "save() flush 됐음!";
        
        // when
        reviewRepository.flush();
        // then
        assert testReview.getCreatedAt().getClass().getSimpleName().equals("Timestamp") : "createdAt 컬럼 default value 실패!";
    }

    @Test
    @DisplayName("find() 테스트")
    void findTest() {
        // given
        UUID findUuid = UUID.fromString("3ede0ef2-92b7-4817-a5f3-0c575361f745");
        // when
        Optional<Review> beforeSaveReview = reviewRepository.findById(findUuid);
        // then
        assert beforeSaveReview.isEmpty() : "없는 데이터 find() 됐음!";

        // given
        Review saveReview =
                Review.builder()
                        .reviewId(findUuid)
                        .user(testUser)
                        .place(testPlace)
                        .content("Test Content")
                        .build();
        // when
        Review testReview = reviewRepository.save(saveReview);
        Optional<Review> afterSaveReview = reviewRepository.findById(testReview.getReviewId());
        // then
        assert afterSaveReview.isPresent() : "find() 실패했음";
        assert afterSaveReview.get().getReviewId().equals(UUID.fromString("3ede0ef2-92b7-4817-a5f3-0c575361f745")) : "잘못된 데이터 find()";
    }

    @Test
    @DisplayName("findAllByPlace() 테스트")
    void findAllByPlace() {
        // given
        User randomUser1 =
                User.builder()
                        .userId(UUID.randomUUID())
                        .build();
        User randomUser2 =
                User.builder()
                        .userId(UUID.randomUUID())
                        .build();
        randomUser1 = userRepository.save(randomUser1);
        randomUser2 = userRepository.save(randomUser2);
        userRepository.flush();

        List<User> testUserList = new LinkedList<>();
        testUserList.add(testUser);
        testUserList.add(randomUser1);
        testUserList.add(randomUser2);

        List<Review> reviewList = new LinkedList<>();
        testUserList.forEach(user -> {
            Review saveReview =
                    Review.builder()
                            .reviewId(UUID.randomUUID())
                            .user(user)
                            .place(testPlace)
                            .content("Test" + user.getUserId())
                            .build();
            reviewList.add(saveReview);
        });
        //  when
        reviewRepository.saveAllAndFlush(reviewList);
        List<Review> findByAllPlaceList = reviewRepository.findAllByPlace(testPlace);
        // then
        assert findByAllPlaceList.size() == 3 : "리스트 사이즈 잘못됨!";
        assert findByAllPlaceList.stream().filter(review -> review.getPlace().equals(testPlace)).count() == 3 : "리스트 사이즈 잘못됨!";
    }

    @Test
    @DisplayName("update() 테스트")
    void updateTest() {
        // given
        Review saveReview =
                Review.builder()
                        .reviewId(UUID.fromString("240a0658-dc5f-4878-9381-ebb7b2667772"))
                        .user(testUser)
                        .place(testPlace)
                        .content("Test Review")
                        .build();
        reviewRepository.save(saveReview);

        Review updateReview =
                Review.builder()
                        .reviewId(saveReview.getReviewId())
                        .user(saveReview.getUser())
                        .place(saveReview.getPlace())
                        .updateAt(new Timestamp(System.currentTimeMillis()))
                        .build();
        // when
        Review resultReview = reviewRepository.save(updateReview);
        reviewRepository.flush();
        // then
        assert resultReview.getContent() == null : "업데이트 잘못됨!";
        assert resultReview.getCreatedAt() != null : "createAt 시간 바뀜";
    }

    @Test
    @DisplayName("findByUserAndPlace() 테스트")
    void testFindByUserAndPlace() {
        // given
        Review saveReview =
                Review.builder()
                        .reviewId(UUID.fromString("3ede0ef2-92b7-4817-a5f3-0c575361f745"))
                        .user(testUser)
                        .place(testPlace)
                        .content("Test Content")
                        .build();
        // when
        Review testReview = reviewRepository.save(saveReview);
        Optional<Review> optionalReview = reviewRepository.findByUserAndPlace(testUser, testPlace);
        // then
        assert testReview.equals(optionalReview.get()) : "잘못됨!";
    }
}
