package petPeople.pet.domain.meeting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petPeople.pet.controller.member.dto.resp.MeetingJoinApplyRespDto;
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

        return finMeetingWaitingMemberByMeetingIdFetchJoinMember(meetingId).stream()
                .map(mwm -> createMeetingWaitingMemberRespDto(mwm))
                .collect(Collectors.toList());
    }

    public Slice<MeetingJoinApplyRespDto> retrieveMeetingWaitingMemberApply(Pageable pageable, Member member) {
        Slice<MeetingWaitingMember> MeetingWaitingMemberSlicing = findMeetingWaitingMemberByMemberIdFetchJoinMemberAndMeeting(pageable, member);
        return meetingWaitingMemberSlicingMapToRespDto(MeetingWaitingMemberSlicing);
    }

    private Slice<MeetingJoinApplyRespDto> meetingWaitingMemberSlicingMapToRespDto(Slice<MeetingWaitingMember> meetingWaitingMemberSlice) {
        return meetingWaitingMemberSlice.map(mwm -> createMeetingJoinApplyRespDto(mwm));
    }

    private MeetingJoinApplyRespDto createMeetingJoinApplyRespDto(MeetingWaitingMember mwm) {
        return MeetingJoinApplyRespDto.builder()
                .meetingId(mwm.getMeeting().getId())
                .doName(mwm.getMeeting().getDoName())
                .sigungu(mwm.getMeeting().getSigungu())
                .location(mwm.getMeeting().getLocation())
                .meetingDate(mwm.getMeeting().getMeetingDate())
                .conditions(mwm.getMeeting().getConditions())
                .maxPeople(mwm.getMeeting().getMaxPeople())
                .sex(mwm.getMeeting().getSex().getDetail())
                .category(mwm.getMeeting().getCategory().getDetail())
                .meetingType(mwm.getMeeting().getMeetingType().getDetail())
                .period(mwm.getMeeting().getPeriod())
                .title(mwm.getMeeting().getTitle())
                .content(mwm.getMeeting().getContent())
                .isOpened(mwm.getMeeting().getIsOpened())
                .joinRequestStatus(mwm.getJoinRequestStatus().getDetail())
                .build();
    }


    private MeetingWaitingMemberRespDto createMeetingWaitingMemberRespDto(MeetingWaitingMember meetingWaitingMember) {
        return MeetingWaitingMemberRespDto.builder()
                .memberId(meetingWaitingMember.getMember().getId())
                .meetingId(meetingWaitingMember.getMeeting().getId())
                .nickname(meetingWaitingMember.getMember().getNickname())
                .introduce(meetingWaitingMember.getMember().getIntroduce())
                .joinRequestStatus(meetingWaitingMember.getJoinRequestStatus().getDetail())
                .createDate(meetingWaitingMember.getCreatedDate())
                .build();
    }

    private Slice<MeetingWaitingMember> findMeetingWaitingMemberByMemberIdFetchJoinMemberAndMeeting(Pageable pageable, Member member) {
        return meetingWaitingMemberRepository.findAllByMemberIdFetchJoinMemberAndMeeting(pageable, member.getId());
    }

    private List<MeetingWaitingMember> finMeetingWaitingMemberByMeetingIdFetchJoinMember(Long meetingId) {
        return meetingWaitingMemberRepository.findAllByMeetingIdFetchJoinMember(meetingId);
    }

    private void validateAuthorization(Long id, Long targetId) {
        if (id != targetId) {
            throwException(ErrorCode.FORBIDDEN_MEMBER, "해당 모임에 권한이 없습니다.");
        }
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
