package petPeople.pet.domain.meeting.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import petPeople.pet.controller.meeting.dto.req.MeetingCreateReqDto;
import petPeople.pet.controller.meeting.dto.req.MeetingEditReqDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingCreateRespDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingEditRespDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingRetrieveRespDto;
import petPeople.pet.controller.post.model.MeetingParameter;
import petPeople.pet.domain.meeting.entity.*;
import petPeople.pet.domain.meeting.entity.vo.Category;
import petPeople.pet.domain.meeting.entity.vo.MeetingType;
import petPeople.pet.domain.meeting.entity.vo.Sex;
import petPeople.pet.domain.meeting.repository.meeting_bookmark.MeetingBookmarkRepository;
import petPeople.pet.domain.meeting.repository.meeting_image.MeetingImageRepository;
import petPeople.pet.domain.meeting.repository.meeting_member.MeetingMemberRepository;
import petPeople.pet.domain.meeting.repository.meeting.MeetingRepository;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.exception.CustomException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeetingServiceTest {

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
    MeetingRepository meetingRepository;

    @Mock
    MeetingImageRepository meetingImageRepository;
    @Mock
    MeetingMemberRepository meetingMemberRepository;
    @Mock
    MeetingBookmarkRepository meetingBookmarkRepository;
    @InjectMocks
    MeetingService meetingService;

    @Test
    @DisplayName("미팅 생성 테스트")
    public void createMeetingTest() throws Exception {
//        given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Meeting meeting = createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople);

        when(meetingRepository.save(any())).thenReturn(meeting);
        when(meetingMemberRepository.save(any())).thenReturn(createMeetingMember(member, meeting));

        List<String> imgUrlList = List.of("meetingImage1", "meetingImage2", "meetingImage3", "meetingImage4");

        MeetingImage meetingImage1 = createMeetingImage(meeting, imgUrlList.get(0));
        MeetingImage meetingImage2 = createMeetingImage(meeting, imgUrlList.get(1));
        MeetingImage meetingImage3 = createMeetingImage(meeting, imgUrlList.get(2));
        MeetingImage meetingImage4 = createMeetingImage(meeting, imgUrlList.get(3));

        when(meetingImageRepository.save(any()))
                .thenReturn(meetingImage1)
                .thenReturn(meetingImage2)
                .thenReturn(meetingImage3)
                .thenReturn(meetingImage4);

        List<MeetingImage> meetingImageList = Arrays.asList(meetingImage1, meetingImage2, meetingImage3, meetingImage4);

        MeetingCreateRespDto result = new MeetingCreateRespDto(meeting, meetingImageList);
        MeetingCreateReqDto meetingCreateReqDto = createMeetingCreateReqDto(title, content, doName, sigungu, sex, category, conditions, maxPeople, imgUrlList);

        //when
        MeetingCreateRespDto expected = meetingService.create(member, meetingCreateReqDto);

        //then
        assertThat(expected).isEqualTo(result);
    }

    @Test
    @DisplayName("미팅 단건 조회")
    public void retrieveMeetingTest() throws Exception {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Meeting meeting = createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople);

        MeetingImage meetingImage1 = createMeetingImage(meeting, "meetingImage1");
        MeetingImage meetingImage2 = createMeetingImage(meeting, "meetingImage2");
        MeetingImage meetingImage3 = createMeetingImage(meeting, "meetingImage3");
        MeetingImage meetingImage4 = createMeetingImage(meeting, "meetingImage4");

        List<MeetingImage> meetingImageList = Arrays.asList(meetingImage1, meetingImage2, meetingImage3, meetingImage4);

        MeetingMember meetingMember1 = createMeetingMember(member, meeting);
        MeetingMember meetingMember2 = createMeetingMember(member, meeting);
        MeetingMember meetingMember3 = createMeetingMember(member, meeting);

        List<MeetingMember> meetingMemberList = Arrays.asList(meetingMember1, meetingMember2, meetingMember3);

        when(meetingRepository.findById(any())).thenReturn(Optional.ofNullable(meeting));
        when(meetingImageRepository.findByMeetingId(any())).thenReturn(meetingImageList);
        when(meetingMemberRepository.findByMeetingId(any())).thenReturn(meetingMemberList);

        MeetingRetrieveRespDto result = new MeetingRetrieveRespDto(meeting, meetingImageList, meetingMemberList, null, null);

        //when
        MeetingRetrieveRespDto respDto = meetingService.localRetrieveOne(meeting.getId(), Optional.empty());

        //then
        assertThat(respDto).isEqualTo(result);
    }

    @Test
    @DisplayName("존재 하지 않는 미팅 단건 조회")
    public void retrieveNotFoundMeetingTest() throws Exception {
        //given
        when(meetingRepository.findById(any())).thenReturn(Optional.empty());

        //when
        //then
        Assertions.assertThrows(CustomException.class, () -> meetingService.localRetrieveOne(any(), Optional.empty()));
    }

    @Test
    @DisplayName("미팅 전체 조회")
    public void retrieveAllMeetingTest() throws Exception {
        //given
        PageRequest pageRequest = PageRequest.of(0, 10);

        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);

        Meeting meeting1 = createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople);
        List<MeetingImage> meetingImageList1 = Arrays.asList(createMeetingImage(meeting1, "meetingImage1"));
        List<MeetingMember> meetingMemberList1 = Arrays.asList(createMeetingMember(member, meeting1));

        Meeting meeting2 = createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople);
        List<MeetingImage> meetingImageList2 = Arrays.asList(createMeetingImage(meeting2, "meetingImage1"));
        List<MeetingMember> meetingMemberList2 = Arrays.asList(createMeetingMember(member, meeting2));

        Meeting meeting3 = createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople);
        List<MeetingImage> meetingImageList3 = Arrays.asList(createMeetingImage(meeting3, "meetingImage1"));
        List<MeetingMember> meetingMemberList3 = Arrays.asList(createMeetingMember(member, meeting3));

        MeetingRetrieveRespDto result1 = new MeetingRetrieveRespDto(meeting1, meetingImageList1, meetingMemberList1, null, null);
        MeetingRetrieveRespDto result2 = new MeetingRetrieveRespDto(meeting2, meetingImageList2, meetingMemberList2, null, null);
        MeetingRetrieveRespDto result3 = new MeetingRetrieveRespDto(meeting3, meetingImageList3, meetingMemberList3, null, null);

        List<Meeting> meetingList = Arrays.asList(meeting1, meeting2, meeting3);
        List<MeetingRetrieveRespDto> content = Arrays.asList(result1, result2, result3);

        SliceImpl<Meeting> meetingSlice = new SliceImpl<>(meetingList, pageRequest, false);
        SliceImpl<MeetingRetrieveRespDto> result = new SliceImpl<>(content, pageRequest, false);

        List<MeetingImage> meetingImageList = new ArrayList<>();
        meetingImageList.addAll(meetingImageList1);
        meetingImageList.addAll(meetingImageList2);
        meetingImageList.addAll(meetingImageList3);

        List<MeetingMember> meetingMemberList = new ArrayList<>();
        meetingMemberList.addAll(meetingMemberList1);
        meetingMemberList.addAll(meetingMemberList2);
        meetingMemberList.addAll(meetingMemberList3);

        when(meetingRepository.findAllSlicingWithFetchJoinMember(any(), any())).thenReturn(meetingSlice);
        when(meetingImageRepository.findByMeetingIds(any())).thenReturn(meetingImageList);
        when(meetingMemberRepository.findByMeetingIds(any())).thenReturn(meetingMemberList);

        //when
        Slice<MeetingRetrieveRespDto> respDtoSlice = meetingService.localRetrieveAll(pageRequest, Optional.empty(), new MeetingParameter());

        //then
        assertThat(respDtoSlice).isEqualTo(result);
    }

    @Test
    @DisplayName("미팅 수정 테스트")
    public void meetingEditTest() throws Exception {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Meeting editMeeting = createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople);

        List<String> meetingImageStrList = List.of("meetingImage1", "meetingImage2", "meetingImage3", "meetingImage4");

        MeetingImage meetingImage1 = createMeetingImage(editMeeting, meetingImageStrList.get(0));
        MeetingImage meetingImage2 = createMeetingImage(editMeeting, meetingImageStrList.get(1));
        MeetingImage meetingImage3 = createMeetingImage(editMeeting, meetingImageStrList.get(2));
        MeetingImage meetingImage4 = createMeetingImage(editMeeting, meetingImageStrList.get(3));

        List<MeetingImage> meetingImageList = Arrays.asList(meetingImage1, meetingImage2, meetingImage3, meetingImage4);

        when(meetingRepository.findById(any())).thenReturn(Optional.ofNullable(editMeeting));
        when(meetingImageRepository.deleteByMeetingId(any())).thenReturn(0L);
        when(meetingImageRepository.save(any()))
                .thenReturn(meetingImage1)
                .thenReturn(meetingImage2)
                .thenReturn(meetingImage3)
                .thenReturn(meetingImage4);

        String editTitle = "수정";
        String editContent = "수정 컨텐트";

        MeetingEditReqDto meetingEditReqDto = new MeetingEditReqDto(title, content, doName, sigungu, location, sex, conditions, category, meetingType, period, maxPeople, true, meetingImageStrList);
        meetingEditReqDto.setTitle(editTitle);
        meetingEditReqDto.setContent(editContent);

        MeetingEditRespDto result = new MeetingEditRespDto(editMeeting, meetingImageList);
        result.setTitle(editTitle);
        result.setContent(editContent);

        //when
        MeetingEditRespDto expected = meetingService.edit(member, editMeeting.getId(), meetingEditReqDto);

        //then
        assertThat(expected).isEqualTo(result);
    }

    @Test
    @DisplayName("회원 미팅 전체 조회 테스트")
    public void retrieveMemberMeetingTest() throws Exception {
        //given
        PageRequest pageRequest = PageRequest.of(0, 10);

        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);

        Meeting meeting1 = createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople);
        List<MeetingImage> meetingImageList1 = Arrays.asList(createMeetingImage(meeting1, "meetingImage1"));
        List<MeetingMember> meetingMemberList1 = Arrays.asList(createMeetingMember(member, meeting1));

        Meeting meeting2 = createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople);
        List<MeetingImage> meetingImageList2 = Arrays.asList(createMeetingImage(meeting2, "meetingImage1"));
        List<MeetingMember> meetingMemberList2 = Arrays.asList(createMeetingMember(member, meeting2));

        Meeting meeting3 = createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople);
        List<MeetingImage> meetingImageList3 = Arrays.asList(createMeetingImage(meeting3, "meetingImage1"));
        List<MeetingMember> meetingMemberList3 = Arrays.asList(createMeetingMember(member, meeting3));

        MeetingBookmark meetingBookmark1 = createMeetingBookmark(member, meeting1);
        MeetingBookmark meetingBookmark2 = createMeetingBookmark(member, meeting2);
        MeetingBookmark meetingBookmark3 = createMeetingBookmark(member, meeting3);

        MeetingRetrieveRespDto result1 = new MeetingRetrieveRespDto(meeting1, meetingImageList1, meetingMemberList1, true, true);
        MeetingRetrieveRespDto result2 = new MeetingRetrieveRespDto(meeting2, meetingImageList2, meetingMemberList2, true, true);
        MeetingRetrieveRespDto result3 = new MeetingRetrieveRespDto(meeting3, meetingImageList3, meetingMemberList3, true, true);

        List<Meeting> meetingList = Arrays.asList(meeting1, meeting2, meeting3);
        List<MeetingRetrieveRespDto> content = Arrays.asList(result1, result2, result3);

        SliceImpl<Meeting> meetingSlice = new SliceImpl<>(meetingList, pageRequest, false);
        SliceImpl<MeetingRetrieveRespDto> result = new SliceImpl<>(content, pageRequest, false);

        List<MeetingImage> meetingImageList = new ArrayList<>();
        meetingImageList.addAll(meetingImageList1);
        meetingImageList.addAll(meetingImageList2);
        meetingImageList.addAll(meetingImageList3);

        List<MeetingMember> meetingMemberList = new ArrayList<>();
        meetingMemberList.addAll(meetingMemberList1);
        meetingMemberList.addAll(meetingMemberList2);
        meetingMemberList.addAll(meetingMemberList3);

        when(meetingRepository.findAllSlicingByMemberId(any(), any())).thenReturn(meetingSlice);
        when(meetingImageRepository.findByMeetingIds(any())).thenReturn(meetingImageList);
        when(meetingMemberRepository.findByMeetingIds(any())).thenReturn(meetingMemberList);
        when(meetingBookmarkRepository.findByMemberIdAndMeetingId(any(), any()))
                .thenReturn(Optional.ofNullable(meetingBookmark1))
                .thenReturn(Optional.ofNullable(meetingBookmark2))
                .thenReturn(Optional.ofNullable(meetingBookmark3));

        //when
        Slice<MeetingRetrieveRespDto> respDtoSlice = meetingService.retrieveMemberMeeting(member, pageRequest);

        //then
        assertThat(respDtoSlice).isEqualTo(result);
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

//    private MeetingEditReqDto createMeetingEditReqDto() {
//        return MeetingEditReqDto.builder()
//                .title(title)
//                .content(content)
//                .doName(doName)
//                .sigungu(sigungu)
//                .sex(sex)
//                .category(category)
//                .conditions(conditions)
//                .maxPeople(maxPeople)
//                .imgUrlList(imgUrlList)
//                .build();
//    }

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


}