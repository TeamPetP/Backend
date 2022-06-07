package petPeople.pet.domain.meeting.service;

import lombok.RequiredArgsConstructor;
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

}
