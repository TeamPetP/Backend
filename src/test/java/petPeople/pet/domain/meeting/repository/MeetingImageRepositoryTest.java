package petPeople.pet.domain.meeting.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import petPeople.pet.common.BaseControllerTest;
import petPeople.pet.domain.meeting.entity.*;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.member.repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class MeetingImageRepositoryTest extends BaseControllerTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MeetingRepository meetingRepository;
    @Autowired
    MeetingImageRepository meetingImageRepository;


    final String uid = "abcd";
    final String email = "issiscv@naver.com";
    final String name = "김상운";
    final String nickname = "balladang";
    final String imgUrl = "www.imgurl.com";
    final String introduce = "잘지내요 우리";

    final String title = "이번 주말에 댕댕이 사료 만드실 분 구해요";
    final String content = "서울 신촌 근처에서 강아지 간식 및 사료 직접 만드실 분 구합니다~~ ";
    final String doName = "서울시";
    final String sigungu = "마포구";
    final Sex sex = Sex.MALE;
    final Category category = Category.WALK;
    final String conditions = "탈모아닌사람만";
    final Integer maxPeople = 5;
    final String period = "주 2회";
    final String location = "올림픽 공원";
    final MeetingType meetingType = MeetingType.REGULAR;

    final String meetingImgUrl = "www.meetingImage.com";

    @Test
    public void findByMeetingIdsTest() throws Exception {
        //given
        Member member = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));

        List<Long> idList = new ArrayList<>();
        Meeting saveMeeting1 = meetingRepository.save(createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        Meeting saveMeeting2 = meetingRepository.save(createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        Meeting saveMeeting3 = meetingRepository.save(createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        idList.add(saveMeeting1.getId());
        idList.add(saveMeeting2.getId());
        idList.add(saveMeeting3.getId());

        MeetingImage saveMeetingImage1 = meetingImageRepository.save(createMeetingImage(saveMeeting1, meetingImgUrl));
        MeetingImage saveMeetingImage2 = meetingImageRepository.save(createMeetingImage(saveMeeting2, meetingImgUrl));
        MeetingImage saveMeetingImage3 = meetingImageRepository.save(createMeetingImage(saveMeeting3, meetingImgUrl));

        //when
        List<MeetingImage> meetingImageList = meetingImageRepository.findByMeetingIds(idList);

        //then
        assertThat(meetingImageList).contains(saveMeetingImage1, saveMeetingImage2, saveMeetingImage3);
    }

    @Test
    public void findByMeetingIdTest() throws Exception {
        //given
        Member member = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));

        Meeting saveMeeting1 = meetingRepository.save(createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        MeetingImage saveMeetingImage1 = meetingImageRepository.save(createMeetingImage(saveMeeting1, meetingImgUrl));
        MeetingImage saveMeetingImage2 = meetingImageRepository.save(createMeetingImage(saveMeeting1, meetingImgUrl));
        MeetingImage saveMeetingImage3 = meetingImageRepository.save(createMeetingImage(saveMeeting1, meetingImgUrl));

        //when
        List<MeetingImage> meetingImageList = meetingImageRepository.findByMeetingId(saveMeeting1.getId());

        //then
        assertThat(meetingImageList).contains(saveMeetingImage1, saveMeetingImage2, saveMeetingImage3);
    }

    @Test
    public void deleteByMeetingIdTest() throws Exception {
        //given
        Member member = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));

        Meeting saveMeeting = meetingRepository.save(createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        meetingImageRepository.save(createMeetingImage(saveMeeting, meetingImgUrl));
        meetingImageRepository.save(createMeetingImage(saveMeeting, meetingImgUrl));
        meetingImageRepository.save(createMeetingImage(saveMeeting, meetingImgUrl));

        //when
        Long count = meetingImageRepository.deleteByMeetingId(saveMeeting.getId());
        List<MeetingImage> meetingImageList = meetingImageRepository.findByMeetingId(saveMeeting.getId());

        //then
        assertThat(count).isEqualTo(3);
        assertThat(meetingImageList).isEmpty();;

    }

    private MeetingImage createMeetingImage(Meeting meeting, String meetingImgUrl) {
        return MeetingImage.builder()
                .meeting(meeting)
                .imgUrl(meetingImgUrl)
                .build();
    }

    private MeetingMember createMeetingMember(Member member, Meeting meeting1) {
        return MeetingMember.builder()
                .member(member)
                .meeting(meeting1)
                .build();
    }

    private Member createMember(String uid, String email, String name, String nickname, String imgUrl, String introduce) {
        return Member.builder()
                .uid(uid)
                .email(email)
                .name(name)
                .nickname(nickname)
                .imgUrl(imgUrl)
                .introduce(introduce)
                .build();
    }

    private Meeting createMeeting(Member member, String title, String content, String doName, String sigungu, String location, Sex sex, Category category, MeetingType meetingType, String period, String conditions, Integer maxPeople) {
        return Meeting.builder()
                .member(member)
                .title(title)
                .content(content)
                .doName(doName)
                .sigungu(sigungu)
                .location(location)
                .sex(sex)
                .category(category)
                .meetingType(meetingType)
                .period(period)
                .conditions(conditions)
                .maxPeople(maxPeople)
                .isOpened(true)
                .build();
    }

}