package com.example.healthyeverythingapi.member.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyPageChatResponse {

    private List<ChatRoomResponse> data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatRoomResponse {

        private Long roomid;
        private Long partnerid;
        private String partnername;
        private String partnerprofileimageUrl;
        private String lastmessage;
        private String lastmessageat;
        private int unreadcount;
    }
}
