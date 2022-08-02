package petPeople.pet.domain.meeting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petPeople.pet.controller.meeting.dto.req.MeetingPostWriteReqDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingPostRetrieveRespDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingPostWriteRespDto;
import petPeople.pet.domain.meeting.entity.*;
import petPeople.pet.domain.meeting.repository.*;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.notification.entity.Notification;
import petPeople.pet.domain.notification.repository.NotificationRepository;
import petPeople.pet.domain.post.entity.Post;
import petPeople.pet.exception.CustomException;
import petPeople.pet.exception.ErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingPostService {

    private final MeetingPostRepository meetingPostRepository;
    private final MeetingMemberRepository meetingMemberRepository;
    private final MeetingPostImageRepository meetingPostImageRepository;
    private final MeetingCommentRepository meetingCommentRepository;
    private final MeetingRepository meetingRepository;
    private final MeetingPostLikeRepository meetingPostLikeRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public MeetingPostWriteRespDto write(Member member, MeetingPostWriteReqDto meetingPostWriteReqDto, Long meetingId) {
        validateJoinedMember(isJoined(member, findMeetingMemberListByMeetingId(meetingId)));

        Meeting findMeeting = validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));
        MeetingPost saveMeetingPost = saveMeetingPost(createMeetingPost(member, meetingPostWriteReqDto, findMeeting));
        List<MeetingPostImage> saveMeetingPostImageList = saveMeetingPostImageList(member, saveMeetingPost, meetingPostWriteReqDto.getImgUrlList());

        Notification findMeetingWritePost = createWritePostNotification(member, findMeeting, saveMeetingPost);
        saveMeetingPostNotification(member, saveMeetingPost, findMeetingWritePost);

        return new MeetingPostWriteRespDto(saveMeetingPost, saveMeetingPostImageList);
    }

    public MeetingPostRetrieveRespDto retrieveOne(Long meetingId, Long meetingPostId, Member member) {
        validateJoinedMember(isJoined(member, findMeetingMemberListByMeetingId(meetingId)));
        validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));

        MeetingPost findMeetingPost = validateOptionalMeetingPost(findOptionalMeetingPostByMeetingPostId(meetingPostId));

        List<MeetingPostImage> findMeetingPostImageList = findAllMeetingPostImageByMeetingPostId(meetingPostId);

        long likeCnt = countMeetingPostLikeByMeetingPostsId(meetingPostId);


        Optional<MeetingPostLike> optionalMeetingPostLike = findOptionalMeetingPostLikeByMemberIdAndMeetingPostId(member.getId(), meetingPostId);

        return new MeetingPostRetrieveRespDto(findMeetingPost, findMeetingPostImageList, likeCnt, optionalMeetingPostLike.isPresent());
    }

    public Slice<MeetingPostRetrieveRespDto> retrieveAll(Long meetingId, Pageable pageable, Member member) {
        validateJoinedMember(isJoined(member, findMeetingMemberListByMeetingId(meetingId)));
        validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));

        Slice<MeetingPost> meetingPostSlice = findAllMeetingPostSliceByMeetingId(meetingId, pageable);

        List<Long> meetingPostIds = getMeetingPostId(meetingPostSlice.getContent());

        List<MeetingPostImage> findMeetingPostImageList = findAllMeetingPostImageByMeetingPostIds(meetingPostIds);
        List<MeetingPostLike> findMeetingPostLikeList = finAllMeetingPostLikeByMeetingPostIds(meetingPostIds);

        return meetingPostSlice.map(meetingPost -> {
            List<MeetingPostImage> meetingPostImageList = getMeetingPostImagesByMeetingPost(findMeetingPostImageList, meetingPost);
            List<MeetingPostLike> meetingPostLikesByMeetingPost = getMeetingPostLikesByMeetingPost(findMeetingPostLikeList, meetingPost);

            boolean isLiked = isMemberLikedMeetingPost(member, meetingPostLikesByMeetingPost);
            boolean isOwner = false;

            if (meetingPost.getMember() == member) {
                isOwner = true;
            }

            return new MeetingPostRetrieveRespDto(meetingPost, meetingPostImageList, Long.valueOf(meetingPostLikesByMeetingPost.size()), isLiked, isOwner);
        });
    }

    public Slice<MeetingPostRetrieveRespDto> retrieveMemberMeetingPost(Member member, Pageable pageable) {
        Slice<MeetingPost> meetingPostSlice = findMeetingPostSliceByMemberId(member, pageable);

        List<Long> meetingPostIds = getMeetingPostId(meetingPostSlice.getContent());

        List<MeetingPostImage> findMeetingPostImageList = findAllMeetingPostImageByMeetingPostIds(meetingPostIds);
        List<MeetingPostLike> findMeetingPostLikeList = finAllMeetingPostLikeByMeetingPostIds(meetingPostIds);

        return meetingPostSlice.map(meetingPost -> {
            List<MeetingPostImage> meetingPostImageList = getMeetingPostImagesByMeetingPost(findMeetingPostImageList, meetingPost);
            List<MeetingPostLike> meetingPostLikesByMeetingPost = getMeetingPostLikesByMeetingPost(findMeetingPostLikeList, meetingPost);

            boolean isLiked = isMemberLikedMeetingPost(member, meetingPostLikesByMeetingPost);

            return new MeetingPostRetrieveRespDto(meetingPost, meetingPostImageList, Long.valueOf(meetingPostLikesByMeetingPost.size()), isLiked);
        });
    }

    @Transactional
    public MeetingPostWriteRespDto edit(Long meetingId, Long meetingPostId, MeetingPostWriteReqDto meetingPostWriteReqDto, Member member) {
        validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));
        MeetingPost findMeetingPost = validateOptionalMeetingPost(findOptionalMeetingPostByMeetingPostId(meetingPostId));

        validateMemberAuthorization(member, findMeetingPost.getMember());

        findMeetingPost.setTitle(meetingPostWriteReqDto.getTitle());
        findMeetingPost.setContent(meetingPostWriteReqDto.getContent());

        deleteMeetingPostImageByMeetingPostId(meetingPostId);

        List<MeetingPostImage> saveMeetingPostImageList = saveMeetingPostImageList(member, findMeetingPost, meetingPostWriteReqDto.getImgUrlList());

        return new MeetingPostWriteRespDto(findMeetingPost, saveMeetingPostImageList);
    }

    @Transactional
    public long like(Long meetingId, Long meetingPostId, Member member) {
        Meeting findMeeting = validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));
        MeetingPost findMeetingPost = validateOptionalMeetingPost(findOptionalMeetingPostByMeetingPostId(meetingPostId));
        Optional<MeetingPostLike> optionalMeetingPostLike = findOptionalMeetingPostLikeByMemberIdAndMeetingPostId(member.getId(), meetingPostId);

        if (optionalMeetingPostLike.isPresent()) {
            deleteMeetingPostLikeByMemberIdAndMeetingPostId(member.getId(), meetingPostId);
        } else {
            saveMeetingPostLike(createMeetingPostLike(member, findMeetingPost));

            Notification meetingLikePostNotification = createLikePostNotification(member, findMeeting, findMeetingPost);
            saveMeetingPostLikeNotification(member, findMeetingPost, meetingLikePostNotification);
        }

        return countMeetingPostLikeByMeetingPostsId(meetingPostId);
    }


    @Transactional
    public void delete(Long meetingId, Long meetingPostId, Member member) {
        validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));
        MeetingPost findMeetingPost = validateOptionalMeetingPost(findOptionalMeetingPostByMeetingPostId(meetingPostId));

        validateMemberAuthorization(member, findMeetingPost.getMember());

        deleteNotificationByMeetingPostIdAndMemberId(meetingPostId, member);

        deleteMeetingCommentByMeetingPostId(meetingPostId);

        deleteMeetingPostLikeByMeetingPostId(meetingPostId);
        deleteMeetingPostImageByMeetingPostId(meetingPostId);
        deleteMeetingPostByMeetingPostId(meetingPostId);
    }

    private void deleteMeetingCommentByMeetingPostId(Long meetingPostId) {
        meetingCommentRepository.deleteMeetingCommentByMeetingPostId(meetingPostId);
    }

    private void deleteNotificationByMeetingPostIdAndMemberId(Long meetingPostId, Member member) {
        notificationRepository.deleteNotificationByMemberIdAndMeetingPostId(meetingPostId, member.getId());
    }

    private void saveMeetingPostNotification(Member member, MeetingPost findMeetingPost, Notification notification) {
        if (!isExistMemberLikePostNotification(findMeetingPost.getId(), member)) {
            saveNotification(notification);
        }
    }

    private void saveMeetingPostLikeNotification(Member member, MeetingPost findMeetingPost, Notification notification) {
        if (isNotSameMember(member, findMeetingPost.getMember())) {
            if (!isExistMemberLikePostNotification(findMeetingPost.getId(), member)) {
                saveNotification(notification);
            }
        }
    }

    private Notification createWritePostNotification(Member member, Meeting findMeeting, MeetingPost findMeetingPost) {
        return Notification.builder()
                .meetingWritePost(findMeetingPost)
                .ownerMember(findMeetingPost.getMember())
                .meeting(findMeeting)
                .member(member)
                .build();
    }

    private Notification createLikePostNotification(Member member, Meeting findMeeting, MeetingPost findMeetingPost) {
        return Notification.builder()
                .meetingPost(findMeetingPost)
                .ownerMember(findMeetingPost.getMember())
                .meeting(findMeeting)
                .member(member)
                .build();
    }

    private boolean isNotSameMember(Member member, Member meetingPostMember) {
        return member != meetingPostMember;
    }

    private boolean isExistMemberLikePostNotification(Long postId, Member member) {
        return notificationRepository.findByMemberIdAndPostId(member.getId(), postId).isPresent();
    }

    private void saveNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    private Slice<MeetingPost> findMeetingPostSliceByMemberId(Member member, Pageable pageable) {
        return meetingPostRepository.findAllSliceByMemberId(pageable, member.getId());
    }

    private void deleteMeetingPostByMeetingPostId(Long meetingPostId) {
        meetingPostRepository.deleteById(meetingPostId);
    }

    private boolean isMemberLikedMeetingPost(Member member, List<MeetingPostLike> meetingPostLikesByMeetingPost) {
        boolean isLiked = false;

        for (MeetingPostLike meetingPostLike : meetingPostLikesByMeetingPost) {
            if (meetingPostLike.getMember() == member) {
                isLiked = true;
                break;
            }
        }
        return isLiked;
    }

    private List<MeetingPostLike> finAllMeetingPostLikeByMeetingPostIds(List<Long> meetingPostIds) {
        return meetingPostLikeRepository.findByMeetingPostIds(meetingPostIds);
    }

    private void deleteMeetingPostLikeByMeetingPostId(Long meetingPostId) {
        meetingPostLikeRepository.deleteByMeetingPostId(meetingPostId);
    }

    private long countMeetingPostLikeByMeetingPostsId(Long meetingPostId) {
        return meetingPostLikeRepository.countByMeetingPostsId(meetingPostId);
    }

    private MeetingPostLike createMeetingPostLike(Member member, MeetingPost meetingPost) {
        MeetingPostLike meetingPostLike = MeetingPostLike.builder()
                .member(member)
                .meetingPost(meetingPost)
                .build();
        return meetingPostLike;
    }

    private MeetingPostLike saveMeetingPostLike(MeetingPostLike meetingPostLike) {
        return meetingPostLikeRepository.save(meetingPostLike);
    }

    private void deleteMeetingPostLikeByMemberIdAndMeetingPostId(Long memberId, Long meetingPostId) {
        meetingPostLikeRepository.deleteByMemberIdAndMeetingPostId(memberId, meetingPostId);
    }

    private Optional<MeetingPostLike> findOptionalMeetingPostLikeByMemberIdAndMeetingPostId(Long memberId, Long meetingPostId) {
        return meetingPostLikeRepository.findByMemberIdAndMeetingPostId(memberId, meetingPostId);
    }


    private void validateMemberAuthorization(Member member, Member targetMember) {
        if (isaNotSameMember(member, targetMember)) {
            throwException(ErrorCode.FORBIDDEN_MEMBER, "해당 게시글에 권한이 없습니다.");
        }
    }

    private boolean isaNotSameMember(Member member, Member postMember) {
        return member != postMember;
    }

    private void deleteMeetingPostImageByMeetingPostId(Long meetingPostId) {
        meetingPostImageRepository.deleteByMeetingPostId(meetingPostId);
    }

    private List<MeetingPostImage> getMeetingPostImagesByMeetingPost(List<MeetingPostImage> findMeetingPostImageList, MeetingPost meetingPost) {
        List<MeetingPostImage> meetingPostImageList = new ArrayList<>();
        for (MeetingPostImage meetingPostImage : findMeetingPostImageList) {
            if (meetingPostImage.getMeetingPost() == meetingPost) {
                meetingPostImageList.add(meetingPostImage);
            }
        }
        return meetingPostImageList;
    }

    private List<MeetingPostLike> getMeetingPostLikesByMeetingPost(List<MeetingPostLike> findMeetingPostLikeList, MeetingPost meetingPost) {
        List<MeetingPostLike> meetingPostLikeList = new ArrayList<>();
        for (MeetingPostLike meetingPostLike : findMeetingPostLikeList) {
            if (meetingPostLike.getMeetingPost() == meetingPost) {
                meetingPostLikeList.add(meetingPostLike);
            }
        }
        return meetingPostLikeList;
    }

    private List<MeetingPostImage> findAllMeetingPostImageByMeetingPostIds(List<Long> meetingPostIds) {
        return meetingPostImageRepository.findAllByMeetingPostIds(meetingPostIds);
    }

    private Slice<MeetingPost> findAllMeetingPostSliceByMeetingId(Long meetingId, Pageable pageable) {
        return meetingPostRepository.findAllSliceByMeetingId(meetingId, pageable);
    }

    private List<Long> getMeetingPostId(List<MeetingPost> content) {
        List<Long> ids = new ArrayList<>();
        for (MeetingPost meetingPost : content) {
            ids.add(meetingPost.getId());
        }
        return ids;
    }

    private List<MeetingPostImage> findAllMeetingPostImageByMeetingPostId(Long meetingPostId) {
        return meetingPostImageRepository.findAllMeetingPostImageByMeetingPostId(meetingPostId);
    }

    private Optional<MeetingPost> findOptionalMeetingPostByMeetingPostId(Long meetingPostId) {
        return meetingPostRepository.findById(meetingPostId);
    }

    private void validateJoinedMember(boolean isJoined) {
        if (!isJoined) {
            throwException(ErrorCode.FORBIDDEN_MEMBER, "모임에 가입한 회원이 아닙니다.");
        }
    }

    private boolean isJoined(Member member, List<MeetingMember> meetingMemberList) {
        boolean isJoined = false;
        for (MeetingMember meetingMember : meetingMemberList) {
            if (meetingMember.getMember() == member) {
                isJoined = true;
                break;
            }
        }
        return isJoined;
    }

    private void throwException(ErrorCode errorCode, String message) {
        throw new CustomException(errorCode, message);
    }

    private List<MeetingMember> findMeetingMemberListByMeetingId(Long meetingId) {
        return meetingMemberRepository.findByMeetingId(meetingId);
    }

    private List<MeetingPostImage> saveMeetingPostImageList(Member member, MeetingPost meetingPost, List<String> imgUrlList) {
        List<MeetingPostImage> meetingPostImageList = new ArrayList<>();
        for (String url : imgUrlList) {
            MeetingPostImage meetingPostImage = saveMeetingPostImage(createMeetingPostImage(member, meetingPost, url));
            meetingPostImageList.add(meetingPostImage);
        }
        return meetingPostImageList;
    }

    private MeetingPostImage saveMeetingPostImage(MeetingPostImage meetingPostImage) {
        return meetingPostImageRepository.save(meetingPostImage);
    }

    private MeetingPost saveMeetingPost(MeetingPost meetingPost) {
        return meetingPostRepository.save(meetingPost);
    }

    private MeetingPostImage createMeetingPostImage(Member member, MeetingPost meetingPost, String url) {
        return MeetingPostImage.builder()
                .member(member)
                .meetingPost(meetingPost)
                .imgUrl(url)
                .build();
    }

    private MeetingPost createMeetingPost(Member member, MeetingPostWriteReqDto meetingPostWriteReqDto, Meeting meeting) {
        return MeetingPost.builder()
                .meeting(meeting)
                .member(member)
                .title(meetingPostWriteReqDto.getTitle())
                .content(meetingPostWriteReqDto.getContent())
                .build();
    }

    private Optional<Meeting> findOptionalMeetingByMeetingId(Long meetingId) {
        return meetingRepository.findById(meetingId);
    }

    private Meeting validateOptionalMeeting(Optional<Meeting> optionalMeeting) {
        return optionalMeeting.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEETING, "존재하지 않은 모임입니다."));
    }

    private MeetingPost validateOptionalMeetingPost(Optional<MeetingPost> optionalMeetingPost) {
        return optionalMeetingPost.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEETING, "존재하지 않은 모임입니다."));
    }
}
