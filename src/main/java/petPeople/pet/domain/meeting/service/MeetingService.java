package petPeople.pet.domain.meeting.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import petPeople.pet.controller.meeting.dto.resp.MeetingPostImageRetrieveRespDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingRetrieveRespDto;
import petPeople.pet.controller.member.dto.resp.MemberMeetingBookMarkRespDto;
import petPeople.pet.controller.post.model.MeetingParameter;
import petPeople.pet.domain.meeting.entity.*;
import petPeople.pet.domain.meeting.entity.vo.JoinRequestStatus;
import petPeople.pet.domain.meeting.repository.meeting.MeetingRepository;
import petPeople.pet.domain.meeting.repository.meeting_bookmark.MeetingBookmarkRepository;
import petPeople.pet.domain.meeting.repository.meeting_image.MeetingImageRepository;
import petPeople.pet.domain.meeting.repository.meeting_member.MeetingMemberRepository;
import petPeople.pet.domain.meeting.repository.meeting_post_Image.MeetingPostImageRepository;
import petPeople.pet.domain.meeting.repository.meeting_waiting_member.MeetingWaitingMemberRepository;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.member.repository.MemberRepository;
import petPeople.pet.domain.notification.entity.Notification;
import petPeople.pet.domain.notification.repository.NotificationRepository;
import petPeople.pet.exception.CustomException;
import petPeople.pet.exception.ErrorCode;
import petPeople.pet.util.RequestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingPostImageRepository meetingPostImageRepository;
    private final MeetingImageRepository meetingImageRepository;
    private final MeetingMemberRepository meetingMemberRepository;
    private final MeetingWaitingMemberRepository meetingWaitingMemberRepository;
    private final MeetingBookmarkRepository meetingBookmarkRepository;
    private final MemberRepository memberRepository;
    private final FirebaseAuth firebaseAuth;
    private final NotificationRepository notificationRepository;

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

    public MeetingRetrieveRespDto localRetrieveOne(Long meetingId, Optional<String> optionalHeader) {
        Meeting meeting = validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));

        List<MeetingImage> meetingImageList = findMeetingImageListByMeetingId(meetingId);
        List<MeetingMember> meetingMemberList = findMeetingMemberListByMeetingId(meetingId);

        if (isLogined(optionalHeader)) {
            Member member = validateOptionalMember(findOptionalMemberByUid(optionalHeader.get()));
            List<MeetingWaitingMember> meetingWaitingMembers = findMeetingWaitingMemberByMemberIdAndMeetingId(meetingId, member);

            Optional<MeetingWaitingMember> meetingWaitingMember =
                    meetingWaitingMembers.isEmpty()
                            ? Optional.empty()
                            : Optional.ofNullable(meetingWaitingMembers.get(meetingWaitingMembers.size() - 1));

            return new MeetingRetrieveRespDto(
                    meeting,
                    meetingImageList,
                    meetingMemberList,
                    isJoined(member, meetingMemberList),
                    isBookmarked(member, meetingId),
                    meetingWaitingMember
            );
        }

        return new MeetingRetrieveRespDto(meeting, meetingImageList, meetingMemberList, null, null);

    }

    public MeetingRetrieveRespDto retrieveOne(Long meetingId, Optional<String> optionalHeader) {
        Meeting meeting = validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));
        List<MeetingImage> meetingImageList = findMeetingImageListByMeetingId(meetingId);
        List<MeetingMember> meetingMemberList = findMeetingMemberListByMeetingId(meetingId);

        if (isLogined(optionalHeader)) {
            FirebaseToken firebaseToken = decodeToken(optionalHeader.get());
            Member member = validateOptionalMember(findOptionalMemberByUid(firebaseToken.getUid()));
            List<MeetingWaitingMember> meetingWaitingMembers = findMeetingWaitingMemberByMemberIdAndMeetingId(meetingId, member);

            Optional<MeetingWaitingMember> meetingWaitingMember =
                    meetingWaitingMembers.isEmpty()
                            ? Optional.empty()
                            : Optional.ofNullable(meetingWaitingMembers.get(0));

            return new MeetingRetrieveRespDto(
                    meeting,
                    meetingImageList,
                    meetingMemberList,
                    isJoined(member, meetingMemberList),
                    isBookmarked(member, meetingId), meetingWaitingMember);
        }
        return new MeetingRetrieveRespDto(meeting, meetingImageList, meetingMemberList, null, null);
    }

    public Slice<MeetingRetrieveRespDto> localRetrieveAll(Pageable pageable, Optional<String> optionalHeader, MeetingParameter meetingParameter) {

        Slice<Meeting> meetingSlice = findAllMeetingSlicingWithFetchJoinMember(pageable, meetingParameter);
        List<Long> meetingIds = getMeetingIdList(meetingSlice.getContent());

        if (isLogined(optionalHeader)) {
            Member member = validateOptionalMember(findOptionalMemberByUid(optionalHeader.get()));
            return meetingSliceMapToRetrieveRespDto(
                    member,
                    meetingSlice,
                    findMeetingImageByMeetingIds(meetingIds),
                    findMeetingMemberByMeetingIds(meetingIds)
            );
        }

        return meetingSliceMapToRetrieveNoLoginRespDto(
                    meetingSlice,
                    findMeetingImageByMeetingIds(meetingIds),
                    findMeetingMemberByMeetingIds(meetingIds)
            );
    }

    public Slice<MeetingRetrieveRespDto> retrieveAll(Pageable pageable, Optional<String> optionalHeader, MeetingParameter meetingParameter) {

        Slice<Meeting> meetingSlice = findAllMeetingSlicingWithFetchJoinMember(pageable, meetingParameter);
        List<Long> meetingIds = getMeetingIdList(meetingSlice.getContent());

        if (isLogined(optionalHeader)) {
            FirebaseToken firebaseToken = decodeToken(optionalHeader.get());
            Member member = validateOptionalMember(findOptionalMemberByUid(firebaseToken.getUid()));

            return meetingSliceMapToRetrieveRespDto(
                    member,
                    meetingSlice,
                    findMeetingImageByMeetingIds(meetingIds),
                    findMeetingMemberByMeetingIds(meetingIds)
            );
        } else {
            return meetingSliceMapToRetrieveNoLoginRespDto(
                    meetingSlice,
                    findMeetingImageByMeetingIds(meetingIds),
                    findMeetingMemberByMeetingIds(meetingIds)
            );
        }
    }

    public Slice<MeetingRetrieveRespDto> retrieveMemberMeeting(Member member, Pageable pageable) {
        Slice<Meeting> meetingSlice = findAllMeetingSlicingByMemberId(member, pageable);
        List<Long> meetingIds = getMeetingIdList(meetingSlice.getContent());
        List<MeetingImage> meetingImageByMeetingIdList = findMeetingImageByMeetingIds(meetingIds);
        List<MeetingMember> meetingMemberByMeetingIdList = findMeetingMemberByMeetingIds(meetingIds);

        return meetingSliceMapToRetrieveRespDto(
                member,
                meetingSlice,
                meetingImageByMeetingIdList,
                meetingMemberByMeetingIdList
        );
    }

    @Transactional
    public void joinRequest(Member member, Long meetingId) {
        Meeting meeting = validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));

        validateOpenedMeeting(meeting.getIsOpened());//모집 상태 검증
        validateOwnMeetingJoinRequest(member, meeting.getMember());//자신의 모임 가입 검증
        validateDuplicatedJoinRequest(member, meetingId);//중복 가입 요청 회원 검증
        validateDuplicatedJoin(member, meetingId);//중복 가입 회원 검증
        validateFullMeeting(meeting.getMaxPeople(), countMeetingMember(meetingId));//인원 검증

        saveMeetingWaitingMember(createMeetingWaitingMember(member, meeting));

        //미팅에 참여 요청 시 방장에게 알림
        saveNotification(createNotification(meeting.getMember(), member, meeting, JoinRequestStatus.WAITING));
    }


    @Transactional
    public void resign(Long meetingId, Member member) {
        Meeting findMeeting = validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));

        validateOwnMeetingResign(member, findMeeting.getMember());
        validateJoinedMember(isJoined(member, findMeetingMemberListByMeetingId(meetingId)));

        if (isFull(findMeeting.getMaxPeople(), countMeetingMember(meetingId))) {
            findMeeting.setIsOpened(true);
        }

        deleteMeetingMemberByMeetingIdAndMemberId(meetingId, member);
        deleteMeetingWaitingMemberByMeetingIdAndMemberId(meetingId, member.getId());
    }

    @Transactional
    public void expelMeetingMember(Long meetingId, Long memberId, Member member) {
        Meeting findMeeting = validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));
        Member findMember = validateOptionalMember(findMemberByMemberId(memberId));

        validateJoinedMember(isJoined(findMember, findMeetingMemberListByMeetingId(meetingId)));

        validateOwnMeetingResign(findMember, member);

        //회원이 개셜한 모임인지 확인하는 로직
        validateMemberAuthorization(findMeeting.getMember(), member);

        if (isFull(findMeeting.getMaxPeople(), countMeetingMember(meetingId))) {
            findMeeting.setIsOpened(true);
        }

        //meetingMember 삭제
        deleteMeetingMemberByMeetingIdAndMemberId(meetingId, findMember);

        deleteMeetingWaitingMemberByMeetingIdAndMemberId(meetingId, memberId);
    }

    @Transactional
    public void approve(Member member, Long meetingId, Long memberId) {
        Meeting findMeeting = validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));
        Long joinMemberCount = countMeetingMember(meetingId);
        Member joinRequestMember = findOptionalMemberById(memberId).orElseThrow(() -> {
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER, "회원을 찾을 수 없습니다.");
        });

        validateMemberAuthorization(member, findMeeting.getMember());//권한 검증
        validateFullMeeting(findMeeting.getMaxPeople(), joinMemberCount);//인원 검즘

        MeetingWaitingMember meetingWaitingMember = validateOptionalMeetingWaitingMember(findOptionalMeetingWaitingMemberByMeetingIdAndMemberId(meetingId, memberId));

        changeMeetingWaitingMemberStatus(meetingWaitingMember, JoinRequestStatus.APPROVED);

        saveMeetingMember(createMeetingMember(meetingWaitingMember.getMember(), findMeeting));
        saveNotification(createNotification(joinRequestMember, findMeeting, JoinRequestStatus.APPROVED));

        Long afterJoinMemberCount = countMeetingMember(meetingId);

        if (isFull(findMeeting.getMaxPeople(), afterJoinMemberCount)) {
            findMeeting.setIsOpened(false);
        }
    }


    @Transactional
    public void decline(Member member, Long meetingId, Long joinRequestMemberId) {
        Meeting findMeeting = validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));

        validateMemberAuthorization(member, findMeeting.getMember());

        MeetingWaitingMember meetingWaitingMember = validateOptionalMeetingWaitingMember(findOptionalMeetingWaitingMemberByMeetingIdAndMemberId(meetingId, joinRequestMemberId));
        changeMeetingWaitingMemberStatus(meetingWaitingMember, JoinRequestStatus.DECLINED);
        saveMeetingMember(createMeetingMember(meetingWaitingMember.getMember(), findMeeting));

        Member joinRequestMember = findOptionalMemberById(joinRequestMemberId).orElseThrow(() -> {
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER, "회원을 찾을 수 없습니다.");
        });

        saveNotification(createNotification(joinRequestMember, findMeeting, JoinRequestStatus.DECLINED));
    }

    @Transactional
    public void bookmark(Member member, Long meetingId) {
        if (isBookmarked(member, meetingId)) {
            throwException(ErrorCode.BOOKMARKED_POST, "이미 북마크를 눌렀습니다.");
        } else {
            savePostBookmark(createPostBookmark(member, validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId))));
        }
    }


    @Transactional
    public void deleteBookmark(Member member, Long meetingId) {
        if (isBookmarked(member, meetingId)) {
            meetingBookmarkRepository.deleteByMemberIdAndMeetingId(member.getId(), meetingId);
        } else {
            throwException(ErrorCode.NEVER_BOOKMARKED_MEETING, "북마크 하지 않은 모임입니다.");
        }
    }

    private void savePostBookmark(MeetingBookmark postBookmark) {
        meetingBookmarkRepository.save(postBookmark);
    }

    private MeetingBookmark createPostBookmark(Member member, Meeting meeting) {
        return MeetingBookmark.builder()
                .member(member)
                .meeting(meeting)
                .build();
    }

    private Optional<MeetingBookmark> findMeetingBookmarkByMemberIdAndPostId(Long memberId, Long meetingId) {
        return meetingBookmarkRepository.findByMemberIdAndMeetingId(memberId, meetingId);
    }

    public Slice<MemberMeetingBookMarkRespDto> retrieveMemberBookMarkMeeting(Member member, Pageable pageable) {

        return findPostBookmarkByMemberId(member, pageable)
                .map(meetingBookmark -> {
                    List<MeetingMember> meetingMemberList = findMeetingMemberListByMeetingId(meetingBookmark.getMeeting().getId());
                    List<MeetingImage> meetingImageList = findMeetingImageListByMeetingId(meetingBookmark.getMeeting().getId());
                    boolean isJoined = isJoined(member, meetingMemberList);
                    return new MemberMeetingBookMarkRespDto(meetingBookmark, meetingMemberList, meetingImageList, isJoined);
                });
    }

    private Slice<MeetingBookmark> findPostBookmarkByMemberId(Member member, Pageable pageable) {
        return meetingBookmarkRepository.findByMemberIdWithFetchJoinMeetingSlicing(member.getId(), pageable);
    }

    @Transactional
    public void cancelJoinRequest(Long meetingId, Member member) {
        validateOptionalMeetingWaitingMember(findOptionalMeetingWaitingMemberByMeetingIdAndMemberId(meetingId, member.getId()));

        deleteMeetingWaitingMemberByMeetingIdAndMemberId(meetingId, member.getId());
    }

    private Optional<Member> findOptionalMemberById(Long memberId) {
        return memberRepository.findById(memberId);
    }

    private void deleteMeetingWaitingMemberByMeetingIdAndMemberId(Long meetingId, Long memberId) {
        meetingWaitingMemberRepository.deleteByMeetingIdAndMemberId(meetingId, memberId);
    }

    private Optional<Member> findMemberByMemberId(Long memberId) {
        return findOptionalMemberById(memberId);
    }

    public List<MeetingPostImageRetrieveRespDto> retrieveAllImage(Long meetingId) {
        return meetingPostImageRepository.findAllByMeetingId(meetingId).stream()
                .map(meetingPostImage ->
                        new MeetingPostImageRetrieveRespDto(
                                meetingPostImage.getId(), meetingPostImage.getMeetingPost().getMeeting().getId(), meetingPostImage.getImgUrl())
                ).collect(Collectors.toList());
    }

    public Long countMemberMeeting(Member member) {
        return meetingRepository.countByMemberId(member.getId());
    }


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
            header = "Bearer " + header;
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

    private void validateOwnMeetingResign(Member member, Member targetMember) {
        if (targetMember == member) {
            throwException(ErrorCode.BAD_REQUEST, "자신의 모임은 탈퇴할 수 없습니다.");
        }
    }

    private void deleteMeetingMemberByMeetingIdAndMemberId(Long meetingId, Member member) {
        meetingMemberRepository.deleteByMeetingIdAndMemberId(meetingId, member.getId());
    }

    private void validateJoinedMember(boolean isJoined) {
        if (!isJoined) {
            throwException(ErrorCode.FORBIDDEN_MEMBER, "모임에 가입한 회원이 아닙니다.");
        }
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
        if (isNotSameMember(member, targetMember)) {
            throwException(ErrorCode.FORBIDDEN_MEMBER, "해당 모임에 권한이 없습니다.");
        }
    }

    private boolean isNotSameMember(Member member, Member postMember) {
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
                        isJoined(member, getMeetingMembersByMeeting(meetingMemberList, meeting)),
                        isBookmarked(member, meeting.getId()))
        );
    }

    private Slice<MeetingRetrieveRespDto> meetingSliceMapToRetrieveNoLoginRespDto(Slice<Meeting> meetingSlice, List<MeetingImage> meetingImageList, List<MeetingMember> meetingMemberList) {
        return meetingSlice.map(meeting ->
                new MeetingRetrieveRespDto(
                        meeting,
                        getMeetingImagesByMeeting(meetingImageList, meeting),
                        getMeetingMembersByMeeting(meetingMemberList, meeting),
                        null,
                        null)
        );
    }

    private boolean isBookmarked(Member member, Long meetingId) {
        return findMeetingBookmarkByMemberIdAndPostId(member.getId(), meetingId).isPresent();
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

    private Slice<Meeting> findAllMeetingSlicingWithFetchJoinMember(Pageable pageable, MeetingParameter meetingParameter) {
        return meetingRepository.findAllSlicingWithFetchJoinMember(pageable, meetingParameter);
    }

    private List<Long> getMeetingIdList(List<Meeting> content) {
        List<Long> ids = new ArrayList<>();
        for (Meeting meeting : content) {
            ids.add(meeting.getId());
        }
        return ids;
    }

    private List<MeetingWaitingMember> findMeetingWaitingMemberByMemberIdAndMeetingId(Long meetingId, Member member) {
        return meetingWaitingMemberRepository.findByMemberIdAndMeetingId(member.getId(), meetingId);
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

    private Meeting createMeeting(Member member, MeetingCreateReqDto meetingCreateReqDto) {
        return Meeting.builder()
                .member(member)
                .doName(meetingCreateReqDto.getDoName())
                .sigungu(meetingCreateReqDto.getSigungu())
                .location(meetingCreateReqDto.getLocation())
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

    private void saveNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    private Notification createNotification(Member member, Member joinRequestMember, Meeting meeting, JoinRequestStatus requestStatus) {
        return Notification.builder()
                .member(joinRequestMember)
                .meetingJoinRequestFlag(requestStatus)
                .ownerMember(member)
                .meeting(meeting)
                .build();
    }

    private Notification createNotification(Member joinRequestMember, Meeting meeting, JoinRequestStatus requestStatus) {
        return Notification.builder()
                .member(joinRequestMember)
                .meetingJoinRequestFlag(requestStatus)
                .ownerMember(joinRequestMember)
                .meeting(meeting)
                .build();
    }
}
