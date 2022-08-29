package petPeople.pet.domain.meeting.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import petPeople.pet.common.BaseControllerTest;
import petPeople.pet.domain.meeting.entity.Meeting;
import petPeople.pet.domain.meeting.entity.MeetingBookmark;
import petPeople.pet.domain.meeting.entity.vo.Category;
import petPeople.pet.domain.meeting.entity.vo.MeetingType;
import petPeople.pet.domain.meeting.entity.vo.Sex;
import petPeople.pet.domain.meeting.repository.meeting.MeetingRepository;
import petPeople.pet.domain.meeting.repository.meeting_bookmark.MeetingBookmarkRepository;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.member.repository.MemberRepository;

import javax.persistence.EntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class MeetingBookMarkRepositoryTest extends BaseControllerTest {

    @Autowired
    MeetingRepository meetingRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MeetingBookmarkRepository meetingBookmarkRepository;
    @Autowired
    EntityManager em;

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

    @DisplayName("member id 와 meeting id 로 조회")
    @Test
    public void findByMemberIdAndMeetingIdTest() throws Exception {
        //given
        Member member1 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Member member2 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Member member3 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));

        Meeting meeting = meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        MeetingBookmark saveMeetingBookmark1 = meetingBookmarkRepository.save(createMeetingBookmark(member1, meeting));
        MeetingBookmark saveMeetingBookmark2 = meetingBookmarkRepository.save(createMeetingBookmark(member2, meeting));
        MeetingBookmark saveMeetingBookmark3 = meetingBookmarkRepository.save(createMeetingBookmark(member3, meeting));

        //when
        MeetingBookmark meetingBookmark1 = meetingBookmarkRepository.findByMemberIdAndMeetingId(member1.getId(), meeting.getId()).get();
        MeetingBookmark meetingBookmark2 = meetingBookmarkRepository.findByMemberIdAndMeetingId(member2.getId(), meeting.getId()).get();
        MeetingBookmark meetingBookmark3 = meetingBookmarkRepository.findByMemberIdAndMeetingId(member3.getId(), meeting.getId()).get();

        //then
        assertThat(meetingBookmark1).isEqualTo(saveMeetingBookmark1);
        assertThat(meetingBookmark2).isEqualTo(saveMeetingBookmark2);
        assertThat(meetingBookmark3).isEqualTo(saveMeetingBookmark3);

    }
    
    @DisplayName("member id 로 페이징 조회")
    @Test
    public void findByMemberIdWithFetchJoinMeetingTest() throws Exception {
        int size = 2;
        PageRequest pageRequest1 = PageRequest.of(0, size);
        PageRequest pageRequest2 = PageRequest.of(1, size);

        Member member1 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Member member2 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));

        Meeting meeting = meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        MeetingBookmark saveMeetingBookmark1 = meetingBookmarkRepository.save(createMeetingBookmark(member1, meeting));
        MeetingBookmark saveMeetingBookmark2 = meetingBookmarkRepository.save(createMeetingBookmark(member1, meeting));
        MeetingBookmark saveMeetingBookmark3 = meetingBookmarkRepository.save(createMeetingBookmark(member1, meeting));
        MeetingBookmark saveMeetingBookmark4 = meetingBookmarkRepository.save(createMeetingBookmark(member1, meeting));

        MeetingBookmark saveMeetingBookmark5 = meetingBookmarkRepository.save(createMeetingBookmark(member2, meeting));
        MeetingBookmark saveMeetingBookmark6 = meetingBookmarkRepository.save(createMeetingBookmark(member2, meeting));
        MeetingBookmark saveMeetingBookmark7 = meetingBookmarkRepository.save(createMeetingBookmark(member2, meeting));
        MeetingBookmark saveMeetingBookmark8 = meetingBookmarkRepository.save(createMeetingBookmark(member2, meeting));

        //when
        Slice<MeetingBookmark> meetingBookmarkSlice1 = meetingBookmarkRepository.findByMemberIdWithFetchJoinMeetingSlicing(member1.getId(), pageRequest1);
        Slice<MeetingBookmark> meetingBookmarkSlice2 = meetingBookmarkRepository.findByMemberIdWithFetchJoinMeetingSlicing(member1.getId(), pageRequest2);

        Slice<MeetingBookmark> meetingBookmarkSlice3 = meetingBookmarkRepository.findByMemberIdWithFetchJoinMeetingSlicing(member2.getId(), pageRequest1);
        Slice<MeetingBookmark> meetingBookmarkSlice4 = meetingBookmarkRepository.findByMemberIdWithFetchJoinMeetingSlicing(member2.getId(), pageRequest2);

        //then
        assertThat(meetingBookmarkSlice1.getContent()).contains(saveMeetingBookmark3, saveMeetingBookmark4);
        assertThat(meetingBookmarkSlice2.getContent()).contains(saveMeetingBookmark1, saveMeetingBookmark2);

        assertThat(meetingBookmarkSlice3.getContent()).contains(saveMeetingBookmark7, saveMeetingBookmark8);
        assertThat(meetingBookmarkSlice4.getContent()).contains(saveMeetingBookmark5, saveMeetingBookmark6);
    }
    
    @DisplayName("member id 와 meeting id 로 삭제")
    @Test
    public void deleteByMemberIdAndMeetingIdTest() throws Exception {
        //given
        Member member1 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Member member2 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));

        Meeting meeting = meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        meetingBookmarkRepository.save(createMeetingBookmark(member1, meeting));
        meetingBookmarkRepository.save(createMeetingBookmark(member2, meeting));

        //when
        Long count1 = meetingBookmarkRepository.deleteByMemberIdAndMeetingId(member1.getId(), meeting.getId());
        Long count2 = meetingBookmarkRepository.deleteByMemberIdAndMeetingId(member2.getId(), meeting.getId());

        Optional<MeetingBookmark> optionalMeetingBookmark1 = meetingBookmarkRepository.findByMemberIdAndMeetingId(member1.getId(), meeting.getId());
        Optional<MeetingBookmark> optionalMeetingBookmark2 = meetingBookmarkRepository.findByMemberIdAndMeetingId(member2.getId(), meeting.getId());

        //then
        assertThat(count1).isEqualTo(1);
        assertThat(count2).isEqualTo(1);

        assertThat(optionalMeetingBookmark1.isEmpty()).isTrue();
        assertThat(optionalMeetingBookmark2.isEmpty()).isTrue();

    }

    private MeetingBookmark createMeetingBookmark(Member member, Meeting meeting) {
        return MeetingBookmark
                .builder()
                .meeting(meeting)
                .member(member)
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
