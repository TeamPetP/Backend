package petPeople.pet.domain.meeting.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
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
import petPeople.pet.domain.member.repository.MemberRepository;
import petPeople.pet.exception.CustomException;
import petPeople.pet.exception.ErrorCode;
import petPeople.pet.util.RequestUtil;

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
    private final MemberRepository memberRepository;
    private final FirebaseAuth firebaseAuth;

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
        Long joinedMemberCount = countMeetingMember(meetingId);

        validateMemberAuthorization(member, findMeeting.getMember());//회원 권한 검증
        validateEditMeetingMaxPeopleNumber(joinedMemberCount, meetingEditReqDto.getMaxPeople());//최대 인원을 현재 가입한 인원보다 낮게 수정할 경우
        validateOpenStatusChange(meetingEditReqDto, findMeeting, joinedMemberCount);//모집중이라 바꾸고 현재 꽉차 있으면서 바꾸고자하는 인원이 max people 보다 이하일 경우

        editMeeting(meetingEditReqDto, findMeeting);//모임 수정

        deleteMeetingImageByMeetingId(meetingId);

        List<MeetingImage> meetingImageList = new ArrayList<>();
        for (String url : meetingEditReqDto.getImgUrlList()) {
            meetingImageList.add(saveMeetingImage(createMeetingImage(findMeeting, url)));
        }

        return new MeetingEditRespDto(findMeeting, meetingImageList);
    }

    private void validateOpenStatusChange(MeetingEditReqDto meetingEditReqDto, Meeting findMeeting, Long joinedMemberCount) {
        if (meetingEditReqDto.getIsOpened() == true && isFull(findMeeting.getMaxPeople(), joinedMemberCount) && (meetingEditReqDto.getMaxPeople() <= findMeeting.getMaxPeople())) {
            throwException(ErrorCode.FULL_MEMBER_MEETING, "모임 인원이 다 꽉찬 상태를 모집중으로 바꿀 수 없습니다.(모집인원을 늘려주세요)");
        }
    }

    private void validateEditMeetingMaxPeopleNumber(Long joinedMemberCount, Integer editMaxPeople) {
        if (editMaxPeople < joinedMemberCount) {
            throwException(ErrorCode.BAD_REQUEST, "현재 가입한 회원의 수 보다 낮게 수정 할 수 없습니다.");
        }
    }

    // TODO: 2022-05-23 모임에 max 회원 도달 경우 자동으로 isOpened 바꿀지(가입 후 max에 찰 경우 자동으로 마감 처리)
    @Transactional
    public void joinRequest(Member member, Long meetingId) {
        Meeting meeting = validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));

        validateOpenedMeeting(meeting.getIsOpened());//모집 상태 검즘
        validateOwnMeetingJoinRequest(member, meeting.getMember());//자신의 모임 가입 검즘
        validateDuplicatedJoinRequest(member, meetingId);//중복 가입 요청 회원 검즘
        validateDuplicatedJoin(member, meetingId);//중복 가입 회원 검즘
        validateFullMeeting(meeting.getMaxPeople(), countMeetingMember(meetingId));//인원 검즘

        saveMeetingWaitingMember(createMeetingWaitingMember(member, meeting));

    }

    public MeetingRetrieveRespDto localRetrieveOne(Long meetingId, Optional<String> optionalHeader) {

        if (isLogined(optionalHeader)) {
            Member member = validateOptionalMember(findOptionalMemberByUid(optionalHeader.get()));

            Meeting meeting = validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));
            List<MeetingImage> meetingImageList = findMeetingImageListByMeetingId(meetingId);
            List<MeetingMember> meetingMemberList = findMeetingMemberListByMeetingId(meetingId);

            return new MeetingRetrieveRespDto(meeting, meetingImageList, meetingMemberList, isJoined(member, meetingMemberList));
        } else {
            Meeting meeting = validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));
            List<MeetingImage> meetingImageList = findMeetingImageListByMeetingId(meetingId);
            List<MeetingMember> meetingMemberList = findMeetingMemberListByMeetingId(meetingId);

            return new MeetingRetrieveRespDto(meeting, meetingImageList, meetingMemberList, null);
        }
    }

    public MeetingRetrieveRespDto retrieveOne(Long meetingId, Optional<String> optionalHeader) {

        if (isLogined(optionalHeader)) {
            FirebaseToken firebaseToken = decodeToken(optionalHeader.get());
            Member member = validateOptionalMember(findOptionalMemberByUid(firebaseToken.getUid()));

            Meeting meeting = validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));
            List<MeetingImage> meetingImageList = findMeetingImageListByMeetingId(meetingId);
            List<MeetingMember> meetingMemberList = findMeetingMemberListByMeetingId(meetingId);

            return new MeetingRetrieveRespDto(meeting, meetingImageList, meetingMemberList, isJoined(member, meetingMemberList));
        } else {
            Meeting meeting = validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));
            List<MeetingImage> meetingImageList = findMeetingImageListByMeetingId(meetingId);
            List<MeetingMember> meetingMemberList = findMeetingMemberListByMeetingId(meetingId);

            return new MeetingRetrieveRespDto(meeting, meetingImageList, meetingMemberList, null);
        }
    }

    public Slice<MeetingRetrieveRespDto> localRetrieveAll(Pageable pageable, Optional<String> optionalHeader) {

        if (isLogined(optionalHeader)) {
            Member member = validateOptionalMember(findOptionalMemberByUid(optionalHeader.get()));

            Slice<Meeting> meetingSlice = findAllMeetingSlicingWithFetchJoinMember(pageable);
            List<Long> meetingIds = getMeetingId(meetingSlice.getContent());
            return meetingSliceMapToRetrieveRespDto(
                    member,
                    meetingSlice,
                    findMeetingImageByMeetingIds(meetingIds),
                    findMeetingMemberByMeetingIds(meetingIds)
            );
        } else {
            Slice<Meeting> meetingSlice = findAllMeetingSlicingWithFetchJoinMember(pageable);
            List<Long> meetingIds = getMeetingId(meetingSlice.getContent());
            return meetingSliceMapToRetrieveNoLoginRespDto(
                    meetingSlice,
                    findMeetingImageByMeetingIds(meetingIds),
                    findMeetingMemberByMeetingIds(meetingIds)
            );
        }
    }

    public Slice<MeetingRetrieveRespDto> retrieveAll(Pageable pageable, Optional<String> optionalHeader) {

        if (isLogined(optionalHeader)) {
            FirebaseToken firebaseToken = decodeToken(optionalHeader.get());
            Member member = validateOptionalMember(findOptionalMemberByUid(firebaseToken.getUid()));

            Slice<Meeting> meetingSlice = findAllMeetingSlicingWithFetchJoinMember(pageable);
            List<Long> meetingIds = getMeetingId(meetingSlice.getContent());
            return meetingSliceMapToRetrieveRespDto(
                    member,
                    meetingSlice,
                    findMeetingImageByMeetingIds(meetingIds),
                    findMeetingMemberByMeetingIds(meetingIds)
            );
        } else {
            Slice<Meeting> meetingSlice = findAllMeetingSlicingWithFetchJoinMember(pageable);
            List<Long> meetingIds = getMeetingId(meetingSlice.getContent());
            return meetingSliceMapToRetrieveNoLoginRespDto(
                    meetingSlice,
                    findMeetingImageByMeetingIds(meetingIds),
                    findMeetingMemberByMeetingIds(meetingIds)
            );
        }
    }

    public Slice<MeetingRetrieveRespDto> retrieveMemberMeeting(Member member, Pageable pageable) {
        Slice<Meeting> meetingSlice = findAllMeetingSlicingByMemberId(member, pageable);
        List<Long> meetingIds = getMeetingId(meetingSlice.getContent());
        return meetingSliceMapToRetrieveRespDto(
                member,
                meetingSlice,
                findMeetingImageByMeetingIds(meetingIds),
                findMeetingMemberByMeetingIds(meetingIds)
        );
    }

    @Transactional
    public void approve(Member member, Long meetingId, Long memberId) {
        Meeting findMeeting = validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));
        Long joinMemberCount = countMeetingMember(meetingId);

        validateMemberAuthorization(member, findMeeting.getMember());//권한 검증
        validateFullMeeting(findMeeting.getMaxPeople(), joinMemberCount);//인원 검즘

        MeetingWaitingMember meetingWaitingMember = validateOptionalMeetingWaitingMember(findOptionalMeetingWaitingMemberByMeetingIdAndMemberId(meetingId, memberId));
        changeMeetingWaitingMemberStatus(meetingWaitingMember, JoinRequestStatus.APPROVED);

        saveMeetingMember(createMeetingMember(meetingWaitingMember.getMember(), findMeeting));

        if (isOccupiedMeeting(findMeeting.getMaxPeople(), joinMemberCount)) {
            findMeeting.setIsOpened(false);
        }
    }

    @Transactional
    public void decline(Member member, Long meetingId, Long memberId) {
        Meeting findMeeting = validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));

        validateMemberAuthorization(member, findMeeting.getMember());

        MeetingWaitingMember meetingWaitingMember = validateOptionalMeetingWaitingMember(findOptionalMeetingWaitingMemberByMeetingIdAndMemberId(meetingId, memberId));
        changeMeetingWaitingMemberStatus(meetingWaitingMember, JoinRequestStatus.DECLINED);
    }




