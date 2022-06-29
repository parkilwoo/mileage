//package com.example.mileage.user.service;
//
//import com.example.mileage.common.Utils;
//import com.example.mileage.common.exception.CustomException;
//import com.example.mileage.common.exception.CustomExceptionEnum;
//import com.example.mileage.user.dto.UserJoinDto;
//import com.example.mileage.user.dto.UserRespDto;
//import com.example.mileage.user.entity.User;
//import com.example.mileage.user.repository.UserRepository;
//import org.springframework.context.MessageSource;
//import org.springframework.dao.DuplicateKeyException;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
///**
// * 유저 Service Class
// */
//@Service
//public class UserService {
//    private final UserRepository userRepository;
//    private final MessageSource messageSource = (MessageSource) Utils.getBean("messageSource");
//
//    public UserService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    /**
//     * 유저 회원가입
//     * @param userJoinDto 유저아이디, 유저이름 담은 Dto
//     */
//    public User singUp(UserJoinDto userJoinDto) {
//        User user = User.builder()
//                .userId(userJoinDto.getUserId())
//                .name(userJoinDto.getName())
//                .build();
//        if(userRepository.findByUserId(user.getUserId()).isPresent()) {
//            throw new DuplicateKeyException(account.getUserId() + "는 이미 등록되어 있는 아이디입니다.");
//        }
//        return userRepository.save(user);
//    }
//
//    /**
//     * 유저 정보 가져오기
//     * @param userId 유저 아이디
//     * @return UserRespDto
//     */
//    public UserRespDto getUser(String userId) throws Exception{
//        Optional<User> optionalUser = userRepository.findByUserId(userId);
//        //                new CustomException.Builder()
//        //                        .code(CustomExceptionEnum.VALIDATE_EXCEPTION.getCode())
//        //                        .message(Utils.getMessageSource("exception.msg.not-found-id"))
//        //                        .build()
//        User user = optionalUser.orElseThrow(Exception::new);
//        return UserRespDto.builder()
//                .name(user.getName())
//                .build();
//    }
//}
