package com.example.mileage.event.repository_test;

import com.example.mileage.event.entity.User;
import com.example.mileage.event.repository.UserRepository;
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
@DisplayName("UserRepository 테스트")
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("save() 테스트")
    void saveTest() {
        // given
        User saveUser =
                User.builder()
                        .userId(UUID.fromString("3ede0ef2-92b7-4817-a5f3-0c575361f745"))
                        .build();
        // when
        User testUser = userRepository.save(saveUser);
        // then
        assert testUser.getUserId().equals(UUID.fromString("3ede0ef2-92b7-4817-a5f3-0c575361f745")) : "save() 실패!";
        assert testUser.getCreatedAt() == null : "save() flush 됨!";

        // when
        userRepository.flush();
        // then
        assert testUser.getCreatedAt().getClass().getSimpleName().equals("Timestamp") : "createdAt 컬럼 default value 실패!";
    }

    @Test
    @DisplayName("find() 테스트")
    void findTest() {
        // given
        UUID findUuid = UUID.fromString("3ede0ef2-92b7-4817-a5f3-0c575361f745");
        // when
        Optional<User> beforeSaveUser = userRepository.findById(findUuid);
        // then
        assert beforeSaveUser.isEmpty() : "없는 데이터 find() 됐음!";

        // given
        User saveUser =
                User.builder()
                        .userId(findUuid)
                        .build();
        // when
        User testUser = userRepository.save(saveUser);
        Optional<User> afterSaveUser = userRepository.findById(testUser.getUserId());
        // then
        assert afterSaveUser.isPresent() : "find() 실패했음";
        assert afterSaveUser.get().getUserId().equals(UUID.fromString("3ede0ef2-92b7-4817-a5f3-0c575361f745")) : "잘못된 데이터 find()";
    }
}
