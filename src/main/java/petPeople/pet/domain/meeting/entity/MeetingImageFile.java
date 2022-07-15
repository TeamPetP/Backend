package petPeople.pet.domain.meeting.entity;

import lombok.*;
import petPeople.pet.domain.base.BaseTimeEntity;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter @Setter
@Builder
public class MeetingImageFile extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "meeting_image_file_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    private String imgFileUrl;
}
