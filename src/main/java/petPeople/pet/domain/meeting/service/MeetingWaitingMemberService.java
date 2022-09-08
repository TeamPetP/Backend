package petPeople.pet.domain.meeting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petPeople.pet.controller.member.dto.resp.MeetingJoinApplyRespDto;
import petPeople.pet.controller.member.dto.resp.MeetingWaitingMemberRespDto;
import petPeople.pet.domain.meeting.entity.Meeting;
import petPeople.pet.domain.meeting.entity.MeetingMember;
import petPeople.pet.domain.meeting.entity.MeetingWaitingMember;
import petPeople.pet.domain.meeting.repository.meeting_member.MeetingMemberRepository;
import petPeople.pet.domain.meeting.repository.meeting.MeetingRepository;
import petPeople.pet.domain.meeting.repository.meeting_waiting_member.MeetingWaitingMemberRepository;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.exception.CustomException;
import petPeople.pet.exception.ErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MeetingWaitingMemberService {

    private final MeetingMemberRepository meetingMemberRepository;
    private final MeetingWaitingMemberRepository meetingWaitingMemberRepository;
    private final MeetingRepository meetingRepository;

    //모임 신청 대기자 조회
    public List<MeetingWaitingMemberRespDto> retrieveMeetingWaitingMember(Member member, Long meetingId) {

        Meeting findMeeting = validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));
        validateAuthorization(member, findMeeting.getMember());

        return finMeetingWaitingMemberByMeetingIdFetchJoinMember(meetingId).stream()
                .map(mwm -> createMeetingWaitingMemberRespDto(mwm))
                .collect(Collectors.toList());
    }

    //내가 가입 신청한 모임 현황 및 모임에 가입된 회원 조회
    public Slice<MeetingJoinApplyRespDto> retrieveMeetingWaitingMemberApply(Pageable pageable, Member member) {
        Slice<MeetingWaitingMember> MeetingWaitingMemberSlicing = findMeetingWaitingMemberByMemberIdFetchJoinMemberAndMeeting(pageable, member);
        List<MeetingWaitingMember> content = MeetingWaitingMemberSlicing.getContent();

        List<Long> ids = new ArrayList<>();
        for (MeetingWaitingMember meetingWaitingMember : content) {
            ids.add(meetingWaitingMember.getMeeting().getId());
        }

        List<MeetingMember> meetingMemberList = findMeetingMemberListByMeetingIds(ids);

        return meetingWaitingMemberSlicingMapToRespDto(MeetingWaitingMemberSlicing, meetingMemberList);
    }

    private Slice<MeetingJoinApplyRespDto> meetingWaitingMemberSlicingMapToRespDto(Slice<MeetingWaitingMember> meetingWaitingMemberSlice, List<MeetingMember> meetingMemberList) {
        return meetingWaitingMemberSlice.map(mwm -> {
            List<Member> joinMembers = new ArrayList<>();
            for (MeetingMember meetingMember : meetingMemberList) {
                if (meetingMember.getMeeting() == mwm.getMeeting()) {
                    joinMembers.add(meetingMember.getMember());
                }
            }
            return new MeetingJoinApplyRespDto(mwm, joinMembers);
        });
    }

    private List<MeetingMember> findMeetingMemberListByMeetingIds(List<Long> meetingIds) {
        return meetingMemberRepository.findByMeetingIds(meetingIds);
    }

    private MeetingWaitingMemberRespDto createMeetingWaitingMemberRespDto(MeetingWaitingMember meetingWaitingMember) {
        return MeetingWaitingMemberRespDto.builder()
                .memberId(meetingWaitingMember.getMember().getId())
                .memberImgUrl(meetingWaitingMember.getMember().getImgUrl())
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

    private void validateAuthorization(Member loginMember, Member targetMember) {
        if (loginMember != targetMember) {
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
