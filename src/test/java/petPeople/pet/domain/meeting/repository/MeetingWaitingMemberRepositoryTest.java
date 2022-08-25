package petPeople.pet.domain.meeting.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.annotation.Rollback;
import petPeople.pet.common.BaseControllerTest;
import petPeople.pet.domain.meeting.entity.Meeting;
import petPeople.pet.domain.meeting.entity.MeetingWaitingMember;
import petPeople.pet.domain.meeting.entity.vo.Category;
import petPeople.pet.domain.meeting.entity.vo.JoinRequestStatus;
import petPeople.pet.domain.meeting.entity.vo.MeetingType;
import petPeople.pet.domain.meeting.entity.vo.Sex;
import petPeople.pet.domain.meeting.repository.meeting.MeetingRepository;
import petPeople.pet.domain.meeting.repository.meeting_waiting_member.MeetingWaitingMemberRepository;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.member.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Rollback(value = false)
class MeetingWaitingMemberRepositoryTest extends BaseControllerTest {

    @Autowired
    MeetingRepository meetingRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MeetingWaitingMemberRepository meetingWaitingMemberRepository;
    @Autowired
    EntityManager em;
    @Autowired
    EntityManagerFactory emf;

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

    @DisplayName("meeting ID 로 meetingWaitingMember 조회 및 member 와 fetch join")
    @Test
    public void findAllByMeetingIdFetchJoinMemberTest() throws Exception {
        //given
        Member member = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Meeting meeting = meetingRepository.save(createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        MeetingWaitingMember meetingWaitingMember1 = meetingWaitingMemberRepository.save(createMeetingWaitingMember(member, meeting, JoinRequestStatus.WAITING));
        MeetingWaitingMember meetingWaitingMember2 = meetingWaitingMemberRepository.save(createMeetingWaitingMember(member, meeting, JoinRequestStatus.WAITING));
        MeetingWaitingMember meetingWaitingMember3 = meetingWaitingMemberRepository.save(createMeetingWaitingMember(member, meeting, JoinRequestStatus.WAITING));

        MeetingWaitingMember meetingWaitingMember4 = meetingWaitingMemberRepository.save(createMeetingWaitingMember(member, meeting, JoinRequestStatus.APPROVED));
        MeetingWaitingMember meetingWaitingMember5 = meetingWaitingMemberRepository.save(createMeetingWaitingMember(member, meeting, JoinRequestStatus.DECLINED));

        //when
        List<MeetingWaitingMember> meetingWaitingMemberList = meetingWaitingMemberRepository.findAllByMeetingIdFetchJoinMember(meeting.getId());

        //then
        assertThat(meetingWaitingMemberList).contains(meetingWaitingMember1, meetingWaitingMember2, meetingWaitingMember3);
        assertThat(meetingWaitingMemberList).doesNotContain(meetingWaitingMember4, meetingWaitingMember5);

    }

    @DisplayName("meeting ID 로 member ID 로 조회")
    @Test
    public void findAllByMeetingIdAndMemberIdTest() throws Exception {
        //given
        Member member1 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Member member2 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Meeting meeting1 = meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        Meeting meeting2 = meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        MeetingWaitingMember meetingWaitingMember1 = meetingWaitingMemberRepository.save(createMeetingWaitingMember(member1, meeting1, JoinRequestStatus.WAITING));
        MeetingWaitingMember meetingWaitingMember2 = meetingWaitingMemberRepository.save(createMeetingWaitingMember(member1, meeting2, JoinRequestStatus.WAITING));

        MeetingWaitingMember meetingWaitingMember3 = meetingWaitingMemberRepository.save(createMeetingWaitingMember(member2, meeting1, JoinRequestStatus.WAITING));
        MeetingWaitingMember meetingWaitingMember4 = meetingWaitingMemberRepository.save(createMeetingWaitingMember(member2, meeting2, JoinRequestStatus.WAITING));

        //when
        MeetingWaitingMember findMeetingWaitingMember1 = meetingWaitingMemberRepository.findAllByMeetingIdAndMemberId(meeting1.getId(), member1.getId()).get();
        MeetingWaitingMember findMeetingWaitingMember2 = meetingWaitingMemberRepository.findAllByMeetingIdAndMemberId(meeting2.getId(), member1.getId()).get();

        MeetingWaitingMember findMeetingWaitingMember3 = meetingWaitingMemberRepository.findAllByMeetingIdAndMemberId(meeting1.getId(), member2.getId()).get();
        MeetingWaitingMember findMeetingWaitingMember4 = meetingWaitingMemberRepository.findAllByMeetingIdAndMemberId(meeting2.getId(), member2.getId()).get();

        //then
        assertThat(findMeetingWaitingMember1).isEqualTo(meetingWaitingMember1);
        assertThat(findMeetingWaitingMember2).isEqualTo(meetingWaitingMember2);
        assertThat(findMeetingWaitingMember3).isEqualTo(meetingWaitingMember3);
        assertThat(findMeetingWaitingMember4).isEqualTo(meetingWaitingMember4);
        assertThat(findMeetingWaitingMember4).isEqualTo(meetingWaitingMember4);
    }
    
    @DisplayName("member ID 를 통해 meetingWaitingMember 페이징 조회")
    @Test
    public void findAllByMemberIdFetchJoinMemberAndMeetingTest() throws Exception {
        //given
        int size = 2;
        PageRequest pageRequest1 = PageRequest.of(0, size);
        PageRequest pageRequest2 = PageRequest.of(1, size);

        Member member1 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Member member2 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Meeting meeting1 = meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        Meeting meeting2 = meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        Meeting meeting3 = meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        Meeting meeting4 = meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        MeetingWaitingMember meetingWaitingMember1 = meetingWaitingMemberRepository.save(createMeetingWaitingMember(member1, meeting1, JoinRequestStatus.WAITING));
        MeetingWaitingMember meetingWaitingMember2 = meetingWaitingMemberRepository.save(createMeetingWaitingMember(member1, meeting2, JoinRequestStatus.WAITING));
        MeetingWaitingMember meetingWaitingMember3 = meetingWaitingMemberRepository.save(createMeetingWaitingMember(member1, meeting3, JoinRequestStatus.WAITING));
        MeetingWaitingMember meetingWaitingMember4 = meetingWaitingMemberRepository.save(createMeetingWaitingMember(member1, meeting4, JoinRequestStatus.WAITING));

        MeetingWaitingMember meetingWaitingMember5 = meetingWaitingMemberRepository.save(createMeetingWaitingMember(member2, meeting1, JoinRequestStatus.WAITING));
        MeetingWaitingMember meetingWaitingMember6 = meetingWaitingMemberRepository.save(createMeetingWaitingMember(member2, meeting2, JoinRequestStatus.WAITING));
        MeetingWaitingMember meetingWaitingMember7 = meetingWaitingMemberRepository.save(createMeetingWaitingMember(member2, meeting3, JoinRequestStatus.WAITING));
        MeetingWaitingMember meetingWaitingMember8 = meetingWaitingMemberRepository.save(createMeetingWaitingMember(member2, meeting4, JoinRequestStatus.WAITING));

        //when
        Slice<MeetingWaitingMember> meetingWaitingMemberSlice1 = meetingWaitingMemberRepository.findAllByMemberIdFetchJoinMemberAndMeeting(pageRequest1, member1.getId());
        Slice<MeetingWaitingMember> meetingWaitingMemberSlice2 = meetingWaitingMemberRepository.findAllByMemberIdFetchJoinMemberAndMeeting(pageRequest2, member1.getId());

        Slice<MeetingWaitingMember> meetingWaitingMemberSlice3 = meetingWaitingMemberRepository.findAllByMemberIdFetchJoinMemberAndMeeting(pageRequest1, member2.getId());
        Slice<MeetingWaitingMember> meetingWaitingMemberSlice4 = meetingWaitingMemberRepository.findAllByMemberIdFetchJoinMemberAndMeeting(pageRequest2, member2.getId());

        //then
        assertThat(meetingWaitingMemberSlice1.getContent()).contains(meetingWaitingMember3, meetingWaitingMember4);
        assertThat(meetingWaitingMemberSlice2.getContent()).contains(meetingWaitingMember1, meetingWaitingMember2);

        assertThat(meetingWaitingMemberSlice3.getContent()).contains(meetingWaitingMember7, meetingWaitingMember8);
        assertThat(meetingWaitingMemberSlice4.getContent()).contains(meetingWaitingMember5, meetingWaitingMember6);
    }

    @DisplayName("meeting ID 와 member ID 를 통해 tkrwp")
    @Test
    public void deleteByMeetingIdAndMemberIdTest() throws Exception {
        Member member1 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Member member2 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Meeting meeting1 = meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        Meeting meeting2 = meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        MeetingWaitingMember meetingWaitingMember1 = meetingWaitingMemberRepository.save(createMeetingWaitingMember(member1, meeting1, JoinRequestStatus.WAITING));
        MeetingWaitingMember meetingWaitingMember2 = meetingWaitingMemberRepository.save(createMeetingWaitingMember(member1, meeting2, JoinRequestStatus.WAITING));

        MeetingWaitingMember meetingWaitingMember5 = meetingWaitingMemberRepository.save(createMeetingWaitingMember(member2, meeting1, JoinRequestStatus.WAITING));
        MeetingWaitingMember meetingWaitingMember6 = meetingWaitingMemberRepository.save(createMeetingWaitingMember(member2, meeting2, JoinRequestStatus.WAITING));

        //when
        Long count1 = meetingWaitingMemberRepository.deleteByMeetingIdAndMemberId(meeting1.getId(), member1.getId());
        Long count2 = meetingWaitingMemberRepository.deleteByMeetingIdAndMemberId(meeting1.getId(), member2.getId());
        Long count3 = meetingWaitingMemberRepository.deleteByMeetingIdAndMemberId(meeting2.getId(), member1.getId());
        Long count4 = meetingWaitingMemberRepository.deleteByMeetingIdAndMemberId(meeting2.getId(), member2.getId());

        List<MeetingWaitingMember> meetingWaitingMemberList1 = meetingWaitingMemberRepository.findByMemberIdAndMeetingId(meeting1.getId(), member1.getId());
        List<MeetingWaitingMember> meetingWaitingMemberList2 = meetingWaitingMemberRepository.findByMemberIdAndMeetingId(meeting1.getId(), member2.getId());
        List<MeetingWaitingMember> meetingWaitingMemberList3 = meetingWaitingMemberRepository.findByMemberIdAndMeetingId(meeting2.getId(), member1.getId());
        List<MeetingWaitingMember> meetingWaitingMemberList4 = meetingWaitingMemberRepository.findByMemberIdAndMeetingId(meeting2.getId(), member2.getId());

        //then
        assertThat(count1).isEqualTo(1);
        assertThat(count2).isEqualTo(1);
        assertThat(count3).isEqualTo(1);
        assertThat(count4).isEqualTo(1);

        assertThat(meetingWaitingMemberList1.isEmpty()).isTrue();
        assertThat(meetingWaitingMemberList2.isEmpty()).isTrue();
        assertThat(meetingWaitingMemberList3.isEmpty()).isTrue();
        assertThat(meetingWaitingMemberList4.isEmpty()).isTrue();

    }

    @Test
    public void findByMemberIdAndMeetingIdTest() throws Exception {
        Member member1 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Member member2 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Meeting meeting1 = meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        Meeting meeting2 = meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        MeetingWaitingMember meetingWaitingMember1 = meetingWaitingMemberRepository.save(createMeetingWaitingMember(member1, meeting1, JoinRequestStatus.DECLINED));
        MeetingWaitingMember meetingWaitingMember2 = meetingWaitingMemberRepository.save(createMeetingWaitingMember(member1, meeting1, JoinRequestStatus.WAITING));

        MeetingWaitingMember meetingWaitingMember3 = meetingWaitingMemberRepository.save(createMeetingWaitingMember(member2, meeting2, JoinRequestStatus.DECLINED));
        MeetingWaitingMember meetingWaitingMember4 = meetingWaitingMemberRepository.save(createMeetingWaitingMember(member2, meeting2, JoinRequestStatus.WAITING));

        //when
        List<MeetingWaitingMember> meetingWaitingMemberList1 = meetingWaitingMemberRepository.findByMemberIdAndMeetingId(member1.getId(), meeting1.getId());
        List<MeetingWaitingMember> meetingWaitingMemberList2 = meetingWaitingMemberRepository.findByMemberIdAndMeetingId(member2.getId(), meeting2.getId());

        //then
        assertThat(meetingWaitingMemberList1).contains(meetingWaitingMember1, meetingWaitingMember2);
        assertThat(meetingWaitingMemberList2).contains(meetingWaitingMember3, meetingWaitingMember4);
    }

    private MeetingWaitingMember createMeetingWaitingMember(Member member, Meeting meeting, JoinRequestStatus waiting) {
        return MeetingWaitingMember.builder()
                .meeting(meeting)
                .member(member)
                .joinRequestStatus(waiting)
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
