package com.example.mileage.event.repository_test;

import com.example.mileage.event.entity.Place;
import com.example.mileage.event.repository.PlaceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@DisplayName("PlaceRepository 테스트")
public class PlaceRepositoryTest {
    @Autowired
    private PlaceRepository placeRepository;

    @Test
    @DisplayName("save() 테스트")
    void saveTest() {
        // given
        Place savePlace =
                Place.builder()
                        .placeId(UUID.fromString("2e4baf1c-5acb-4efb-a1af-eddada31b00f"))
                        .build();
        // when
        Place testPlace = placeRepository.save(savePlace);
        // then
        assert testPlace.getPlaceId().equals(UUID.fromString("2e4baf1c-5acb-4efb-a1af-eddada31b00f")) : "save() 실패!";
        assert testPlace.getCreatedAt() == null : "save() flush 됐음!";

        // when
        placeRepository.flush();
        // then
        assert testPlace.getCreatedAt().getClass().getSimpleName().equals("Timestamp") : "createdAt 컬럼 default value 실패!";
    }

    @Test
    @DisplayName("find() 테스트")
    void findTest() {
        // given
        UUID findUuid = UUID.fromString("2e4baf1c-5acb-4efb-a1af-eddada31b00f");
        // when
        Optional<Place> beforeSaveUser = placeRepository.findById(findUuid);
        // then
        assert beforeSaveUser.isEmpty() : "없는 데이터 find() 됐음!";

        // given
        Place savePlace =
                Place.builder()
                        .placeId(findUuid)
                        .build();
        // when
        Place testPlace = placeRepository.save(savePlace);
        Optional<Place> afterSaveUser = placeRepository.findById(testPlace.getPlaceId());
        // then
        assert afterSaveUser.isPresent() : "find() 실패했음";
        assert afterSaveUser.get().getPlaceId().equals(UUID.fromString("2e4baf1c-5acb-4efb-a1af-eddada31b00f")) : "잘못된 데이터 find()";
    }

}
