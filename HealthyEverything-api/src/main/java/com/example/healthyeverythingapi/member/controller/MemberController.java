package com.example.healthyeverythingapi.member.controller;

import com.example.healthyeverythingapi.member.dto.*;
import com.example.healthyeverythingapi.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/mypage")
    public MyPageResponse mypageinfo(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return memberService.getMyPageInfo(authorization);
    }

    @GetMapping("/mypage/review")
    public MyPageReviewResponse mypagereviewinfo(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return memberService.getMyPageReviews(authorization);
    }

    @GetMapping("/mypage/reviews")
    public MyPageDetailReViewResponse mypagereviewdetailinfo(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                             @RequestParam String reviewid) {
        return memberService.getMyPageReviewDetail(authorization, reviewid);
    }

    @GetMapping("/mypage/alarm")
    public MypageAlarmResponse mypagealarminfo(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return memberService.getMyPageAlarms(authorization);
    }

    @GetMapping("/mypage/alarms")
    public MyPageDetailAlarmResponse mypagedetailalarminfo(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                           @RequestParam String alarmid) {
        return memberService.getMyPageAlarmDetail(authorization, alarmid);
    }

    @GetMapping("/mypage/chat")
    public MyPageChatResponse mypagechatinfo(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return memberService.getMyPageChats(authorization);
    }
}
