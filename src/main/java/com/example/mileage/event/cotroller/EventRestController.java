package com.example.mileage.event.cotroller;

import com.example.mileage.common.Result;
import com.example.mileage.event.dto.PointReqDto;
import com.example.mileage.event.service.EventService;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/event")
public class EventRestController {

    private final EventService eventService;

    public EventRestController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * 포인트 적립 API
     * @param pointReqDto 포인트 적립에 필요한 parameter DTO
     * @return result class
     */
    @PostMapping
    public Result accumulatePoint(@Valid @RequestBody PointReqDto pointReqDto) throws Exception {
        Result result = new Result();
        eventService.accumulatePoint(pointReqDto);
        result.setSuccess();
        return result;
    }

    /**
     * 포인트 조회 API
     * @param userId 조회할 유저 ID
     * @return result Class
     */
    @GetMapping("/point/{userId}")
    public Result getUserPoint(@Length(max = 36, min = 36, message = "userId는 36글자 UUID 형식 입니다.") @PathVariable("userId") String userId) {
        Result result = new Result();
        result.setSuccess(eventService.getUserPoint(userId));
        return result;
    }
}
