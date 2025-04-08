package com.mos.backend.notifications.application.sending;

import com.mos.backend.notifications.application.dto.DataPayloadDto;

public interface SendingService {

    void sendMessage(Long userId, String title, String content, DataPayloadDto dataPayloadDto);
}
