package petPeople.pet.domain.meeting.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import petPeople.pet.common.BaseControllerTest;
import petPeople.pet.controller.post.model.MeetingParameter;
import petPeople.pet.domain.meeting.entity.*;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.member.repository.MemberRepository;

import static org.assertj.core.api.Assertions.*;

class MeetingRepositoryTest extends BaseControllerTest {


    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MeetingRepository meetingRepository;
    @Autowired
    MeetingMemberRepository meetingMemberRepository;

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

    @DisplayName("회원이 가입한 모임 조회")
    @Test
    public void findAllSlicingByMemberIdTest() throws Exception {
        int size = 3;
        PageRequest pageRequest = PageRequest.of(0, size);

        //given
        Member member1 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));

        Meeting meeting1 = meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        Meeting meeting2 = meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        Meeting meeting3 = meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        Meeting meeting4 = meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        Meeting meeting5 = meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        Meeting meeting6 = meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        meetingMemberRepository.save(createMeetingMember(member1, meeting1));
        meetingMemberRepository.save(createMeetingMember(member1, meeting2));
        meetingMemberRepository.save(createMeetingMember(member1, meeting3));
        meetingMemberRepository.save(createMeetingMember(member1, meeting4));
        meetingMemberRepository.save(createMeetingMember(member1, meeting5));
        meetingMemberRepository.save(createMeetingMember(member1, meeting6));

        //when
        Slice<Meeting> meetingSlice = meetingRepository.findAllSlicingByMemberId(pageRequest, member1.getId());

        //then
        assertThat(meetingSlice.getContent().size()).isEqualTo(3);
        assertThat(meetingSlice.hasNext()).isTrue();
        assertThat(meetingSlice.hasPrevious()).isFalse();
    }
    
    @DisplayName("조건없이 모임 페이징 조회")
    @Test
    public void findAllSlicingWithFetchJoinMemberTest() throws Exception {
        int size = 3;
        PageRequest pageRequest = PageRequest.of(0, size);

        //given
        Member member1 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));

        meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        //when
        MeetingParameter meetingParameter = createMeetingCondition(null, null, null, null);
        Slice<Meeting> meetingSlice = meetingRepository.findAllSlicingWithFetchJoinMember(pageRequest, meetingParameter);

        //then
        assertThat(meetingSlice.getContent().size()).isEqualTo(3);
        assertThat(meetingSlice.hasNext()).isTrue();
        assertThat(meetingSlice.hasPrevious()).isFalse();

    }

    @DisplayName("조건있는 모임 페이징 조회")
    @Test
    public void findAllSlicingWithConditionFetchJoinMemberTest() throws Exception {

        //given
        int size = 3;
        PageRequest pageRequest = PageRequest.of(0, size);

        Member member1 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));

        meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        meetingRepository.save(createMeeting(member1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        //when
        MeetingParameter meetingParameter = createMeetingCondition(member1.getNickname(), doName, "true", content);
        Slice<Meeting> meetingSlice = meetingRepository.findAllSlicingWithFetchJoinMember(pageRequest, meetingParameter);

        //then
        assertThat(meetingSlice.getContent().size()).isEqualTo(3);
        assertThat(meetingSlice.hasNext()).isTrue();
        assertThat(meetingSlice.hasPrevious()).isFalse();

    }

    private MeetingParameter createMeetingCondition(String meetingHost, String dosi, String isOpened, String content) {
        return MeetingParameter.builder()
                .meetingHost(meetingHost)
                .dosi(dosi)
                .isOpened(isOpened)
                .content(content)
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