package com.example.healthyeverythingapi.member.service;

import com.example.healthyeverythingapi.common.exception.InvalidCredentialsException;
import com.example.healthyeverythingapi.member.domain.Alarm;
import com.example.healthyeverythingapi.member.domain.ChatRoom;
import com.example.healthyeverythingapi.member.domain.MemberProfile;
import com.example.healthyeverythingapi.member.domain.Review;
import com.example.healthyeverythingapi.member.dto.*;
import com.example.healthyeverythingapi.member.repository.AlarmRepository;
import com.example.healthyeverythingapi.member.repository.ChatRoomRepository;
import com.example.healthyeverythingapi.member.repository.MemberProfileRepository;
import com.example.healthyeverythingapi.member.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberProfileRepository memberProfileRepository;
    private final ReviewRepository reviewRepository;
    private final AlarmRepository alarmRepository;
    private final ChatRoomRepository chatRoomRepository;

    private void validateAuthorization(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new InvalidCredentialsException("인증이 필요합니다.");
        }
    }

    @Transactional(readOnly = true)
    public MyPageResponse getMyPageInfo(String authorization) {
        validateAuthorization(authorization);

        // 테스트용 userId = 1L 사용
        MemberProfile profile = memberProfileRepository.findByUserId(1L)
                .orElseThrow(() -> new IllegalArgumentException("MEMBER_NOT_FOUND"));

        return new MyPageResponse(
                profile.getUsername(),
                profile.getUseremail(),
                profile.getUserphonenumber()
        );
    }

    @Transactional(readOnly = true)
    public MyPageReviewResponse getMyPageReviews(String authorization) {
        validateAuthorization(authorization);

        List<Review> reviews = reviewRepository.findByUserId(1L);

        List<MyPageReviewResponse.ReviewItem> reviewItems = reviews.stream()
                .map(r -> new MyPageReviewResponse.ReviewItem(r.getSubject()))
                .toList();

        return new MyPageReviewResponse(reviewItems);
    }

    @Transactional(readOnly = true)
    public MyPageDetailReViewResponse getMyPageReviewDetail(String authorization, String reviewId) {
        validateAuthorization(authorization);

        Review review = reviewRepository.findById(Long.parseLong(reviewId))
                .orElseThrow(() -> new IllegalArgumentException("REVIEW_NOT_FOUND"));

        return new MyPageDetailReViewResponse(
                review.getSubject(),
                review.getContent()
        );
    }

    @Transactional(readOnly = true)
    public MypageAlarmResponse getMyPageAlarms(String authorization) {
        validateAuthorization(authorization);

        List<Alarm> alarms = alarmRepository.findByUserId(1L);

        List<MypageAlarmResponse.AlarmItem> alarmItems = alarms.stream()
                .map(a -> new MypageAlarmResponse.AlarmItem(a.getSubject()))
                .toList();

        return new MypageAlarmResponse(alarmItems);
    }

    @Transactional(readOnly = true)
    public MyPageDetailAlarmResponse getMyPageAlarmDetail(String authorization, String alarmId) {
        validateAuthorization(authorization);

        Alarm alarm = alarmRepository.findById(Long.parseLong(alarmId))
                .orElseThrow(() -> new IllegalArgumentException("ALARM_NOT_FOUND"));

        return new MyPageDetailAlarmResponse(
                alarm.getSubject(),
                alarm.getContent()
        );
    }

    @Transactional(readOnly = true)
    public MyPageChatResponse getMyPageChats(String authorization) {
        validateAuthorization(authorization);

        List<ChatRoom> chatRooms = chatRoomRepository.findByUserId(1L);

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
