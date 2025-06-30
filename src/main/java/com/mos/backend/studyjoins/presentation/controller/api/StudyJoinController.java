package com.mos.backend.studyjoins.presentation.controller.api;

import com.mos.backend.studyjoins.application.StudyJoinService;
import com.mos.backend.studyjoins.application.res.MyStudyJoinRes;
import com.mos.backend.studyjoins.application.res.StudyJoinRes;
import com.mos.backend.studyjoins.presentation.controller.req.StudyJoinReq;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class StudyJoinController {

    private final StudyJoinService studyJoinService;

    @PostMapping("/studies/{studyId}/study-joins")
    @ResponseStatus(HttpStatus.OK)
    public void joinStudy(@AuthenticationPrincipal Long userId,
                          @PathVariable Long studyId,
                          @RequestBody(required = false) List<StudyJoinReq> studyJoinReqs) {
        studyJoinService.joinStudy(userId, studyId, studyJoinReqs);
    }

    @GetMapping("/study-joins")
    @ResponseStatus(HttpStatus.OK)
    public List<MyStudyJoinRes> getMyStudyJoins(@AuthenticationPrincipal Long userId, @RequestParam(required = false) String studyJoinStatusCond) {
        return studyJoinService.getMyStudyJoins(userId, studyJoinStatusCond);
    }

    @GetMapping("/studies/{studyId}/study-joins")
    @ResponseStatus(HttpStatus.OK)
    public List<StudyJoinRes> getStudyJoins(@PathVariable Long studyId, @RequestParam(required = false) String studyJoinStatusCond) {
        return studyJoinService.getStudyJoins(studyId, studyJoinStatusCond);
    }

    @PatchMapping("/studies/{studyId}/study-joins/{studyJoinId}/approval")
    @ResponseStatus(HttpStatus.OK)
    public void approveStudyJoin(@PathVariable Long studyId, @PathVariable Long studyJoinId) {
        studyJoinService.approveStudyJoin(studyId, studyJoinId);
    }

    @PatchMapping("/studies/{studyId}/study-joins/{studyJoinId}/rejection")
    @ResponseStatus(HttpStatus.OK)
    public void rejectStudyJoin(@PathVariable Long studyId, @PathVariable Long studyJoinId) {
        studyJoinService.rejectStudyJoin(studyId, studyJoinId);
    }

    @PatchMapping("/studies/{studyId}/study-joins/{studyJoinId}")
    @ResponseStatus(HttpStatus.OK)
    public void cancelStudyJoin(@PathVariable Long studyId, @PathVariable Long studyJoinId) {
        studyJoinService.cancelStudyJoin(studyId, studyJoinId);
    }
}
