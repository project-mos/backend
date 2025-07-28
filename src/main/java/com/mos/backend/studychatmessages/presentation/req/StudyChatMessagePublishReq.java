package com.mos.backend.studychatmessages.presentation.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudyChatMessagePublishReq {
    private Long studyChatRoomId;
    private String message;
}
