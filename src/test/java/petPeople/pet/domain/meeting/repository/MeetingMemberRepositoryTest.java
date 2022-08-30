package petPeople.pet.domain.meeting.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import petPeople.pet.common.BaseControllerTest;
import petPeople.pet.domain.meeting.entity.*;
import petPeople.pet.domain.meeting.entity.vo.Category;
import petPeople.pet.domain.meeting.entity.vo.MeetingType;
import petPeople.pet.domain.meeting.entity.vo.Sex;
import petPeople.pet.domain.meeting.repository.meeting.MeetingRepository;
import petPeople.pet.domain.meeting.repository.meeting_member.MeetingMemberRepository;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.member.repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class MeetingMemberRepositoryTest extends BaseControllerTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MeetingMemberRepository meetingMemberRepository;
    @Autowired
    MeetingRepository meetingRepository;

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

    @DisplayName("여러 meeting ID 로 meetingMember 조회")
    @Test
    public void findByMeetingIdsTest() throws Exception {
        //given
        Member member = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));

        List<MeetingMember> meetingMemberList = new ArrayList<>();

        Meeting saveMeeting1 = meetingRepository.save(createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        meetingMemberList.add(meetingMemberRepository.save(createMeetingMember(member, saveMeeting1)));

        Meeting saveMeeting2 = meetingRepository.save(createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        meetingMemberList.add(meetingMemberRepository.save(createMeetingMember(member, saveMeeting2)));

        Meeting saveMeeting3 = meetingRepository.save(createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        meetingMemberList.add(meetingMemberRepository.save(createMeetingMember(member, saveMeeting3)));

        List<Long> idList = new ArrayList<>();
        idList.add(saveMeeting1.getId());
        idList.add(saveMeeting2.getId());
        idList.add(saveMeeting3.getId());

        //when
        List<MeetingMember> meetingMembers = meetingMemberRepository.findByMeetingIds(idList);

        //then
        assertThat(meetingMembers.containsAll(meetingMemberList)).isTrue();
    }

    @DisplayName("meeting ID 로 meetingMember 조회")
    @Test
    public void findByMeetingIdTest() throws Exception {
        //given
        Member saveMember1 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Member saveMember2 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Member saveMember3 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));

        Meeting saveMeeting = meetingRepository.save(createMeeting(saveMember1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        List<MeetingMember> meetingMemberList = new ArrayList<>();
        MeetingMember saveMeetingMember1 = meetingMemberRepository.save(createMeetingMember(saveMember1, saveMeeting));
        meetingMemberList.add(saveMeetingMember1);

        MeetingMember saveMeetingMember2 = meetingMemberRepository.save(createMeetingMember(saveMember2, saveMeeting));
        meetingMemberList.add(saveMeetingMember2);

        MeetingMember saveMeetingMember3 = meetingMemberRepository.save(createMeetingMember(saveMember3, saveMeeting));
        meetingMemberList.add(saveMeetingMember3);

        //when
        List<MeetingMember> meetingMembers = meetingMemberRepository.findByMeetingId(saveMeeting.getId());

        //then
        assertThat(meetingMembers.containsAll(meetingMemberList)).isTrue();
    }


    @DisplayName("meeting ID 그리고 member ID 로 meetingMember 삭제")
    @Test
    public void deleteByMeetingIdAndMemberId() throws Exception {
        //given
        Member member = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));


        Meeting saveMeeting1 = meetingRepository.save(createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        MeetingMember meetingMember1 = meetingMemberRepository.save(createMeetingMember(member, saveMeeting1));

        Meeting saveMeeting2 = meetingRepository.save(createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        MeetingMember meetingMember2 = meetingMemberRepository.save(createMeetingMember(member, saveMeeting2));

        Meeting saveMeeting3 = meetingRepository.save(createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        MeetingMember meetingMember3 = meetingMemberRepository.save(createMeetingMember(member, saveMeeting3));

        //when
        Long count1 = meetingMemberRepository.deleteByMeetingIdAndMemberId(saveMeeting1.getId(), member.getId());
        Long count2 = meetingMemberRepository.deleteByMeetingIdAndMemberId(saveMeeting2.getId(), member.getId());
        Long count3 = meetingMemberRepository.deleteByMeetingIdAndMemberId(saveMeeting3.getId(), member.getId());

        Optional<MeetingMember> meetingMemberOptional1 = meetingMemberRepository.findById(meetingMember1.getId());
        Optional<MeetingMember> meetingMemberOptional2 = meetingMemberRepository.findById(meetingMember2.getId());
        Optional<MeetingMember> meetingMemberOptional3 = meetingMemberRepository.findById(meetingMember3.getId());

        //then
        assertThat(count1).isEqualTo(1L);
        assertThat(count2).isEqualTo(1L);
        assertThat(count3).isEqualTo(1L);

        assertThat(meetingMemberOptional1.isEmpty()).isTrue();
        assertThat(meetingMemberOptional2.isEmpty()).isTrue();
        assertThat(meetingMemberOptional3.isEmpty()).isTrue();

    }

    @DisplayName("meeting Id 로 meetingMember 개수 조회")
    @Test
    public void countByMeetingIdTest() throws Exception {
        //given
        Member member1 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Member member2 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Member member3 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));


        Meeting saveMeeting1 = meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        meetingMemberRepository.save(createMeetingMember(member1, saveMeeting1));
        meetingMemberRepository.save(createMeetingMember(member2, saveMeeting1));
        meetingMemberRepository.save(createMeetingMember(member3, saveMeeting1));

        //when
        Long count = meetingMemberRepository.countByMeetingId(saveMeeting1.getId());

        //then
        assertThat(count).isEqualTo(3L);
    }

    @DisplayName("member ID 로 meetingMember 개수 조회")
    @Test
    public void countByMemberIdTest() throws Exception {
        //given
        Member member = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));


        Meeting saveMeeting1 = meetingRepository.save(createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        Meeting saveMeeting2 = meetingRepository.save(createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        Meeting saveMeeting3 = meetingRepository.save(createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        meetingMemberRepository.save(createMeetingMember(member, saveMeeting1));
        meetingMemberRepository.save(createMeetingMember(member, saveMeeting2));
        meetingMemberRepository.save(createMeetingMember(member, saveMeeting3));

        //when
        Long count = meetingMemberRepository.countByMemberId(member.getId());

        //then
        assertThat(count).isEqualTo(3L);
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