package com.mos.backend.userstudylikes.application;

import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.hotstudies.application.HotStudyService;
import com.mos.backend.hotstudies.entity.HotStudyEventType;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.users.entity.User;
import com.mos.backend.userstudylikes.entity.UserStudyLike;
import com.mos.backend.userstudylikes.infrastructure.UserStudyLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserStudyLikeService {

    private final UserStudyLikeRepository userStudyLikeRepository;
    private final EntityFacade entityFacade;
    private final HotStudyService hotStudyService;


    @Transactional
    public void like(Long studyId, Long userId) {
        Study study = entityFacade.getStudy(studyId);
        User user = entityFacade.getUser(userId);
        if (!existsUserStudyLike(user, study)) {
            userStudyLikeRepository.save(UserStudyLike.create(user, study));
        }
        hotStudyService.handleEvent(HotStudyEventType.LIKE, studyId);
    }

    @Transactional
    public void unlike(Long studyId, Long userId) {
        Study study = entityFacade.getStudy(studyId);
        User user = entityFacade.getUser(userId);
        if (existsUserStudyLike(user, study)) {
            userStudyLikeRepository.deleteByUserAndStudy(user, study);
        }
        hotStudyService.handleEvent(HotStudyEventType.LIKE_CANCEL, studyId);
    }

    private boolean existsUserStudyLike(User user, Study study) {
        return userStudyLikeRepository.existsUserStudyLikeByUserAndStudy(user, study);
    }



}
