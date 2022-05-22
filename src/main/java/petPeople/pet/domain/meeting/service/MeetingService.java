package petPeople.pet.domain.meeting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petPeople.pet.controller.meeting.dto.req.MeetingCreateReqDto;
import petPeople.pet.controller.meeting.dto.req.MeetingEditReqDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingCreateRespDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingEditRespDto;
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

    @Transactional
    public MeetingEditRespDto editMeeting(Member member, Long meetingId, MeetingEditReqDto meetingEditReqDto) {
        validateEndDateBeforeMeetingDate(meetingEditReqDto.getMeetingDate(), meetingEditReqDto.getEndDate());

        Meeting findMeeting = validateOptionalPost(findOptionalMeetingByMeetingId(meetingId));

        validateMemberAuthorization(member, findMeeting.getMember());

        editMeeting(meetingEditReqDto, findMeeting);
        deleteMeetingImageByMeetingId(meetingId);

        List<MeetingImage> meetingImageList = new ArrayList<>();
        for (String url : meetingEditReqDto.getImgUrlList()) {
            meetingImageList.add(saveMeetingImage(createMeetingImage(findMeeting, url)));
        }

        return new MeetingEditRespDto(findMeeting, meetingImageList);
    }

    private void editMeeting(MeetingEditReqDto meetingEditReqDto, Meeting meeting) {
        meeting.edit(meetingEditReqDto);
    }

    private void validateMemberAuthorization(Member member, Member targetMember) {
        if (isaNotSameMember(member, targetMember)) {
            throwException(ErrorCode.FORBIDDEN_MEMBER, "해당 모임에 권한이 없습니다.");
        }
    }

    private boolean isaNotSameMember(Member member, Member postMember) {
        return member != postMember;
    }

    private void deleteMeetingImageByMeetingId(Long meetingId) {
        meetingImageRepository.deleteByMeetingId(meetingId);
    }

    public MeetingRetrieveRespDto retrieveOne(Long meetingId) {
        Meeting meeting = validateOptionalPost(findOptionalMeetingByMeetingId(meetingId));
        List<MeetingImage> meetingImageList = findMeetingImageListByMeetingId(meetingId);
        List<MeetingMember> meetingMemberList = findMeetingMemberListByMeetingId(meetingId);

        return new MeetingRetrieveRespDto(meeting, meetingImageList, meetingMemberList);
    }

    public Slice<MeetingRetrieveRespDto> retrieveAll(Pageable pageable) {
        Slice<Meeting> meetingSlice = findAllMeetingSlicingWithFetchJoinMember(pageable);
        List<Long> meetingIds = getMeetingId(meetingSlice.getContent());
        return meetingSliceMapToRespDto(
                meetingSlice,
                findMeetingImageByMeetingIds(meetingIds),
                findMeetingMemberByMeetingIds(meetingIds)
        );
    }

    private Slice<MeetingRetrieveRespDto> meetingSliceMapToRespDto(Slice<Meeting> meetingSlice, List<MeetingImage> meetingImageList, List<MeetingMember> meetingMemberList) {
        return meetingSlice.map(meeting ->
            new MeetingRetrieveRespDto(
                    meeting,
                    getMeetingImagesByMeeting(meetingImageList, meeting),
                    getMeetingMembersByMeeting(meetingMemberList, meeting))
        );
    }

    private List<MeetingMember> getMeetingMembersByMeeting(List<MeetingMember> meetingMemberList, Meeting meeting) {
        List<MeetingMember> meetingMembers = new ArrayList<>();
        for (MeetingMember meetingMember : meetingMemberList) {
            if (meetingMember.getMeeting() == meeting) {
                meetingMembers.add(meetingMember);
            }
        }
        return meetingMembers;
    }

    private List<MeetingImage> getMeetingImagesByMeeting(List<MeetingImage> meetingImageList, Meeting meeting) {
        List<MeetingImage> meetingImages = new ArrayList<>();
        for (MeetingImage meetingImage : meetingImageList) {
            if (meetingImage.getMeeting() == meeting) {
                meetingImages.add(meetingImage);
            }
        }
        return meetingImages;
    }

    private List<MeetingMember> findMeetingMemberByMeetingIds(List<Long> meetingIds) {
        return meetingMemberRepository.findByMeetingIds(meetingIds);
    }

    private List<MeetingImage> findMeetingImageByMeetingIds(List<Long> meetingIds) {
        return meetingImageRepository.findByMeetingIds(meetingIds);
    }

    private Slice<Meeting> findAllMeetingSlicingWithFetchJoinMember(Pageable pageable) {
        return meetingRepository.findAllSlicingWithFetchJoinMember(pageable);
    }

    private List<Long> getMeetingId(List<Meeting> content) {
        List<Long> ids = new ArrayList<>();
        for (Meeting meeting : content) {
            ids.add(meeting.getId());
        }
        return ids;
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
