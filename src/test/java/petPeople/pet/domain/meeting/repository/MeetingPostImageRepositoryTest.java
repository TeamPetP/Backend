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
import petPeople.pet.domain.meeting.repository.meeting_post_Image.MeetingPostImageRepository;
import petPeople.pet.domain.meeting.repository.meeting_post.MeetingPostRepository;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.member.repository.MemberRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class MeetingPostImageRepositoryTest extends BaseControllerTest {


    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MeetingRepository meetingRepository;
    @Autowired
    MeetingPostRepository meetingPostRepository;
    @Autowired
    MeetingPostImageRepository meetingPostImageRepository;

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

    final String postTitle = "모임 게시글 제목";
    final String postContent = "모임 게시글 본문";

    final String meetingPostImageUrl = "www.meetingPostImageUrl.com";

    @DisplayName("meetingPost ID 로 meetingPostImage 조회")
    @Test
    public void findAllMeetingPostImageByMeetingPostIdTest() throws Exception {
        //given


        Member saveMember = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Meeting saveMeeting = meetingRepository.save(createMeeting(saveMember, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        MeetingPost saveMeetingPost = meetingPostRepository.save(createMeetingPost(saveMember, saveMeeting, postTitle, postContent));


        MeetingPostImage saveMeetingPostImage1 = meetingPostImageRepository.save(createMeetingPostImage(saveMember, saveMeetingPost, meetingPostImageUrl+1));
        MeetingPostImage saveMeetingPostImage2 = meetingPostImageRepository.save(createMeetingPostImage(saveMember, saveMeetingPost, meetingPostImageUrl+2));
        MeetingPostImage saveMeetingPostImage3 = meetingPostImageRepository.save(createMeetingPostImage(saveMember, saveMeetingPost, meetingPostImageUrl+3));
        MeetingPostImage saveMeetingPostImage4 = meetingPostImageRepository.save(createMeetingPostImage(saveMember, saveMeetingPost, meetingPostImageUrl+4));

        //when
        List<MeetingPostImage> meetingPostImageList = meetingPostImageRepository.findAllMeetingPostImageByMeetingPostId(saveMeetingPost.getId());

        //then
        assertThat(meetingPostImageList).contains(saveMeetingPostImage1, saveMeetingPostImage2, saveMeetingPostImage3, saveMeetingPostImage4);
    }

    @DisplayName("post 의 여러 ID 로 meetingPostImage 조회")
    @Test
    public void findAllByMeetingPostIdsTest() throws Exception {
        //given
        Member saveMember = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Meeting saveMeeting = meetingRepository.save(createMeeting(saveMember, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        List<Long> idList = new ArrayList<>();
        MeetingPost saveMeetingPost1 = meetingPostRepository.save(createMeetingPost(saveMember, saveMeeting, postTitle+1, postContent+1));
        MeetingPost saveMeetingPost2 = meetingPostRepository.save(createMeetingPost(saveMember, saveMeeting, postTitle+2, postContent+2));
        MeetingPost saveMeetingPost3 = meetingPostRepository.save(createMeetingPost(saveMember, saveMeeting, postTitle+3, postContent+3));

        idList.add(saveMeetingPost1.getId());
        idList.add(saveMeetingPost2.getId());
        idList.add(saveMeetingPost3.getId());

        MeetingPostImage saveMeetingPostImage1 = meetingPostImageRepository.save(createMeetingPostImage(saveMember, saveMeetingPost1, meetingPostImageUrl+1));
        MeetingPostImage saveMeetingPostImage2 = meetingPostImageRepository.save(createMeetingPostImage(saveMember, saveMeetingPost2, meetingPostImageUrl+1));
        MeetingPostImage saveMeetingPostImage3 = meetingPostImageRepository.save(createMeetingPostImage(saveMember, saveMeetingPost3, meetingPostImageUrl+1));
        
        //when
        List<MeetingPostImage> postImageList = meetingPostImageRepository.findAllByMeetingPostIds(idList);

        //then
        assertThat(postImageList).contains(saveMeetingPostImage1, saveMeetingPostImage2, saveMeetingPostImage3);
    }

    @DisplayName("meeting 의 ID 로 meetingPostImage 조회")
    @Test
    public void findAllByMeetingIdTest() throws Exception {
        //given
        Member saveMember1 = memberRepository.save(createMember(uid+1, email, name, nickname, imgUrl, introduce));
        Meeting saveMeeting1 = meetingRepository.save(createMeeting(saveMember1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));
        Member saveMember2 = memberRepository.save(createMember(uid+1, email, name, nickname, imgUrl, introduce));
        Meeting saveMeeting2 = meetingRepository.save(createMeeting(saveMember1, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        MeetingPost saveMeetingPost1 = meetingPostRepository.save(createMeetingPost(saveMember1, saveMeeting1, postTitle+1, postContent+1));
        MeetingPost saveMeetingPost2 = meetingPostRepository.save(createMeetingPost(saveMember2, saveMeeting2, postTitle+1, postContent+1));

        MeetingPostImage saveMeetingPostImage1 = meetingPostImageRepository.save(createMeetingPostImage(saveMember1, saveMeetingPost1, meetingPostImageUrl+1));
        MeetingPostImage saveMeetingPostImage2 = meetingPostImageRepository.save(createMeetingPostImage(saveMember1, saveMeetingPost1, meetingPostImageUrl+1));
        MeetingPostImage saveMeetingPostImage3 = meetingPostImageRepository.save(createMeetingPostImage(saveMember1, saveMeetingPost1, meetingPostImageUrl+1));

        MeetingPostImage saveMeetingPostImage4 = meetingPostImageRepository.save(createMeetingPostImage(saveMember2, saveMeetingPost2, meetingPostImageUrl+1));
        MeetingPostImage saveMeetingPostImage5 = meetingPostImageRepository.save(createMeetingPostImage(saveMember2, saveMeetingPost2, meetingPostImageUrl+1));
        MeetingPostImage saveMeetingPostImage6 = meetingPostImageRepository.save(createMeetingPostImage(saveMember2, saveMeetingPost2, meetingPostImageUrl+1));

        //when
        List<MeetingPostImage> postImageList1 = meetingPostImageRepository.findAllByMeetingId(saveMeeting1.getId());
        List<MeetingPostImage> postImageList2 = meetingPostImageRepository.findAllByMeetingId(saveMeeting2.getId());

        //then
        assertThat(postImageList1).contains(saveMeetingPostImage1, saveMeetingPostImage2, saveMeetingPostImage3);
        assertThat(postImageList2).contains(saveMeetingPostImage4, saveMeetingPostImage5, saveMeetingPostImage6);
    }
        
    @DisplayName("meetingPost ID 로 meetingPostImage 삭제")
    @Test
    public void deleteByMeetingPostIdTest() throws Exception {
        //given
        Member saveMember = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Meeting saveMeeting = meetingRepository.save(createMeeting(saveMember, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople));

        MeetingPost saveMeetingPost1 = meetingPostRepository.save(createMeetingPost(saveMember, saveMeeting, postTitle+1, postContent+1));
        MeetingPost saveMeetingPost2 = meetingPostRepository.save(createMeetingPost(saveMember, saveMeeting, postTitle+2, postContent+2));
        MeetingPost saveMeetingPost3 = meetingPostRepository.save(createMeetingPost(saveMember, saveMeeting, postTitle+3, postContent+3));

        meetingPostImageRepository.save(createMeetingPostImage(saveMember, saveMeetingPost1, meetingPostImageUrl+1));
        meetingPostImageRepository.save(createMeetingPostImage(saveMember, saveMeetingPost1, meetingPostImageUrl+2));
        meetingPostImageRepository.save(createMeetingPostImage(saveMember, saveMeetingPost1, meetingPostImageUrl+3));

        meetingPostImageRepository.save(createMeetingPostImage(saveMember, saveMeetingPost2, meetingPostImageUrl+4));
        meetingPostImageRepository.save(createMeetingPostImage(saveMember, saveMeetingPost2, meetingPostImageUrl+5));
        meetingPostImageRepository.save(createMeetingPostImage(saveMember, saveMeetingPost2, meetingPostImageUrl+6));

        meetingPostImageRepository.save(createMeetingPostImage(saveMember, saveMeetingPost3, meetingPostImageUrl+7));
        meetingPostImageRepository.save(createMeetingPostImage(saveMember, saveMeetingPost3, meetingPostImageUrl+8));
        meetingPostImageRepository.save(createMeetingPostImage(saveMember, saveMeetingPost3, meetingPostImageUrl+9));

        //when
        Long count1 = meetingPostImageRepository.deleteByMeetingPostId(saveMeetingPost1.getId());
        Long count2 = meetingPostImageRepository.deleteByMeetingPostId(saveMeetingPost2.getId());
        Long count3 = meetingPostImageRepository.deleteByMeetingPostId(saveMeetingPost3.getId());

        List<MeetingPostImage> meetingPostImageList = meetingPostImageRepository.findAllByMeetingPostIds(Arrays.asList(saveMeetingPost1.getId(), saveMeetingPost2.getId(), saveMeetingPost3.getId()));

        //then`
        assertThat(meetingPostImageList.isEmpty()).isTrue();

        assertThat(count1).isEqualTo(3);
        assertThat(count2).isEqualTo(3);
        assertThat(count3).isEqualTo(3);
    }

    private MeetingPostImage createMeetingPostImage(Member saveMember, MeetingPost saveMeetingPost, String imgUrl) {
        return MeetingPostImage.builder()
                .meetingPost(saveMeetingPost)
                .member(saveMember)
                .imgUrl(imgUrl)
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