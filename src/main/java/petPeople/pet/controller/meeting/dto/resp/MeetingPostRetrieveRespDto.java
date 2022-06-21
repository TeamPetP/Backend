package petPeople.pet.controller.meeting.dto.resp;

import lombok.*;
import petPeople.pet.domain.meeting.entity.MeetingPost;
import petPeople.pet.domain.meeting.entity.MeetingPostImage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class MeetingPostRetrieveRespDto {

    private Long meetingPostId;

    private Long meetingId;

    private Long memberId;

    private String nickname;

    private String title;

    private String content;

    private List<String> imgUrlList = new ArrayList<>();

    private LocalDateTime createdDate;

    private Long likeCnt;

    private Boolean isLiked;

    public MeetingPostRetrieveRespDto(MeetingPost meetingPost, List<MeetingPostImage> meetingPostImageList, Long likeCnt, boolean isLiked) {
        this.meetingPostId = meetingPost.getId();
        this.meetingId = meetingPost.getMeeting().getId();
        this.memberId = meetingPost.getMember().getId();
        this.nickname = meetingPost.getMember().getNickname();
        this.title = meetingPost.getTitle();
        this.content = meetingPost.getContent();
        this.createdDate = meetingPost.getCreatedDate();

        for (MeetingPostImage meetingPostImage : meetingPostImageList) {
            imgUrlList.add(meetingPostImage.getImgUrl());
        }

        this.likeCnt = likeCnt;
        this.isLiked = isLiked;
    }
}