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
import petPeople.pet.domain.meeting.repository.meeting_post.MeetingPostRepository;
import petPeople.pet.domain.meeting.repository.meeting_post_like.MeetingPostLikeRepository;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.member.repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class MeetingPostLikeRepositoryTest extends BaseControllerTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MeetingRepository meetingRepository;
    @Autowired
    MeetingPostRepository meetingPostRepository;
    @Autowired
    MeetingPostLikeRepository meetingPostLikeRepository;

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

    @DisplayName("member ID 와 meetingPost ID 를 로 postLike 조회")
    @Test
    public void findByMemberIdAndMeetingPostIdTest() throws Exception {

        //given
        String postTitle = "모임 게시글 제목";
        String postContent = "모임 게시글 본문";

        Member saveMember = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Meeting saveMeeting = meetingRepository.save(createMeeting(saveMember, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        MeetingPost saveMeetingPost1 = meetingPostRepository.save(createMeetingPost(saveMember, saveMeeting, postTitle, postContent));
        MeetingPost saveMeetingPost2 = meetingPostRepository.save(createMeetingPost(saveMember, saveMeeting, postTitle, postContent));
        MeetingPost saveMeetingPost3 = meetingPostRepository.save(createMeetingPost(saveMember, saveMeeting, postTitle, postContent));

        MeetingPostLike saveMeetingPostLike1 = meetingPostLikeRepository.save(createMeetingPostLike(saveMember, saveMeetingPost1));
        MeetingPostLike saveMeetingPostLike2 = meetingPostLikeRepository.save(createMeetingPostLike(saveMember, saveMeetingPost2));
        MeetingPostLike saveMeetingPostLike3 = meetingPostLikeRepository.save(createMeetingPostLike(saveMember, saveMeetingPost3));

        //when
        Optional<MeetingPostLike> postLikeOptional1 = meetingPostLikeRepository.findByMemberIdAndMeetingPostId(saveMember.getId(), saveMeetingPost1.getId());
        Optional<MeetingPostLike> postLikeOptional2 = meetingPostLikeRepository.findByMemberIdAndMeetingPostId(saveMember.getId(), saveMeetingPost2.getId());
        Optional<MeetingPostLike> postLikeOptional3 = meetingPostLikeRepository.findByMemberIdAndMeetingPostId(saveMember.getId(), saveMeetingPost3.getId());

        //then
        assertThat(postLikeOptional1.get()).isEqualTo(saveMeetingPostLike1);
        assertThat(postLikeOptional2.get()).isEqualTo(saveMeetingPostLike2);
        assertThat(postLikeOptional3.get()).isEqualTo(saveMeetingPostLike3);
    }

    @DisplayName("meetingPostId 의 여러 ID 로 meetingPostLike 조회")
    @Test
    public void findByMeetingPostIdsTest() throws Exception {
        //given
        String postTitle = "모임 게시글 제목";
        String postContent = "모임 게시글 본문";

        Member saveMember = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Meeting saveMeeting = meetingRepository.save(createMeeting(saveMember, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        List<Long> idList = new ArrayList<>();
        MeetingPost saveMeetingPost1 = meetingPostRepository.save(createMeetingPost(saveMember, saveMeeting, postTitle, postContent));
        MeetingPost saveMeetingPost2 = meetingPostRepository.save(createMeetingPost(saveMember, saveMeeting, postTitle, postContent));
        MeetingPost saveMeetingPost3 = meetingPostRepository.save(createMeetingPost(saveMember, saveMeeting, postTitle, postContent));
        idList.add(saveMeetingPost1.getId());
        idList.add(saveMeetingPost2.getId());
        idList.add(saveMeetingPost3.getId());


        MeetingPostLike saveMeetingPostLike1 = meetingPostLikeRepository.save(createMeetingPostLike(saveMember, saveMeetingPost1));
        MeetingPostLike saveMeetingPostLike2 = meetingPostLikeRepository.save(createMeetingPostLike(saveMember, saveMeetingPost2));
        MeetingPostLike saveMeetingPostLike3 = meetingPostLikeRepository.save(createMeetingPostLike(saveMember, saveMeetingPost3));

        //when
        List<MeetingPostLike> meetingPostLikeList = meetingPostLikeRepository.findByMeetingPostIds(idList);

        //then
        assertThat(meetingPostLikeList).contains(saveMeetingPostLike1, saveMeetingPostLike2, saveMeetingPostLike3);
    }

    @DisplayName("meetingPostId ID 와 member Id 로 meetingPostLike 삭제")
    @Test
    public void deleteByMemberIdAndMeetingPostIdTest() throws Exception {
        //given
        String postTitle = "모임 게시글 제목";
        String postContent = "모임 게시글 본문";

        Member saveMember1 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Meeting saveMeeting = meetingRepository.save(createMeeting(saveMember1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        MeetingPost saveMeetingPost1 = meetingPostRepository.save(createMeetingPost(saveMember1, saveMeeting, postTitle, postContent));
        MeetingPost saveMeetingPost2 = meetingPostRepository.save(createMeetingPost(saveMember1, saveMeeting, postTitle, postContent));
        MeetingPost saveMeetingPost3 = meetingPostRepository.save(createMeetingPost(saveMember1, saveMeeting, postTitle, postContent));

        meetingPostLikeRepository.save(createMeetingPostLike(saveMember1, saveMeetingPost1));
        meetingPostLikeRepository.save(createMeetingPostLike(saveMember1, saveMeetingPost2));
        meetingPostLikeRepository.save(createMeetingPostLike(saveMember1, saveMeetingPost3));

        //when
        meetingPostLikeRepository.deleteByMemberIdAndMeetingPostId(saveMember1.getId(), saveMeetingPost1.getId());
        meetingPostLikeRepository.deleteByMemberIdAndMeetingPostId(saveMember1.getId(), saveMeetingPost2.getId());
        meetingPostLikeRepository.deleteByMemberIdAndMeetingPostId(saveMember1.getId(), saveMeetingPost3.getId());

        Optional<MeetingPostLike> postLikeOptional1 = meetingPostLikeRepository.findByMemberIdAndMeetingPostId(saveMember1.getId(), saveMeetingPost1.getId());
        Optional<MeetingPostLike> postLikeOptional2 = meetingPostLikeRepository.findByMemberIdAndMeetingPostId(saveMember1.getId(), saveMeetingPost2.getId());
        Optional<MeetingPostLike> postLikeOptional3 = meetingPostLikeRepository.findByMemberIdAndMeetingPostId(saveMember1.getId(), saveMeetingPost3.getId());

        //then
        assertThat(postLikeOptional1.isEmpty()).isTrue();
        assertThat(postLikeOptional2.isEmpty()).isTrue();
        assertThat(postLikeOptional3.isEmpty()).isTrue();

    }
        
    @DisplayName("meetingPost ID 로 meetingPostLike 삭제")
    @Test
    public void deleteByMeetingPostIdTest() throws Exception {
        //given
        String postTitle = "모임 게시글 제목";
        String postContent = "모임 게시글 본문";

        Member saveMember1 = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Meeting saveMeeting = meetingRepository.save(createMeeting(saveMember1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        List<Long> idList = new ArrayList<>();
        MeetingPost saveMeetingPost1 = meetingPostRepository.save(createMeetingPost(saveMember1, saveMeeting, postTitle, postContent));
        MeetingPost saveMeetingPost2 = meetingPostRepository.save(createMeetingPost(saveMember1, saveMeeting, postTitle, postContent));
        MeetingPost saveMeetingPost3 = meetingPostRepository.save(createMeetingPost(saveMember1, saveMeeting, postTitle, postContent));
        idList.add(saveMeetingPost1.getId());
        idList.add(saveMeetingPost2.getId());
        idList.add(saveMeetingPost3.getId());

        meetingPostLikeRepository.save(createMeetingPostLike(saveMember1, saveMeetingPost1));
        meetingPostLikeRepository.save(createMeetingPostLike(saveMember1, saveMeetingPost1));
        meetingPostLikeRepository.save(createMeetingPostLike(saveMember1, saveMeetingPost1));

        //when
        Long count = meetingPostLikeRepository.deleteByMeetingPostId(saveMeetingPost1.getId());
        List<MeetingPostLike> meetingPostLikeList = meetingPostLikeRepository.findByMeetingPostIds(idList);

        //then
        assertThat(count).isEqualTo(3L);
        assertThat(meetingPostLikeList).isEmpty();

    }

    private MeetingPostLike createMeetingPostLike(Member saveMember, MeetingPost saveMeetingPost) {
        return MeetingPostLike
                .builder()
                .meetingPost(saveMeetingPost)
                .member(saveMember)
                .build();
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