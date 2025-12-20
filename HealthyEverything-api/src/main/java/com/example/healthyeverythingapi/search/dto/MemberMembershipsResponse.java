package com.example.healthyeverythingapi.search.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MemberMembershipsResponse {

    private List<Membership> memberships;

    @Getter
    @AllArgsConstructor
    public static class Membership {

        private Long membershipId;

        private Long trainerId;
        private String trainerName;
        private String trainerProfileImageUrl;
        private String gymName;

        private String productName;

        private int totalSessions;
        private int remainingSessions;

        private String status;

        private String registeredAt;
    }
}