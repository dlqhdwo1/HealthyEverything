package com.example.healthyeverythingapi.member.controller;

import com.example.healthyeverythingapi.exception.InvalidCredentialsException;
import com.example.healthyeverythingapi.member.dto.*;
import com.example.healthyeverythingapi.exception.DuplicateEmailException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.CREATED)
    public JoinResponse signup(@Valid @RequestBody JoinRequest request) {

        if ("dlwjdrb9412".equals(request.getUserid())) {
            throw new DuplicateEmailException();
        }

        var tokens = new JoinResponse.Tokens(
                "dummy-access-token",
                "dummy-refresh-token",
                3600,
                2592000
        );

        return JoinResponse.success(request.getUserid(), request.getPhonenumber(), tokens);
    }

    @GetMapping("/mypage")
    public MyPageResponse mypageinfo(@RequestHeader(value = "Authorization", required = false) String authorization){

        if (authorization == null || authorization.isBlank()) {
            throw new InvalidCredentialsException("인증이 필요합니다.");
        }
        return new MyPageResponse(
                "이정구",
                "test@test.com",
                "01012345678"
        );
    }

    @GetMapping("/mypage/review")
    public MyPageReviewResponse mypagereviewinfo(@RequestHeader(value = "Authorization", required = false) String authorization){

        if (authorization == null || authorization.isBlank()) {
            throw new InvalidCredentialsException("인증이 필요합니다.");
        }
        return new MyPageReviewResponse(
                List.of(
                        new MyPageReviewResponse.ReviewItem("정말 친절하고 잘 가르쳐주세요!")
                )
        );
    }

    @GetMapping("/mypage/reviews")
    public MyPageDetailReViewResponse mypagereviewdetailinfo(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                             @RequestParam String reviewid)
    {
        if (authorization == null || authorization.isBlank()) {
            throw new InvalidCredentialsException("인증이 필요합니다.");
        }

        return new MyPageDetailReViewResponse(
                "아주 친절하고 잘 가르쳐 주십니다!",
                "작년 7월부터 김땡떙 트레이너 선생님이랑 같이 운동하게 되었는데 정말 친절하고 상세하게 가르쳐 주십니다."
        );
    }



    @GetMapping("/mypage/alarm")
    public MypageAlarmResponse mypagealarminfo(@RequestHeader(value="Authorization", required = false) String authorization){
        if (authorization == null || authorization.isBlank()) {
            throw new InvalidCredentialsException("인증이 필요합니다.");
        }

        return new MypageAlarmResponse(
                List.of(
                        new MypageAlarmResponse.AlarmItem("2024신년계획 잊진 않으셨죠?")
                )
        );
    }

    @GetMapping("/mypage/alarms")
    public MyPageDetailAlarmResponse mypagedetailalarminfo(@RequestHeader(value="Authorization", required = false) String authorization,
                                                           @RequestParam String alarmid){
        if (authorization == null || authorization.isBlank()) {
            throw new InvalidCredentialsException("인증이 필요합니다.");
        }

        return new MyPageDetailAlarmResponse(
                "2024년도 얼마 안남았어요",
                "무료체험 PT받아보기 한 주의 시작을 건강하게! 이땡땡 회원님의 운동 시작을 헬스의 모든것이 도와드리겠습니다."
        );
    }


    @GetMapping("/mypage/chat")
    public MyPageChatResponse mypagechatinfo(@RequestHeader(value="Authorization", required = false) String authorization){
        if (authorization == null || authorization.isBlank()) {
            throw new InvalidCredentialsException("인증이 필요합니다.");
        }

        return new MyPageChatResponse(
                List.of(
                        new MyPageChatResponse.ChatRoomResponse(
                                123L,
                                1001L,
                                "김떙떙트레이너",
                                "김떙땡트레이너.jpg",
                                "다음 주 스케줄 알려드릴게요.",
                                "20251201",
                                3
                        )
                )
        );
    }

}