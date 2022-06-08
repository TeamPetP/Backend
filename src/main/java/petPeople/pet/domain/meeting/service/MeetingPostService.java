package petPeople.pet.domain.meeting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petPeople.pet.controller.meeting.dto.req.MeetingPostWriteReqDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingPostWriteRespDto;
import petPeople.pet.domain.meeting.entity.Meeting;
import petPeople.pet.domain.meeting.entity.MeetingMember;
import petPeople.pet.domain.meeting.entity.MeetingPost;
import petPeople.pet.domain.meeting.entity.MeetingPostImage;
import petPeople.pet.domain.meeting.repository.MeetingMemberRepository;
import petPeople.pet.domain.meeting.repository.MeetingPostImageRepository;
import petPeople.pet.domain.meeting.repository.MeetingPostRepository;
import petPeople.pet.domain.meeting.repository.MeetingRepository;
import petPeople.pet.domain.member.entity.Member;
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
    private final MeetingRepository meetingRepository;

    @Transactional
    public MeetingPostWriteRespDto write(Member member, MeetingPostWriteReqDto meetingPostWriteReqDto, Long meetingId) {
        validateJoinedMember(isJoined(member, findMeetingMemberListByMeetingId(meetingId)));

        Meeting findMeeting = validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));
        MeetingPost saveMeetingPost = saveMeetingPost(createMeetingPost(member, meetingPostWriteReqDto, findMeeting));
        List<MeetingPostImage> saveMeetingPostImageList = saveMeetingPostImageList(member, saveMeetingPost, meetingPostWriteReqDto.getImgUrlList());

        return new MeetingPostWriteRespDto(saveMeetingPost, saveMeetingPostImageList);
    }

    public MeetingPostWriteRespDto retrieveOne(Long meetingId, Long meetingPostId, Member member) {
        validateJoinedMember(isJoined(member, findMeetingMemberListByMeetingId(meetingId)));
        validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));

        MeetingPost findMeetingPost = validateOptionalMeetingPost(findMeetingPostByMeetingPostId(meetingPostId));

        List<MeetingPostImage> findMeetingPostImageList = findAllMeetingPostImageByMeetingPostId(meetingPostId);

        return new MeetingPostWriteRespDto(findMeetingPost, findMeetingPostImageList);
    }

    public Slice<MeetingPostWriteRespDto> retrieveAll(Long meetingId, Pageable pageable, Member member) {
        validateJoinedMember(isJoined(member, findMeetingMemberListByMeetingId(meetingId)));
        validateOptionalMeeting(findOptionalMeetingByMeetingId(meetingId));

        Slice<MeetingPost> meetingPostSlice = findAllMeetingPostSliceByMeetingId(meetingId, pageable);

        List<Long> meetingPostIds = getMeetingPostId(meetingPostSlice.getContent());

        List<MeetingPostImage> findMeetingPostImageList = findAllMeetingPostImageByMeetingPostIds(meetingPostIds);

        return meetingPostSlice.map(meetingPost -> {
            List<MeetingPostImage> meetingPostImageList = getMeetingPostImagesByMeetingPost(findMeetingPostImageList, meetingPost);
            return new MeetingPostWriteRespDto(meetingPost, meetingPostImageList);
        });
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

    private Optional<MeetingPost> findMeetingPostByMeetingPostId(Long meetingPostId) {
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
