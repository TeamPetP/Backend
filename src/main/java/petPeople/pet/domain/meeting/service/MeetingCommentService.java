package petPeople.pet.domain.meeting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petPeople.pet.controller.meeting.dto.req.MeetingCommentWriteReqDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingCommentRetrieveRespDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingCommentWriteRespDto;
import petPeople.pet.domain.comment.entity.Comment;
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
public class MeetingCommentService {

    private final MeetingRepository meetingRepository;
    private final MeetingPostRepository meetingPostRepository;
    private final MeetingMemberRepository meetingMemberRepository;
    private final MeetingCommentRepository meetingCommentRepository;
    private final MeetingCommentLikeRepository meetingCommentLikeRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public MeetingCommentWriteRespDto write(Long meetingId, Long meetingPostId, MeetingCommentWriteReqDto meetingCommentWriteReqDto, Member member) {

        validateJoinedMember(isJoined(member, findMeetingMemberListByMeetingId(meetingId)));

        validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));
        MeetingPost findMeetingPost = validateOptionalMeetingPost(findOptionalMeetingPostByMeetingPostId(meetingPostId));

        MeetingComment saveMeetingComment = saveMeetingComment(meetingCommentWriteReqDto, member, findMeetingPost);

        saveNotification(member, findMeetingPost, saveMeetingComment);

        return new MeetingCommentWriteRespDto(saveMeetingComment.getId(), meetingPostId, meetingId, meetingCommentWriteReqDto.getContent());
    }

    public Slice<MeetingCommentRetrieveRespDto> retrieveComments(Long meetingId, Long meetingPostId, Member member, Pageable pageable) {

        validateJoinedMember(isJoined(member, findMeetingMemberListByMeetingId(meetingId)));

        validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));
        validateOptionalMeetingPost(findOptionalMeetingPostByMeetingPostId(meetingPostId));

        Slice<MeetingComment> meetingCommentSlice = findMeetingCommentByMeetingPostId(meetingPostId, pageable);

        List<Long> meetingCommentIds = getMeetingCommentIds(meetingCommentSlice.getContent());

        List<MeetingCommentLike> findMeetingCommentLikeList = findMeetingCommentLikeByMeetingCommentIds(meetingCommentIds);

        return meetingCommentSlice
                .map(meetingComment -> {
                    List<MeetingCommentLike> meetingCommentLikeList = getMeetingCommentLikeByMeetingComment(findMeetingCommentLikeList, meetingComment);

                    return new MeetingCommentRetrieveRespDto(meetingComment.getId(), meetingPostId, meetingId, meetingComment.getContent(), isMemberLikedMeetingComment(member, meetingCommentLikeList), Long.valueOf(meetingCommentLikeList.size()));
                });
    }

    private List<Long> getMeetingCommentIds(List<MeetingComment> content) {
        List<Long> meetingCommentIds = new ArrayList<>();

        for (MeetingComment meetingComment : content) {
            meetingCommentIds.add(meetingComment.getId());
        }
        return meetingCommentIds;
    }

    @Transactional
    public Long likeComment(Long meetingId, Long meetingPostId, Long meetingCommentId, Member member) {
        validateJoinedMember(isJoined(member, findMeetingMemberListByMeetingId(meetingId)));

        validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));
        validateOptionalMeetingPost(findOptionalMeetingPostByMeetingPostId(meetingPostId));
        MeetingComment meetingComment = validateOptionalMeetingComment(findMeetingCommentByMeetingCommentId(meetingCommentId));

        Optional<MeetingCommentLike> optionalMeetingCommentLike = findMeetingCommentLikeByMeetingCommentIdAndMemberId(meetingCommentId, member.getId());

        if (optionalMeetingCommentLike.isPresent()) {
            deleteMeetingCommentLikeByMeetingCommentIdAndMemberId(meetingCommentId, member.getId());
        } else {
            saveMeetingCommentLike(createMeetingCommentLike(member, meetingComment));
        }

        return countMeetingCommentLikeByMeetingCommentId(meetingCommentId);

    }

    private boolean isMemberLikedMeetingComment(Member member, List<MeetingCommentLike> meetingCommentLikes) {
        boolean isLiked = false;
        for (MeetingCommentLike meetingCommentLike : meetingCommentLikes) {
            if (meetingCommentLike.getMember() == member) {
                isLiked = true;
                break;
            }
        }
        return isLiked;
    }

    private List<MeetingCommentLike> getMeetingCommentLikeByMeetingComment(List<MeetingCommentLike> meetingCommentLikeList, MeetingComment meetingComment) {
        List<MeetingCommentLike> meetingCommentLikes = new ArrayList<>();

        for (MeetingCommentLike meetingCommentLike : meetingCommentLikeList) {
            if (meetingCommentLike.getMeetingComment() == meetingComment) {
                meetingCommentLikes.add(meetingCommentLike);
            }
        }
        return meetingCommentLikes;
    }

    private void saveNotification(Member member, MeetingPost findMeetingPost, MeetingComment saveMeetingComment) {
        if (isNotSameMember(member, findMeetingPost.getMember())) {
            saveRepositoryNotification(createNotification(member, findMeetingPost, saveMeetingComment));
        }
    }

    private boolean isNotSameMember(Member member, Member findMember) {
        return findMember != member;
    }

    private Notification createNotification(Member member, MeetingPost findMeetingPost, MeetingComment saveMeetingComment) {
        return Notification.builder()
                .meetingComment(saveMeetingComment)
                .meetingPost(findMeetingPost)
                .ownerMember(findMeetingPost.getMember()) //게시글 작성자
                .member(member) //게시글에 댓글을 단 사용자
                .build();
    }

    private void saveRepositoryNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    private Long countMeetingCommentLikeByMeetingCommentId(Long meetingCommentId) {
        return meetingCommentLikeRepository.countByMeetingCommentId(meetingCommentId);
    }

    private void deleteMeetingCommentLikeByMeetingCommentIdAndMemberId(Long meetingCommentId, Long memberId) {
        meetingCommentLikeRepository.deleteByMeetingCommentIdAndMemberId(meetingCommentId, memberId);
    }

    private MeetingCommentLike saveMeetingCommentLike(MeetingCommentLike meetingCommentLike) {
        return meetingCommentLikeRepository.save(meetingCommentLike);
    }

    private MeetingCommentLike createMeetingCommentLike(Member member, MeetingComment meetingComment) {
        return MeetingCommentLike.builder()
                .member(member)
                .meetingComment(meetingComment)
                .build();
    }

    private Optional<MeetingCommentLike> findMeetingCommentLikeByMeetingCommentIdAndMemberId(Long meetingCommentId, Long memberId) {
        return meetingCommentLikeRepository.findByMeetingPostIdAndMemberId(meetingCommentId, memberId);
    }

    private MeetingComment validateOptionalMeetingComment(Optional<MeetingComment> optionalMeetingComment) {
        return optionalMeetingComment.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEETING, "존재하지 않은 댓글입니다."));
    }

    private Optional<MeetingComment> findMeetingCommentByMeetingCommentId(Long meetingCommentId) {
        return meetingCommentRepository.findById(meetingCommentId);
    }

    private List<MeetingCommentLike> findMeetingCommentLikeByMeetingCommentIds(List<Long> meetingCommentIds) {
        return meetingCommentLikeRepository.findByMeetingCommentIds(meetingCommentIds);
    }

    private Slice<MeetingComment> findMeetingCommentByMeetingPostId(Long meetingPostId, Pageable pageable) {
        return meetingCommentRepository.findByMeetingPostId(pageable, meetingPostId);
    }

    private MeetingComment saveMeetingComment(MeetingCommentWriteReqDto meetingCommentWriteReqDto, Member member, MeetingPost findMeetingPost) {
        return meetingCommentRepository.save(createMeetingComment(meetingCommentWriteReqDto, member, findMeetingPost));
    }

    private MeetingComment createMeetingComment(MeetingCommentWriteReqDto meetingCommentWriteReqDto, Member member, MeetingPost findMeetingPost) {
        return MeetingComment.builder()
                .member(member)
                .meetingPost(findMeetingPost)
                .content(meetingCommentWriteReqDto.getContent())
                .build();
    }

    private List<MeetingMember> findMeetingMemberListByMeetingId(Long meetingId) {
        return meetingMemberRepository.findByMeetingId(meetingId);
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

    private void validateJoinedMember(boolean isJoined) {
        if (!isJoined) {
            throwException(ErrorCode.FORBIDDEN_MEMBER, "모임에 가입한 회원이 아닙니다.");
        }
    }

    private void throwException(ErrorCode errorCode, String message) {
        throw new CustomException(errorCode, message);
    }

    private Meeting validateOptionalMeeting(Optional<Meeting> optionalMeeting) {
        return optionalMeeting.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEETING, "존재하지 않은 모임입니다."));
    }

    private Optional<Meeting> findOptionalMeetingByMeetingId(Long meetingId) {
        return meetingRepository.findById(meetingId);
    }

    private Optional<MeetingPost> findOptionalMeetingPostByMeetingPostId(Long meetingPostId) {
        return meetingPostRepository.findById(meetingPostId);
    }

    private MeetingPost validateOptionalMeetingPost(Optional<MeetingPost> optionalMeetingPost) {
        return optionalMeetingPost.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEETING, "존재하지 않은 모임입니다."));
    }
}
