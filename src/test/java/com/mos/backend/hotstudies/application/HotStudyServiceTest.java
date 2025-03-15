package com.mos.backend.hotstudies.application;

import com.mos.backend.hotstudies.application.hotstudyeventhandler.EventHandler;
import com.mos.backend.hotstudies.entity.HotStudyEventType;
import com.mos.backend.hotstudies.infrastructure.HotStudyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotStudyServiceTest {

    @Mock
    private HotStudyEventHandlerDispatcher hotStudyEventHandlerDispatcher;

    @Mock
    private HotStudyRepository hotStudyRepository;

    @Mock
    private HotStudyScoreUpdater hotStudyScoreUpdater;

    @Mock
    private EventHandler eventHandler;

    @InjectMocks
    private HotStudyService hotStudyService;

    @Nested
    @DisplayName("handleEvent() 테스트")
    class HandleEventTest {
        @Test
        @DisplayName("생성, 수정 관련 이벤트 처리")
        void handleEvent_CreateOrUpdate() {
            // Given
            for (HotStudyEventType eventType : HotStudyEventType.values()) {
                if (HotStudyEventType.DELETE.equals(eventType)) continue;

                Long studyId = 1L;
                when(hotStudyEventHandlerDispatcher.getHotStudyEventHandler(eventType)).thenReturn(eventHandler);
                // When
                hotStudyService.handleEvent(eventType, studyId);

                // Then
                verify(eventHandler).handle(studyId, eventType);
                verify(hotStudyEventHandlerDispatcher).getHotStudyEventHandler(eventType);
                verify(hotStudyScoreUpdater).update(studyId);
                reset(eventHandler, hotStudyEventHandlerDispatcher, hotStudyScoreUpdater);
            }
        }

        @Test
        @DisplayName("삭제 이벤트 처리")
        void handleEvent_Delete() {
            // Given
            Long studyId = 1L;
            HotStudyEventType eventType = HotStudyEventType.DELETE;
            when(hotStudyEventHandlerDispatcher.getHotStudyEventHandler(eventType)).thenReturn(eventHandler);

            // When
            hotStudyService.handleEvent(eventType, studyId);

            // Then
            verify(hotStudyEventHandlerDispatcher).getHotStudyEventHandler(eventType);
            verify(eventHandler).handle(studyId, eventType);
            verify(hotStudyRepository).remove(studyId);
            verify(hotStudyScoreUpdater, never()).update(any());

        }
    }
}