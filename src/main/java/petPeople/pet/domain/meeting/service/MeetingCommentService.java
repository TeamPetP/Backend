package petPeople.pet.domain.meeting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petPeople.pet.controller.meeting.dto.req.MeetingCommentWriteReqDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingCommentRetrieveRespDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingCommentWriteRespDto;
import petPeople.pet.domain.meeting.entity.*;
import petPeople.pet.domain.meeting.repository.*;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.exception.CustomException;
import petPeople.pet.exception.ErrorCode;

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

    @Transactional
    public MeetingCommentWriteRespDto write(Long meetingId, Long meetingPostId, MeetingCommentWriteReqDto meetingCommentWriteReqDto, Member member) {

        validateJoinedMember(isJoined(member, findMeetingMemberListByMeetingId(meetingId)));

        validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));
        MeetingPost findMeetingPost = validateOptionalMeetingPost(findOptionalMeetingPostByMeetingPostId(meetingPostId));

        MeetingComment saveMeetingComment = saveMeetingComment(meetingCommentWriteReqDto, member, findMeetingPost);

        return new MeetingCommentWriteRespDto(saveMeetingComment.getId(), meetingPostId, meetingId, meetingCommentWriteReqDto.getContent());
    }

    public Slice<MeetingCommentRetrieveRespDto> retrieveComments(Long meetingId, Long meetingPostId, Member member, Pageable pageable) {

        validateJoinedMember(isJoined(member, findMeetingMemberListByMeetingId(meetingId)));

        validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));
        validateOptionalMeetingPost(findOptionalMeetingPostByMeetingPostId(meetingPostId));

        List<MeetingCommentLike> meetingCommentLikeList = findMeetingCommentLikeByPostId(meetingPostId);

        return findMeetingCommentByMeetingPostId(meetingPostId, pageable)
                .map(meetingComment -> {
                    boolean isLiked = false;
                    for (MeetingCommentLike meetingCommentLike : meetingCommentLikeList) {
                        if (meetingCommentLike.getMember() == member) {
                            isLiked = true;
                            break;
                        }
                    }
                    return new MeetingCommentRetrieveRespDto(meetingComment.getId(), meetingPostId, meetingId, meetingComment.getContent(), isLiked);
                });
    }

    private List<MeetingCommentLike> findMeetingCommentLikeByPostId(Long meetingPostId) {
        return meetingCommentLikeRepository.findByMeetingPostId(meetingPostId);
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
