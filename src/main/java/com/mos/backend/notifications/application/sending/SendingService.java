package com.mos.backend.notifications.application.sending;

import com.mos.backend.notifications.application.dto.payload.DataPayload;

import java.util.List;

public interface SendingService {

    void sendMulticastMessage(List<Long> recipientIds, String title, String content, DataPayload dataPayload);
}
