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
import petPeople.pet.domain.meeting.entity.*;
import petPeople.pet.domain.meeting.repository.MeetingImageRepository;
import petPeople.pet.domain.meeting.repository.MeetingMemberRepository;
import petPeople.pet.domain.meeting.repository.MeetingRepository;
import petPeople.pet.domain.meeting.repository.MeetingWaitingMemberRepository;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.exception.CustomException;
import petPeople.pet.exception.ErrorCode;

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
    private final MeetingWaitingMemberRepository meetingWaitingMemberRepository;

    @Transactional
    public MeetingCreateRespDto create(Member member, MeetingCreateReqDto meetingCreateReqDto) {

        Meeting saveMeeting = saveMeeting(createMeeting(member, meetingCreateReqDto));
        saveMeetingMember(createMeetingMember(member, saveMeeting));

        List<MeetingImage> meetingImageList = new ArrayList<>();
        for (String url : meetingCreateReqDto.getImgUrlList()) {
            meetingImageList.add(saveMeetingImage(createMeetingImage(saveMeeting, url)));
        }

        return new MeetingCreateRespDto(saveMeeting, meetingImageList);
    }

    @Transactional
    public MeetingEditRespDto edit(Member member, Long meetingId, MeetingEditReqDto meetingEditReqDto) {
        Meeting findMeeting = validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));

        validateMemberAuthorization(member, findMeeting.getMember());

        editMeeting(meetingEditReqDto, findMeeting);
        deleteMeetingImageByMeetingId(meetingId);

        List<MeetingImage> meetingImageList = new ArrayList<>();
        for (String url : meetingEditReqDto.getImgUrlList()) {
            meetingImageList.add(saveMeetingImage(createMeetingImage(findMeeting, url)));
        }

        return new MeetingEditRespDto(findMeeting, meetingImageList);
    }

    // TODO: 2022-05-23 모임에 max 회원 도달 경우 자동으로 isOpened 바꿀지(가입 후 max에 찰 경우 자동으로 마감 처리)
    // TODO: 2022-05-23 모집 기간이 지난 모임 가입 가입 불가로(모집 마감 시간이 넘을 경우 프론트에서 마감으로 처리하기)
    @Transactional
    public void joinRequest(Member member, Long meetingId) {
        Meeting meeting = validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));
        Long joinMemberCount = countMeetingMember(meetingId);

        validateOpenedMeeting(meeting.getIsOpened());//모집 상태인지
        validateDuplicatedJoin(member, meetingId);//중복 가입인지
        validateFullMeeting(meeting.getMaxPeople(), joinMemberCount);//인원이 다 찼는지

        saveMeetingWaitingMember(createMeetingWaitingMember(member, meeting));

    }

    public MeetingRetrieveRespDto retrieveOne(Long meetingId) {
        Meeting meeting = validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));
        List<MeetingImage> meetingImageList = findMeetingImageListByMeetingId(meetingId);
        List<MeetingMember> meetingMemberList = findMeetingMemberListByMeetingId(meetingId);

        return new MeetingRetrieveRespDto(meeting, meetingImageList, meetingMemberList);
    }

    public Slice<MeetingRetrieveRespDto> retrieveAll(Pageable pageable) {
        Slice<Meeting> meetingSlice = findAllMeetingSlicingWithFetchJoinMember(pageable);
        List<Long> meetingIds = getMeetingId(meetingSlice.getContent());
        return meetingSliceMapToRetrieveRespDto(
                meetingSlice,
                findMeetingImageByMeetingIds(meetingIds),
                findMeetingMemberByMeetingIds(meetingIds)
        );
    }

    public Slice<MeetingRetrieveRespDto> retrieveMemberMeeting(Member member, Pageable pageable) {
        Slice<Meeting> meetingSlice = findAllMeetingSlicingByMemberId(member, pageable);
        List<Long> meetingIds = getMeetingId(meetingSlice.getContent());
        return meetingSliceMapToRetrieveRespDto(
                meetingSlice,
                findMeetingImageByMeetingIds(meetingIds),
                findMeetingMemberByMeetingIds(meetingIds)
        );
    }

    @Transactional
    public void approve(Member member, Long meetingId, Long memberId) {
        Meeting findMeeting = validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));

        validateMemberAuthorization(member, findMeeting.getMember());

        changeMeetingWaitingMemberStatusApproved(validateOptionalMeetingWaitingMember(findOptionalMeetingWaitingMemberByMeetingIdAndMemberId(meetingId, memberId)));

        saveMeetingMember(createMeetingMember(member, findMeeting));
    }

    private void changeMeetingWaitingMemberStatusApproved(MeetingWaitingMember meetingWaitingMember) {
        meetingWaitingMember.setJoinRequestStatus(JoinRequestStatus.APPROVED);
    }

    private Optional<MeetingWaitingMember> findOptionalMeetingWaitingMemberByMeetingIdAndMemberId(Long meetingId, Long memberId) {
        return meetingWaitingMemberRepository.findByMeetingIdAndMemberId(meetingId, memberId);
    }

    private Slice<Meeting> findAllMeetingSlicingByMemberId(Member member, Pageable pageable) {
        return meetingRepository.findAllSlicingByMemberId(pageable, member.getId());
    }

    private MeetingWaitingMember saveMeetingWaitingMember(MeetingWaitingMember meetingWaitingMember) {
        return meetingWaitingMemberRepository.save(meetingWaitingMember);
    }

    private MeetingWaitingMember createMeetingWaitingMember(Member member, Meeting meeting) {
        return MeetingWaitingMember.builder()
                .member(member)
                .meeting(meeting)
                .joinRequestStatus(JoinRequestStatus.WAITING)
                .build();
    }

    private void validateDuplicatedJoin(Member member, Long meetingId) {
        List<MeetingMember> meetingMemberList = findMeetingMemberListByMeetingId(meetingId);
        for (MeetingMember meetingMember : meetingMemberList) {
            if (meetingMember.getMember() == member) {
                throwException(ErrorCode.DUPLICATED_JOIN_MEETING, "이미 가입한 모임입니다.");
            }
        }
    }

    private void validateOpenedMeeting(Boolean status) {
        if (!status) {
            throwException(ErrorCode.EXPIRED_MEETING, "모집이 마감된 모임입니다.");
        }
    }

    private void validateFullMeeting(Integer maxPeople, Long joinMemberCount) {
        if (isFull(maxPeople, joinMemberCount)) {
            throwException(ErrorCode.FULL_MEMBER_MEETING, "해당 모임에 인원이 다 찼습니다.");
        }
    }

    private boolean isFull(Integer maxPeople, Long joinMemberCount) {
        return maxPeople <= joinMemberCount;
    }

    private Long countMeetingMember(Long meetingId) {
        return meetingMemberRepository.countByMeetingId(meetingId);
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

    private Slice<MeetingRetrieveRespDto> meetingSliceMapToRetrieveRespDto(Slice<Meeting> meetingSlice, List<MeetingImage> meetingImageList, List<MeetingMember> meetingMemberList) {
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

    private Meeting validateOptionalMeeting(Optional<Meeting> optionalMeeting) {
        return optionalMeeting.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEETING, "존재하지 않은 모임입니다."));
    }

    private MeetingWaitingMember validateOptionalMeetingWaitingMember(Optional<MeetingWaitingMember> optionalMeetingWaitingMember) {
        return optionalMeetingWaitingMember.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER, "존재하지 회원 입니다."));
    }

    private Optional<Meeting> findOptionalMeetingByMeetingId(Long meetingId) {
        return meetingRepository.findById(meetingId);
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

    private MeetingMember saveMeetingMember(MeetingMember meetingMember) {
        return meetingMemberRepository.save(meetingMember);
    }

    private Meeting saveMeeting(Meeting meeting) {
        return meetingRepository.save(meeting);
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
                .location(meetingCreateReqDto.getLocation())
                .meetingDate(meetingCreateReqDto.getMeetingDate())
                .conditions(meetingCreateReqDto.getConditions())
                .maxPeople(meetingCreateReqDto.getMaxPeople())
                .sex(meetingCreateReqDto.getSex())
                .category(meetingCreateReqDto.getCategory())
                .meetingType(meetingCreateReqDto.getMeetingType())
                .period(meetingCreateReqDto.getPeriod())
                .title(meetingCreateReqDto.getTitle())
                .content(meetingCreateReqDto.getContent())
                .isOpened(true)
                .build();
    }
}
