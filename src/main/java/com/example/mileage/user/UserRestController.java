//package com.example.mileage.user;
//
//import com.example.mileage.common.Result;
//import com.example.mileage.user.dto.UserJoinDto;
//import com.example.mileage.user.service.UserService;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * 유저 관련 RestController
// */
//@RestController
//@RequestMapping("/user")
//public class UserRestController {
//    private final UserService userService;
//
//    public UserRestController(UserService userService) {
//        this.userService = userService;
//    }
//
//    /**
//     * 유저 회원가입
//     * @param userJoinDto 유저 회원가입 정보 Dto
//     * @return
//     */
//    @PostMapping
//    public Result signUp(@RequestBody UserJoinDto userJoinDto) {
//        Result result = new Result();
//        userService.singUp(userJoinDto);
//        result.setSuccess();
//        return result;
//    }
//}
