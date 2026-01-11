package com.example.healthyeverythingapi.member.service;

import com.example.healthyeverythingapi.common.exception.InvalidCredentialsException;
import com.example.healthyeverythingapi.member.domain.Alarm;
import com.example.healthyeverythingapi.member.domain.ChatRoom;
import com.example.healthyeverythingapi.member.domain.MemberProfile;
import com.example.healthyeverythingapi.member.domain.Review;
import com.example.healthyeverythingapi.member.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberProfileService memberProfileService;
    private final ReviewService reviewService;
    private final AlarmService alarmService;
    private final ChatRoomService chatRoomService;

    private void validateAuthorization(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new InvalidCredentialsException("인증이 필요합니다.");
        }
    }

    public MyPageResponse getMyPageInfo(String authorization) {
        validateAuthorization(authorization);

        MemberProfile profile = memberProfileService.findByUserId(1L);

        return new MyPageResponse(
                profile.getUsername(),
                profile.getUseremail(),
                profile.getUserphonenumber()
        );
    }

    public MyPageReviewResponse getMyPageReviews(String authorization) {
        validateAuthorization(authorization);

        List<Review> reviews = reviewService.findByUserId(1L);

        List<MyPageReviewResponse.ReviewItem> reviewItems = reviews.stream()
                .map(r -> new MyPageReviewResponse.ReviewItem(r.getSubject()))
                .toList();

        return new MyPageReviewResponse(reviewItems);
    }

    public MyPageDetailReViewResponse getMyPageReviewDetail(String authorization, String reviewId) {
        validateAuthorization(authorization);

        Review review = reviewService.findById(Long.parseLong(reviewId));

        return new MyPageDetailReViewResponse(
                review.getSubject(),
                review.getContent()
        );
    }

    public MypageAlarmResponse getMyPageAlarms(String authorization) {
        validateAuthorization(authorization);

        List<Alarm> alarms = alarmService.findByUserId(1L);

        List<MypageAlarmResponse.AlarmItem> alarmItems = alarms.stream()
                .map(a -> new MypageAlarmResponse.AlarmItem(a.getSubject()))
                .toList();

        return new MypageAlarmResponse(alarmItems);
    }

    public MyPageDetailAlarmResponse getMyPageAlarmDetail(String authorization, String alarmId) {
        validateAuthorization(authorization);

        Alarm alarm = alarmService.findById(Long.parseLong(alarmId));

        return new MyPageDetailAlarmResponse(
                alarm.getSubject(),
                alarm.getContent()
        );
    }

    public MyPageChatResponse getMyPageChats(String authorization) {
        validateAuthorization(authorization);

        List<ChatRoom> chatRooms = chatRoomService.findByUserId(1L);

        List<MyPageChatResponse.ChatRoomResponse> chatRoomResponses = chatRooms.stream()
                .map(c -> new MyPageChatResponse.ChatRoomResponse(
                        c.getId(),
                        c.getPartnerId(),
                        c.getPartnerName(),
                        c.getPartnerProfileImageUrl(),
                        c.getLastMessage(),
                        c.getLastMessageAt(),
                        c.getUnreadCount()
                ))
                .toList();

        return new MyPageChatResponse(chatRoomResponses);
    }
}