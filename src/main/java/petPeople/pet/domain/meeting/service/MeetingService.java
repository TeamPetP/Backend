package petPeople.pet.domain.meeting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petPeople.pet.controller.meeting.dto.req.MeetingCreateReqDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingCreateRespDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingRetrieveRespDto;
import petPeople.pet.domain.meeting.entity.Meeting;
import petPeople.pet.domain.meeting.entity.MeetingImage;
import petPeople.pet.domain.meeting.entity.MeetingMember;
import petPeople.pet.domain.meeting.repository.MeetingImageRepository;
import petPeople.pet.domain.meeting.repository.MeetingMemberRepository;
import petPeople.pet.domain.meeting.repository.MeetingRepository;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.exception.CustomException;
import petPeople.pet.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingImageRepository meetingImageRepository;
    private final MeetingMemberRepository meetingMemberRepository;

    @Transactional
    public MeetingCreateRespDto create(Member member, MeetingCreateReqDto meetingCreateReqDto) {
        validateEndDateBeforeMeetingDate(meetingCreateReqDto.getMeetingDate(), meetingCreateReqDto.getEndDate());

        Meeting saveMeeting = saveMeeting(member, meetingCreateReqDto);
        saveMeetingMember(member, saveMeeting);

        List<MeetingImage> meetingImageList = new ArrayList<>();
        for (String url : meetingCreateReqDto.getImgUrlList()) {
            meetingImageList.add(saveMeetingImage(createMeetingImage(saveMeeting, url)));
        }

        return new MeetingCreateRespDto(saveMeeting, meetingImageList);
    }

    public MeetingRetrieveRespDto retrieveOne(Long meetingId) {
        Meeting meeting = validateOptionalPost(findOptionalMeetingByMeetingId(meetingId));
        List<MeetingImage> meetingImageList = findMeetingImageListByMeetingId(meetingId);
        List<MeetingMember> meetingMemberList = findMeetingMemberListByMeetingId(meetingId);

        return new MeetingRetrieveRespDto(meeting, meetingImageList, meetingMemberList);
    }

    private List<MeetingImage> findMeetingImageListByMeetingId(Long meetingId) {
        return meetingImageRepository.findByMeetingId(meetingId);
    }

    private List<MeetingMember> findMeetingMemberListByMeetingId(Long meetingId) {
        return meetingMemberRepository.findByMeetingId(meetingId);
    }

    private Meeting validateOptionalPost(Optional<Meeting> optionalMeeting) {
        return optionalMeeting.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEETING, "존재하지 않은 모임입니다."));
    }

    private Optional<Meeting> findOptionalMeetingByMeetingId(Long meetingId) {
        return meetingRepository.findById(meetingId);
    }

    private void validateEndDateBeforeMeetingDate(LocalDateTime meetingDate, LocalDateTime endDate) {
        if (meetingDate.isBefore(endDate)) {
            throwException(ErrorCode.BAD_REQUEST_PARAM, "모집 마감 시간을 미팅 시간 이전으로 선택하여야 합니다!");
        }
    }

    private void throwException(ErrorCode errorCode, String message) {
        throw new CustomException(errorCode, message);
    }
    
    private MeetingImage saveMeetingImage(MeetingImage meetingImage) {
        return meetingImageRepository.save(meetingImage);
    }

    private MeetingImage createMeetingImage(Meeting meeting, String url) {
        return MeetingImage.builder()
                .meeting(meeting)
                .imgUrl(url)
                .build();
    }

    private MeetingMember saveMeetingMember(Member member, Meeting meeting) {
        return meetingMemberRepository.save(createMeetingMember(member, meeting));
    }

    private Meeting saveMeeting(Member member, MeetingCreateReqDto meetingCreateReqDto) {
        return meetingRepository.save(createMeeting(member, meetingCreateReqDto));
    }

    private MeetingMember createMeetingMember(Member member, Meeting meeting) {
        return MeetingMember.builder()
                .meeting(meeting)
                .member(member)
                .build();
    }

    private Meeting createMeeting(Member member, MeetingCreateReqDto meetingCreateReqDto) {
        return Meeting.builder()
                .member(member)
                .doName(meetingCreateReqDto.getDoName())
                .sigungu(meetingCreateReqDto.getSigungu())
                .endDate(meetingCreateReqDto.getEndDate())
                .meetingDate(meetingCreateReqDto.getMeetingDate())
                .conditions(meetingCreateReqDto.getConditions())
                .maxPeople(meetingCreateReqDto.getMaxPeople())
                .sex(meetingCreateReqDto.getSex())
                .category(meetingCreateReqDto.getCategory())
                .maxAge(meetingCreateReqDto.getMaxAge())
                .minAge(meetingCreateReqDto.getMinAge())
                .title(meetingCreateReqDto.getTitle())
                .content(meetingCreateReqDto.getContent())
                .isOpened(true)
                .build();
    }


}