//    public void retrieveAllImage(Long meetingId, String header) {
//
//    }

    private Optional<Member> findOptionalMemberByUid(String uid) {
        return memberRepository.findByUid(uid);
    }

    private Member validateOptionalMember(Optional<Member> optionalMember) {
        return optionalMember
                .orElseThrow(() ->
                        new CustomException(ErrorCode.NOT_FOUND_MEMBER, "존재하지 않은 회원입니다."));
    }
    private void validateOwnMeetingJoinRequest(Member member, Member targetMember) {
    if (member == targetMember) {
        throwException(ErrorCode.DUPLICATED_JOIN_MEETING, "이미 가입한 모임입니다.");
    }
}

    public FirebaseToken decodeToken(String header) {
        try {
            String token = RequestUtil.getAuthorizationToken(header);
            return firebaseAuth.verifyIdToken(token);
        } catch (IllegalArgumentException | FirebaseAuthException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "{\"code\":\"INVALID_TOKEN\", \"message\":\"" + e.getMessage() + "\"}");
        }
    }

    private boolean isJoined(Member member, List<MeetingMember> meetingMemberList) {
        boolean isJoined = false;
        for (MeetingMember meetingMember : meetingMemberList) {
            if (meetingMember.getMember().getId() == member.getId()) {
                isJoined = true;
                break;
            }
        }
        return isJoined;
    }

    private boolean isLogined(Optional<String> optionalHeader) {
        return optionalHeader.isPresent();
    }

    private void changeMeetingWaitingMemberStatus(MeetingWaitingMember meetingWaitingMember, JoinRequestStatus joinRequestStatus) {
        meetingWaitingMember.setJoinRequestStatus(joinRequestStatus);
    }

    private boolean isOccupiedMeeting(Integer maxPeople, Long joinMemberCount) {
        return joinMemberCount + 1 >= maxPeople;
    }

    private Optional<MeetingWaitingMember> findOptionalMeetingWaitingMemberByMeetingIdAndMemberId(Long meetingId, Long memberId) {
        return meetingWaitingMemberRepository.findByMeetingIdAndMemberIdFetchJoinMember(meetingId, memberId);
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

    private void validateDuplicatedJoinRequest(Member member, Long meetingId) {
        List<MeetingWaitingMember> meetingWaitingMembers = findMeetingWaitingMemberByMeetingId(meetingId);
        for (MeetingWaitingMember meetingWaitingMember : meetingWaitingMembers) {
            if (meetingWaitingMember.getMember() == member) {
                throwException(ErrorCode.DUPLICATED_JOIN_MEETING, "이미 가입한 모임입니다.");
            }
        }
    }

    private void validateDuplicatedJoin(Member member, Long meetingId) {
        List<MeetingMember> meetingMemberList = findMeetingMemberListByMeetingId(meetingId);
        for (MeetingMember meetingMember : meetingMemberList) {
            if (meetingMember.getMember() == member) {
                throwException(ErrorCode.DUPLICATED_JOIN_MEETING, "이미 가입한 모임입니다.");
            }
        }
    }

    private List<MeetingWaitingMember> findMeetingWaitingMemberByMeetingId(Long meetingId) {
        return meetingWaitingMemberRepository.findAllByMeetingIdFetchJoinMember(meetingId);
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

    private Slice<MeetingRetrieveRespDto> meetingSliceMapToRetrieveRespDto(Member member, Slice<Meeting> meetingSlice, List<MeetingImage> meetingImageList, List<MeetingMember> meetingMemberList) {
        return meetingSlice.map(meeting ->
            new MeetingRetrieveRespDto(
                    meeting,
                    getMeetingImagesByMeeting(meetingImageList, meeting),
                    getMeetingMembersByMeeting(meetingMemberList, meeting),
                    isJoined(member, getMeetingMembersByMeeting(meetingMemberList, meeting)))
        );
    }

    private Slice<MeetingRetrieveRespDto> meetingSliceMapToRetrieveNoLoginRespDto(Slice<Meeting> meetingSlice, List<MeetingImage> meetingImageList, List<MeetingMember> meetingMemberList) {
        return meetingSlice.map(meeting ->
                new MeetingRetrieveRespDto(
                        meeting,
                        getMeetingImagesByMeeting(meetingImageList, meeting),
                        getMeetingMembersByMeeting(meetingMemberList, meeting),
                        null)
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
        return optionalMeetingWaitingMember.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER, "모임 가입을 신청한 회원이 아닙니다."));
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
