package petPeople.pet.domain.meeting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petPeople.pet.controller.dto.req.MeetingCreateReqDto;
import petPeople.pet.controller.dto.resp.MeetingCreateRespDto;
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
            meetingImageList.add(saveMeetingImage(createMeetingImage(member, saveMeeting, url)));
        }

        return new MeetingCreateRespDto(saveMeeting, meetingImageList);
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

    private MeetingImage createMeetingImage(Member member, Meeting meeting, String url) {
        return MeetingImage.builder()
                .member(member)
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
