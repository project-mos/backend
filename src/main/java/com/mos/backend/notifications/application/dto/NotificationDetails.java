package com.mos.backend.notifications.application.dto;

import lombok.Getter;

@Getter
public class NotificationDetails {
    private String recipientId;
    private String title;
    private String content;
    private DataPayloadDto dataPayloadDto;

    public static NotificationDetails forFileUploaded(Long recipientId, String title, String content, DataPayloadDto dataPayloadDto) {
        NotificationDetails notificationDetails = new NotificationDetails();
        notificationDetails.recipientId = recipientId.toString();
        notificationDetails.title = title;
        notificationDetails.content = content;
        notificationDetails.dataPayloadDto = dataPayloadDto;
        return notificationDetails;
    }
}
