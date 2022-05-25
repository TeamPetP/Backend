package petPeople.pet.domain.meeting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petPeople.pet.controller.member.dto.resp.MeetingWaitingMemberRespDto;
import petPeople.pet.domain.meeting.entity.Meeting;
import petPeople.pet.domain.meeting.entity.MeetingWaitingMember;
import petPeople.pet.domain.meeting.repository.MeetingRepository;
import petPeople.pet.domain.meeting.repository.MeetingWaitingMemberRepository;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.exception.CustomException;
import petPeople.pet.exception.ErrorCode;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MeetingWaitingMemberService {

    private final MeetingWaitingMemberRepository meetingWaitingMemberRepository;
    private final MeetingRepository meetingRepository;

    public List<MeetingWaitingMemberRespDto> retrieveMeetingWaitingMember(Member member, Long meetingId) {

        Meeting findMeeting = validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));
        validateAuthorization(member.getId(), findMeeting.getMember().getId());

        return finMeetingWaitingMemberByMeetingId(meetingId).stream()
                .map(mwm ->
                        new MeetingWaitingMemberRespDto(
                                mwm.getMember().getId(), mwm.getMeeting().getId(), mwm.getMember().getNickname(), mwm.getCreatedDate())
                )
                .collect(Collectors.toList());
    }

    private List<MeetingWaitingMember> finMeetingWaitingMemberByMeetingId(Long meetingId) {
        return meetingWaitingMemberRepository.findAllByMeetingId(meetingId);
    }

    private void validateAuthorization(Long id, Long targetId) {
        if (isaNotSame(id, targetId)) {
            throwException(ErrorCode.FORBIDDEN_MEMBER, "해당 모임에 권한이 없습니다.");
        }
    }

    private boolean isaNotSame(Long id, Long targetId) {
        return id != targetId;
    }

    private void throwException(ErrorCode errorCode, String message) {
        throw new CustomException(errorCode, message);
    }

    private Optional<Meeting> findOptionalMeetingByMeetingId(Long meetingId) {
        return meetingRepository.findById(meetingId);
    }

    private Meeting validateOptionalMeeting(Optional<Meeting> optionalMeeting) {
        return optionalMeeting.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEETING, "존재하지 않은 모임입니다."));
    }

}
