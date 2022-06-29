package com.example.mileage.event.repository_test;

import com.example.mileage.common.Point;
import com.example.mileage.event.entity.PointHistory;
import com.example.mileage.event.entity.User;
import com.example.mileage.event.repository.PointHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@DisplayName("PointHistoryRepository 테스트")
public class PointHistoryRepositoryTest {
    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @BeforeEach
    @DisplayName("PointHistory data setUp")
    void setPointHistoryRepository() {
        PointHistory pointHistory =
                PointHistory.builder()
                        .reviewId(UUID.fromString("240a0658-dc5f-4878-9381-ebb7b2667772"))
                        .userId(UUID.fromString("3ede0ef2-92b7-4817-a5f3-0c575361f745"))
                        .historyId(UUID.randomUUID())
                        .point(Point.ContentAdd.getScore())
                        .reason(Point.ContentAdd.getReason())
                        .tag('C')
                        .build();
        pointHistoryRepository.saveAndFlush(pointHistory);
    }

    @Test
    @DisplayName("FindByReviewIdAndTag() Test")
    void testFindByReviewIdAndTag() {
        // given
        UUID reviewId = UUID.fromString("240a0658-dc5f-4878-9381-ebb7b2667772");
        char tag = 'C';

        // when
        List<PointHistory> pointHistories = pointHistoryRepository.findAllByReviewIdAndTag(reviewId, tag);

        // then
        assert pointHistories.size() == 1 : "findAllByReviewIdAndTag() 잘못됨!";
        assert pointHistories.get(0).getReason().equals(Point.ContentAdd.getReason()) : "findAllByReviewIdAndTag() 잘못 조회됨!";
    }
}
