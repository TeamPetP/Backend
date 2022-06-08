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
public class MeetingPostWriteRespDto {

    private Long meetingPostId;

    private Long meetingId;

    private String title;

    private String content;

    private List<String> imgUrlList = new ArrayList<>();

    private LocalDateTime createdDate;

    public MeetingPostWriteRespDto(MeetingPost meetingPost, List<MeetingPostImage> meetingPostImageList) {
        this.meetingPostId = meetingPost.getId();
        this.meetingId = meetingPost.getMeeting().getId();
        this.title = meetingPost.getTitle();
        this.content = meetingPost.getContent();
        this.createdDate = meetingPost.getCreatedDate();

        for (MeetingPostImage meetingPostImage : meetingPostImageList) {
            imgUrlList.add(meetingPostImage.getImgUrl());
        }
    }
}
