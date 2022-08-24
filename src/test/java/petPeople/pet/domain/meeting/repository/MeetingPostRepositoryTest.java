package petPeople.pet.domain.meeting.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import petPeople.pet.common.BaseControllerTest;
import petPeople.pet.domain.meeting.entity.*;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.member.repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class MeetingPostRepositoryTest extends BaseControllerTest {


    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MeetingRepository meetingRepository;
    @Autowired
    MeetingPostRepository meetingPostRepository;

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

    @DisplayName("모임 ID 로 모임 게시글 페이징 조회")
    @Test
    public void findAllSliceByMeetingIdTest() throws Exception {
        //given
        int size = 3;
        PageRequest pageRequest1 = PageRequest.of(0, size);
        PageRequest pageRequest2 = PageRequest.of(1, size);

        Member saveMember1 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Meeting saveMeeting1 = meetingRepository.save(createMeeting(saveMember1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        Member saveMember2 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Meeting saveMeeting2 = meetingRepository.save(createMeeting(saveMember2, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        String postTitle = "모임 게시글 제목";
        String postContent = "모임 게시글 본문";

        MeetingPost saveMeetingPost1 = meetingPostRepository.save(createMeetingPost(saveMember1, saveMeeting1, postTitle, postContent));
        MeetingPost saveMeetingPost2 = meetingPostRepository.save(createMeetingPost(saveMember1, saveMeeting1, postTitle, postContent));
        MeetingPost saveMeetingPost3 = meetingPostRepository.save(createMeetingPost(saveMember1, saveMeeting1, postTitle, postContent));
        MeetingPost saveMeetingPost4 = meetingPostRepository.save(createMeetingPost(saveMember1, saveMeeting1, postTitle, postContent));
        MeetingPost saveMeetingPost5 = meetingPostRepository.save(createMeetingPost(saveMember1, saveMeeting1, postTitle, postContent));
        MeetingPost saveMeetingPost6 = meetingPostRepository.save(createMeetingPost(saveMember1, saveMeeting1, postTitle, postContent));

        MeetingPost saveMeetingPost7 = meetingPostRepository.save(createMeetingPost(saveMember2, saveMeeting2, postTitle, postContent));
        MeetingPost saveMeetingPost8 = meetingPostRepository.save(createMeetingPost(saveMember2, saveMeeting2, postTitle, postContent));
        MeetingPost saveMeetingPost9 = meetingPostRepository.save(createMeetingPost(saveMember2, saveMeeting2, postTitle, postContent));
        MeetingPost saveMeetingPost10 = meetingPostRepository.save(createMeetingPost(saveMember2, saveMeeting2, postTitle, postContent));
        MeetingPost saveMeetingPost11 = meetingPostRepository.save(createMeetingPost(saveMember2, saveMeeting2, postTitle, postContent));
        MeetingPost saveMeetingPost12 = meetingPostRepository.save(createMeetingPost(saveMember2, saveMeeting2, postTitle, postContent));

        //when
        Slice<MeetingPost> meetingPostSlice1 = meetingPostRepository.findAllSliceByMeetingId(saveMeeting1.getId(), pageRequest1);
        Slice<MeetingPost> meetingPostSlice2 = meetingPostRepository.findAllSliceByMeetingId(saveMeeting1.getId(), pageRequest2);

        Slice<MeetingPost> meetingPostSlice3 = meetingPostRepository.findAllSliceByMeetingId(saveMeeting2.getId(), pageRequest1);
        Slice<MeetingPost> meetingPostSlice4 = meetingPostRepository.findAllSliceByMeetingId(saveMeeting2.getId(), pageRequest2);

        //then
        assertThat(meetingPostSlice1.getContent()).contains(saveMeetingPost6, saveMeetingPost5, saveMeetingPost4);
        assertThat(meetingPostSlice2.getContent()).contains(saveMeetingPost3, saveMeetingPost2, saveMeetingPost1);

        assertThat(meetingPostSlice3.getContent()).contains(saveMeetingPost12, saveMeetingPost11, saveMeetingPost10);
        assertThat(meetingPostSlice4.getContent()).contains(saveMeetingPost9, saveMeetingPost8, saveMeetingPost7);

    }

    @DisplayName("회원 ID 로 모임 게시글 페이징 조회")
    @Test
    public void findAllSliceByMemberIdTest() throws Exception {
        //given
        int size = 3;
        PageRequest pageRequest1 = PageRequest.of(0, size);
        PageRequest pageRequest2 = PageRequest.of(1, size);

        Member saveMember1 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Meeting saveMeeting1 = meetingRepository.save(createMeeting(saveMember1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        Member saveMember2 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Meeting saveMeeting2 = meetingRepository.save(createMeeting(saveMember2, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        String postTitle = "모임 게시글 제목";
        String postContent = "모임 게시글 본문";

        MeetingPost saveMeetingPost1 = meetingPostRepository.save(createMeetingPost(saveMember1, saveMeeting1, postTitle, postContent));
        MeetingPost saveMeetingPost2 = meetingPostRepository.save(createMeetingPost(saveMember1, saveMeeting1, postTitle, postContent));
        MeetingPost saveMeetingPost3 = meetingPostRepository.save(createMeetingPost(saveMember1, saveMeeting1, postTitle, postContent));
        MeetingPost saveMeetingPost4 = meetingPostRepository.save(createMeetingPost(saveMember1, saveMeeting1, postTitle, postContent));
        MeetingPost saveMeetingPost5 = meetingPostRepository.save(createMeetingPost(saveMember1, saveMeeting1, postTitle, postContent));
        MeetingPost saveMeetingPost6 = meetingPostRepository.save(createMeetingPost(saveMember1, saveMeeting1, postTitle, postContent));

        MeetingPost saveMeetingPost7 = meetingPostRepository.save(createMeetingPost(saveMember2, saveMeeting2, postTitle, postContent));
        MeetingPost saveMeetingPost8 = meetingPostRepository.save(createMeetingPost(saveMember2, saveMeeting2, postTitle, postContent));
        MeetingPost saveMeetingPost9 = meetingPostRepository.save(createMeetingPost(saveMember2, saveMeeting2, postTitle, postContent));
        MeetingPost saveMeetingPost10 = meetingPostRepository.save(createMeetingPost(saveMember2, saveMeeting2, postTitle, postContent));
        MeetingPost saveMeetingPost11 = meetingPostRepository.save(createMeetingPost(saveMember2, saveMeeting2, postTitle, postContent));
        MeetingPost saveMeetingPost12 = meetingPostRepository.save(createMeetingPost(saveMember2, saveMeeting2, postTitle, postContent));

        //when
        Slice<MeetingPost> meetingPostSlice1 = meetingPostRepository.findAllSliceByMemberId(pageRequest1, saveMember1.getId());
        Slice<MeetingPost> meetingPostSlice2 = meetingPostRepository.findAllSliceByMemberId(pageRequest2, saveMember1.getId());

        Slice<MeetingPost> meetingPostSlice3 = meetingPostRepository.findAllSliceByMemberId(pageRequest1, saveMember2.getId());
        Slice<MeetingPost> meetingPostSlice4 = meetingPostRepository.findAllSliceByMemberId(pageRequest2, saveMember2.getId());

        //then
        assertThat(meetingPostSlice1.getContent()).contains(saveMeetingPost6, saveMeetingPost5, saveMeetingPost4);
        assertThat(meetingPostSlice2.getContent()).contains(saveMeetingPost3, saveMeetingPost2, saveMeetingPost1);

        assertThat(meetingPostSlice3.getContent()).contains(saveMeetingPost12, saveMeetingPost11, saveMeetingPost10);
        assertThat(meetingPostSlice4.getContent()).contains(saveMeetingPost9, saveMeetingPost8, saveMeetingPost7);
    }

    private MeetingPost createMeetingPost(Member saveMember, Meeting saveMeeting, String postTitle, String postContent) {
        return MeetingPost.builder()
                .meeting(saveMeeting)
                .member(saveMember)
                .title(postTitle)
                .content(postContent)
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