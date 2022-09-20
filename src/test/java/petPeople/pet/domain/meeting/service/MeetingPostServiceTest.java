package petPeople.pet.domain.meeting.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import petPeople.pet.controller.meeting.dto.req.MeetingCreateReqDto;
import petPeople.pet.controller.meeting.dto.req.MeetingPostWriteReqDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingPostRetrieveRespDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingPostWriteRespDto;
import petPeople.pet.domain.meeting.entity.*;
import petPeople.pet.domain.meeting.entity.vo.Category;
import petPeople.pet.domain.meeting.entity.vo.MeetingType;
import petPeople.pet.domain.meeting.entity.vo.Sex;
import petPeople.pet.domain.meeting.repository.meeting.MeetingRepository;
import petPeople.pet.domain.meeting.repository.meeting_comment.MeetingCommentRepository;
import petPeople.pet.domain.meeting.repository.meeting_member.MeetingMemberRepository;
import petPeople.pet.domain.meeting.repository.meeting_post.MeetingPostRepository;
import petPeople.pet.domain.meeting.repository.meeting_post_Image.MeetingPostImageRepository;
import petPeople.pet.domain.meeting.repository.meeting_post_like.MeetingPostLikeRepository;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.notification.repository.NotificationRepository;
import petPeople.pet.exception.CustomException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeetingPostServiceTest {

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

    @Mock
    MeetingPostRepository meetingPostRepository;
    @Mock
    MeetingMemberRepository meetingMemberRepository;
    @Mock
    MeetingPostImageRepository meetingPostImageRepository;
    @Mock
    MeetingCommentRepository meetingCommentRepository;
    @Mock
    MeetingRepository meetingRepository;
    @Mock
    MeetingPostLikeRepository meetingPostLikeRepository;

    @InjectMocks
    MeetingPostService meetingPostService;

    @Test
    public void 모임_게시글_작성() throws Exception {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Meeting meeting = createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople);
        MeetingMember meetingMember = createMeetingMember(member, meeting);

        Member member1 = createMember(uid, email, name, nickname, imgUrl, introduce);
        Member member2 = createMember(uid, email, name, nickname, imgUrl, introduce);
        Member member3 = createMember(uid, email, name, nickname, imgUrl, introduce);
        Member member4 = createMember(uid, email, name, nickname, imgUrl, introduce);

        MeetingMember meetingMember1 = createMeetingMember(member1, meeting);
        MeetingMember meetingMember2 = createMeetingMember(member2, meeting);
        MeetingMember meetingMember3 = createMeetingMember(member3, meeting);
        MeetingMember meetingMember4 = createMeetingMember(member4, meeting);

        List<MeetingMember> meetingMemberList = List.of(meetingMember, meetingMember1, meetingMember2, meetingMember3, meetingMember4);

        String meetingPostTitle = "모임 게시글 제목";
        String meetingPostContent = "모임 게시글 본문";

        List<String> imgUrlList = List.of("모임 이미지1", "모임 이미지2", "모임 이미지3");

        MeetingPost meetingPost = createMeetingPost(member1, meeting, meetingPostTitle+1, meetingPostContent+1);

        MeetingPostImage meetingPostImage1 = createMeetingPostImage(member, meetingPost, imgUrlList.get(0));
        MeetingPostImage meetingPostImage2 = createMeetingPostImage(member, meetingPost, imgUrlList.get(1));
        MeetingPostImage meetingPostImage3 = createMeetingPostImage(member, meetingPost, imgUrlList.get(2));

        List<MeetingPostImage> meetingPostImageList = List.of(meetingPostImage1, meetingPostImage2, meetingPostImage3);

        when(meetingMemberRepository.findByMeetingId(any())).thenReturn(meetingMemberList);
        when(meetingRepository.findById(any())).thenReturn(Optional.ofNullable(meeting));
        when(meetingPostRepository.save(any())).thenReturn(meetingPost);
        when(meetingPostImageRepository.save(any()))
                .thenReturn(meetingPostImage1)
                .thenReturn(meetingPostImage2)
                .thenReturn(meetingPostImage3);

        MeetingPostWriteRespDto result = new MeetingPostWriteRespDto(meetingPost, meetingPostImageList);

        //when
        MeetingPostWriteReqDto meetingPostWriteReqDto = new MeetingPostWriteReqDto(meetingPostTitle, meetingPostContent, imgUrlList);
        MeetingPostWriteRespDto expected = meetingPostService.write(member, meetingPostWriteReqDto, 1L);

        //then
        assertThat(expected).isEqualTo(result);
    }

    @Test
    public void 모임_게시글_단건_조회() throws Exception {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Meeting meeting = createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople);
        MeetingMember meetingMember = createMeetingMember(member, meeting);

        Member member1 = createMember(uid, email, name, nickname, imgUrl, introduce);
        Member member2 = createMember(uid, email, name, nickname, imgUrl, introduce);
        Member member3 = createMember(uid, email, name, nickname, imgUrl, introduce);
        Member member4 = createMember(uid, email, name, nickname, imgUrl, introduce);

        MeetingMember meetingMember1 = createMeetingMember(member1, meeting);
        MeetingMember meetingMember2 = createMeetingMember(member2, meeting);
        MeetingMember meetingMember3 = createMeetingMember(member3, meeting);
        MeetingMember meetingMember4 = createMeetingMember(member4, meeting);

        List<MeetingMember> meetingMemberList = List.of(meetingMember, meetingMember1, meetingMember2, meetingMember3, meetingMember4);

        String meetingPostTitle = "모임 게시글 제목";
        String meetingPostContent = "모임 게시글 본문";

        List<String> imgUrlList = List.of("모임 이미지1", "모임 이미지2", "모임 이미지3");

        MeetingPost meetingPost = createMeetingPost(member1, meeting, meetingPostTitle+1, meetingPostContent+1);

        MeetingPostImage meetingPostImage1 = createMeetingPostImage(member, meetingPost, imgUrlList.get(0));
        MeetingPostImage meetingPostImage2 = createMeetingPostImage(member, meetingPost, imgUrlList.get(1));
        MeetingPostImage meetingPostImage3 = createMeetingPostImage(member, meetingPost, imgUrlList.get(2));

        List<MeetingPostImage> meetingPostImageList = List.of(meetingPostImage1, meetingPostImage2, meetingPostImage3);

        long meetingPostLikeCount = 3L;

        MeetingPostLike meetingPostLike = createMeetingPostLike(member1, meetingPost);

        when(meetingMemberRepository.findByMeetingId(any())).thenReturn(meetingMemberList);
        when(meetingRepository.findById(any())).thenReturn(Optional.ofNullable(meeting));
        when(meetingPostRepository.findById(any())).thenReturn(Optional.ofNullable(meetingPost));
        when(meetingPostImageRepository.findAllMeetingPostImageByMeetingPostId(any())).thenReturn(meetingPostImageList);
        when(meetingPostLikeRepository.countByMeetingPostsId(any())).thenReturn(meetingPostLikeCount);
        when(meetingPostLikeRepository.findByMemberIdAndMeetingPostId(any(), any())).thenReturn(Optional.ofNullable(meetingPostLike));

        MeetingPostRetrieveRespDto result1 = new MeetingPostRetrieveRespDto(meetingPost, meetingPostImageList, meetingPostLikeCount, true);

        //when
        MeetingPostRetrieveRespDto expected1 = meetingPostService.retrieveOne(1L, 1L, member1);

        //then
        assertThat(expected1).isEqualTo(result1);

        //모임 회원이 아닌 회원이 조회할 경우
        assertThrows(CustomException.class, () -> meetingPostService.retrieveOne(1L, 1L, new Member()));
        
    }

    @Test
    public void 모임_게시글_전체_조회() throws Exception {
        //given
        Member member = createMember(uid+0, email, name, nickname, imgUrl, introduce);
        Meeting meeting = createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople);
        MeetingMember meetingMember = createMeetingMember(member, meeting);

        Member member1 = createMember(uid+1, email, name, nickname, imgUrl, introduce);
        Member member2 = createMember(uid+2, email, name, nickname, imgUrl, introduce);
        Member member3 = createMember(uid+3, email, name, nickname, imgUrl, introduce);

        MeetingMember meetingMember1 = createMeetingMember(member1, meeting);
        MeetingMember meetingMember2 = createMeetingMember(member2, meeting);
        MeetingMember meetingMember3 = createMeetingMember(member3, meeting);

        List<MeetingMember> meetingMemberList = List.of(meetingMember, meetingMember1, meetingMember2, meetingMember3);

        String meetingPostTitle = "모임 게시글 제목";
        String meetingPostContent = "모임 게시글 본문";

        MeetingPost meetingPost1 = createMeetingPost(member1, meeting, meetingPostTitle, meetingPostContent);
        MeetingPost meetingPost2 = createMeetingPost(member2, meeting, meetingPostTitle, meetingPostContent);
        MeetingPost meetingPost3 = createMeetingPost(member3, meeting, meetingPostTitle, meetingPostContent);

        List<MeetingPost> meetingPostList = List.of(meetingPost1, meetingPost2, meetingPost3);

        List<MeetingPostImage> meetingPostImageList = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            meetingPostImageList.add(createMeetingPostImage(member1, meetingPost1, "모임 이미지1"+i));
        }

        for (int i = 1; i <= 3; i++) {
            meetingPostImageList.add(createMeetingPostImage(member2, meetingPost2, "모임 이미지2"+i));
        }

        for (int i = 1; i <= 3; i++) {
            meetingPostImageList.add(createMeetingPostImage(member3, meetingPost3, "모임 이미지3"+i+i));
        }

        List<MeetingPostLike> meetingPostLikeList = List.of(
                createMeetingPostLike(member, meetingPost1),
                createMeetingPostLike(member1, meetingPost1),
                createMeetingPostLike(member2, meetingPost2),
                createMeetingPostLike(member3, meetingPost3));

        SliceImpl<MeetingPost> postSlice = new SliceImpl<>(meetingPostList);

        Slice<MeetingPostRetrieveRespDto> result = postSlice.map(meetingPost -> {
            List<MeetingPostImage> meetingPostImagesByMeetingPost = getMeetingPostImagesByMeetingPost(meetingPostImageList, meetingPost);
            List<MeetingPostLike> meetingPostLikesByMeetingPost = getMeetingPostLikesByMeetingPost(meetingPostLikeList, meetingPost);

            boolean isLiked = isMemberLikedMeetingPost(member, meetingPostLikesByMeetingPost);
            boolean isOwner = false;

            if (meetingPost.getMember() == member) {
                isOwner = true;
            }
            return new MeetingPostRetrieveRespDto(meetingPost, meetingPostImagesByMeetingPost, Long.valueOf(meetingPostLikesByMeetingPost.size()), isLiked, isOwner);
        });

        when(meetingMemberRepository.findByMeetingId(any())).thenReturn(meetingMemberList);
        when(meetingRepository.findById(any())).thenReturn(Optional.ofNullable(meeting));
        when(meetingPostRepository.findAllSliceByMeetingId(any(), any())).thenReturn(postSlice);
        when(meetingPostImageRepository.findAllByMeetingPostIds(any())).thenReturn(meetingPostImageList);
        when(meetingPostLikeRepository.findByMeetingPostIds(any())).thenReturn(meetingPostLikeList);

        //when
        Slice<MeetingPostRetrieveRespDto> expected = meetingPostService.retrieveAll(1L, any(), member);

        //then
        assertThat(expected).isEqualTo(result);
    }

    @Test
    public void 모임_게시글_좋아요() throws Exception {
        //given
        Member member = createMember(uid+0, email, name, nickname, imgUrl, introduce);
        Meeting meeting = createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople);
        MeetingMember meetingMember = createMeetingMember(member, meeting);

        Member member1 = createMember(uid+1, email, name, nickname, imgUrl, introduce);
        Member member2 = createMember(uid+2, email, name, nickname, imgUrl, introduce);
        Member member3 = createMember(uid+3, email, name, nickname, imgUrl, introduce);

        MeetingMember meetingMember1 = createMeetingMember(member1, meeting);
        MeetingMember meetingMember2 = createMeetingMember(member2, meeting);
        MeetingMember meetingMember3 = createMeetingMember(member3, meeting);

        List<MeetingMember> meetingMemberList = List.of(meetingMember, meetingMember1, meetingMember2, meetingMember3);

        String meetingPostTitle = "모임 게시글 제목";
        String meetingPostContent = "모임 게시글 본문";

        MeetingPost meetingPost = createMeetingPost(member1, meeting, meetingPostTitle, meetingPostContent);
        MeetingPostLike meetingPostLike = createMeetingPostLike(member1, meetingPost);

        long likeCnt = 3L;

        when(meetingMemberRepository.findByMeetingId(any())).thenReturn(meetingMemberList);
        when(meetingRepository.findById(any())).thenReturn(Optional.ofNullable(meeting));
        when(meetingPostRepository.findById(any())).thenReturn(Optional.ofNullable(meetingPost));
        when(meetingPostLikeRepository.findByMemberIdAndMeetingPostId(any(), any())).thenReturn(Optional.empty());
        when(meetingPostLikeRepository.save(any())).thenReturn(meetingPostLike);
        when(meetingPostLikeRepository.countByMeetingPostsId(any())).thenReturn(likeCnt);
        //when

        long expected = meetingPostService.like(1L, 1L, member1);

        //then
        assertThat(expected).isEqualTo(likeCnt);
    }

    @Test
    public void 모임_게시글_수정() throws Exception {
        //given
        Member member = createMember(uid+0, email, name, nickname, imgUrl, introduce);
        Meeting meeting = createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople);

        String meetingPostTitle = "모임 게시글 제목";
        String meetingPostContent = "모임 게시글 본문";

        MeetingPost meetingPost = createMeetingPost(member, meeting, meetingPostTitle, meetingPostContent);

        String editPostTitle = "수정 제목";
        String editPostContent = "수정 본문";

        String editImg1 = "수정 이미지1";
        String editImg2 = "수정 이미지2";
        String editImg3 = "수정 이미지3";

        List<String> editMeetingPostStrImageList = List.of(editImg1, editImg2, editImg3);

        MeetingPostWriteReqDto meetingPostWriteReqDto = new MeetingPostWriteReqDto(editPostTitle, editPostContent, editMeetingPostStrImageList);

        createMeetingPostImage(member, meetingPost, editImg2);
        createMeetingPostImage(member, meetingPost, editImg3);

        MeetingPostImage editMeetingPostImage1 = createMeetingPostImage(member, meetingPost, editImg1);
        MeetingPostImage editMeetingPostImage2 = createMeetingPostImage(member, meetingPost, editImg2);
        MeetingPostImage editMeetingPostImage3 = createMeetingPostImage(member, meetingPost, editImg3);

        List<MeetingPostImage> editMeetingPostImageList = List.of(
                editMeetingPostImage1,
                editMeetingPostImage2,
                editMeetingPostImage3
        );

        meetingPost.setTitle(editPostTitle);
        meetingPost.setContent(editPostContent);

        MeetingPostWriteRespDto result = new MeetingPostWriteRespDto(meetingPost, editMeetingPostImageList);

        when(meetingRepository.findById(any())).thenReturn(Optional.ofNullable(meeting));
        when(meetingPostRepository.findById(any())).thenReturn(Optional.ofNullable(meetingPost));
        when(meetingPostImageRepository.save(any()))
                .thenReturn(
                        editMeetingPostImage1,
                        editMeetingPostImage2,
                        editMeetingPostImage3
                );
        
        //when
        MeetingPostWriteRespDto expected = meetingPostService.edit(1L, 1L, meetingPostWriteReqDto, member);

        //then
        assertThat(expected).isEqualTo(result);
    }
    
    private boolean isMemberLikedMeetingPost(Member member, List<MeetingPostLike> meetingPostLikesByMeetingPost) {
        boolean isLiked = false;

        for (MeetingPostLike meetingPostLike : meetingPostLikesByMeetingPost) {
            if (meetingPostLike.getMember() == member) {
                isLiked = true;
                break;
            }
        }
        return isLiked;
    }

    private List<MeetingPostImage> getMeetingPostImagesByMeetingPost(List<MeetingPostImage> findMeetingPostImageList, MeetingPost meetingPost) {
        List<MeetingPostImage> meetingPostImageList = new ArrayList<>();
        for (MeetingPostImage meetingPostImage : findMeetingPostImageList) {
            if (meetingPostImage.getMeetingPost() == meetingPost) {
                meetingPostImageList.add(meetingPostImage);
            }
        }
        return meetingPostImageList;
    }

    private List<MeetingPostLike> getMeetingPostLikesByMeetingPost(List<MeetingPostLike> findMeetingPostLikeList, MeetingPost meetingPost) {
        List<MeetingPostLike> meetingPostLikeList = new ArrayList<>();
        for (MeetingPostLike meetingPostLike : findMeetingPostLikeList) {
            if (meetingPostLike.getMeetingPost() == meetingPost) {
                meetingPostLikeList.add(meetingPostLike);
            }
        }
        return meetingPostLikeList;
    }

    private MeetingPostImage createMeetingPostImage(Member member, MeetingPost meetingPost, String imgUrl) {
        return MeetingPostImage.builder()
                .meetingPost(meetingPost)
                .member(member)
                .imgUrl(imgUrl)
                .build();
    }

    private MeetingBookmark createMeetingBookmark(Member member, Meeting meeting) {
        return MeetingBookmark
                .builder()
                .meeting(meeting)
                .member(member)
                .build();
    }

    private MeetingMember createMeetingMember(Member member, Meeting meeting) {
        return MeetingMember.builder()
                .member(member)
                .meeting(meeting)
                .build();
    }

    private MeetingCreateReqDto createMeetingCreateReqDto(String title, String content, String doName, String sigungu, Sex sex, Category category, String conditions, Integer maxPeople, List<String> imgUrlList) {
        return MeetingCreateReqDto.builder()
                .title(title)
                .content(content)
                .doName(doName)
                .sigungu(sigungu)
                .sex(sex)
                .category(category)
                .conditions(conditions)
                .maxPeople(maxPeople)
                .imgUrlList(imgUrlList)
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

    private MeetingImage createMeetingImage(Meeting meeting, String meetingImgUrl) {
        return MeetingImage.builder()
                .meeting(meeting)
                .imgUrl(meetingImgUrl)
                .build();
    }

    private MeetingPost createMeetingPost(Member member, Meeting meeting, String postTitle, String postContent) {
        return MeetingPost.builder()
                .meeting(meeting)
                .member(member)
                .title(postTitle)
                .content(postContent)
                .build();
    }

    private MeetingPostLike createMeetingPostLike(Member member, MeetingPost meetingPost) {
        return MeetingPostLike
                .builder()
                .meetingPost(meetingPost)
                .member(member)
                .build();
    }

}