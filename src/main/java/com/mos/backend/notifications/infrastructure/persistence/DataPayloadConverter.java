package com.mos.backend.notifications.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mos.backend.notifications.application.dto.payload.DataPayload;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@Converter
public class DataPayloadConverter implements AttributeConverter<DataPayload, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 객체를 JSON 문자열로 반환
     */
    @Override
    public String convertToDatabaseColumn(DataPayload dataPayload) {
        if (dataPayload == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(dataPayload);
        } catch (JsonProcessingException e) {
            log.error("DataPayload를 JSON으로 직렬화하는데 실패했습니다.", e);
            throw new IllegalArgumentException("DataPayload를 JSON으로 변환 중 오류 발생", e);
        }
    }

    /**
     * JSON 문자열을 객체로 반환
     */
    @Override
    public DataPayload convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, DataPayload.class);
        } catch (IOException e) {
            log.error("JSON을 DataPayload로 역직렬화하는데 실패했습니다.", e);
            throw new IllegalArgumentException("JSON을 DataPayload로 변환 중 오류 발생", e);
        }
    }
}
