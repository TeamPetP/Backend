package petPeople.pet.domain.meeting.service;

import org.junit.jupiter.api.BeforeEach;
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
import petPeople.pet.domain.meeting.repository.MeetingImageRepository;
import petPeople.pet.domain.meeting.repository.MeetingMemberRepository;
import petPeople.pet.domain.meeting.repository.MeetingRepository;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.exception.CustomException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
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
    final List<String> imgUrlList = Arrays.asList("www.abc.com", "www.abc.com", "www.abc.com", "www.abc.com");
    final String period = "주 2회";
    final String 올림픽_공원 = "올림픽 공원";
    final MeetingType meetingType =MeetingType.REGULAR;


    Long id;
    Member member;
    Meeting meeting;


    @BeforeEach
    void beforeEach() {
        id = 1L;
        member = createMember(uid, email, name, nickname, imgUrl, introduce);
        meeting = createMeeting();
    }

    @Mock
    MeetingRepository meetingRepository;

    @Mock
    MeetingImageRepository meetingImageRepository;
    @Mock
    MeetingMemberRepository meetingMemberRepository;
    @InjectMocks
    MeetingService meetingService;

    @Test
    @DisplayName("미팅 생성 테스트")
    public void createMeetingTest() throws Exception {
//        given
        when(meetingRepository.save(any())).thenReturn(meeting);
        when(meetingMemberRepository.save(any())).thenReturn(createMeetingMember());
        MeetingImage meetingImage1 = new MeetingImage(++id, meeting, imgUrlList.get(0));
        MeetingImage meetingImage2 = new MeetingImage(++id, meeting, imgUrlList.get(1));
        MeetingImage meetingImage3 = new MeetingImage(++id, meeting, imgUrlList.get(2));
        MeetingImage meetingImage4 = new MeetingImage(++id, meeting, imgUrlList.get(3));
        when(meetingImageRepository.save(any()))
                .thenReturn(meetingImage1)
                .thenReturn(meetingImage2)
                .thenReturn(meetingImage3)
                .thenReturn(meetingImage4);

        List<MeetingImage> meetingImageList = Arrays.asList(meetingImage1, meetingImage2, meetingImage3, meetingImage4);

        MeetingCreateRespDto result = new MeetingCreateRespDto(meeting, meetingImageList);

        //when
        MeetingCreateRespDto respDto = meetingService.create(member, createMeetingCreateReqDto());

        //then
        assertThat(respDto).isEqualTo(result);
    }

    @Test
    @DisplayName("미팅 단건 조회")
    public void retrieveMeetingTest() throws Exception {
        //given
        MeetingImage meetingImage1 = new MeetingImage(++id, meeting, imgUrlList.get(0));
        MeetingImage meetingImage2 = new MeetingImage(++id, meeting, imgUrlList.get(1));
        MeetingImage meetingImage3 = new MeetingImage(++id, meeting, imgUrlList.get(2));
        MeetingImage meetingImage4 = new MeetingImage(++id, meeting, imgUrlList.get(3));

        List<MeetingImage> meetingImageList = Arrays.asList(meetingImage1, meetingImage2, meetingImage3, meetingImage4);

        MeetingMember meetingMember1 = new MeetingMember(++id, meeting, member);
        MeetingMember meetingMember2 = new MeetingMember(++id, meeting, new Member());
        MeetingMember meetingMember3 = new MeetingMember(++id, meeting, new Member());

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
        assertThrows(CustomException.class, () -> meetingService.localRetrieveOne(meeting.getId(), Optional.empty()));
    }

    @Test
    @DisplayName("미팅 전체 조회")
    public void retrieveAllMeetingTest() throws Exception {
        //given
        PageRequest pageRequest = PageRequest.of(0, 10);

        Meeting meeting1 = Meeting.builder()
                .id(++id)
                .member(member)
                .category(Category.AMITY)
                .meetingType(MeetingType.ONCE)
                .sex(Sex.ALL)
                .build();
        List<MeetingImage> meetingImageList1 = Arrays.asList(new MeetingImage(++id, meeting1, imgUrlList.get(0)));
        List<MeetingMember> meetingMemberList1 = Arrays.asList(new MeetingMember(++id, meeting1, member));

        Meeting meeting2 = Meeting.builder()
                .id(++id)
                .member(member)
                .category(Category.AMITY)
                .meetingType(MeetingType.ONCE)
                .sex(Sex.ALL)
                .build();
        List<MeetingImage> meetingImageList2 = Arrays.asList(new MeetingImage(++id, meeting2, imgUrlList.get(0)));
        List<MeetingMember> meetingMemberList2 = Arrays.asList(new MeetingMember(++id, meeting2, member));

        Meeting meeting3 = Meeting.builder()
                .id(++id)
                .member(member)
                .category(Category.AMITY)
                .meetingType(MeetingType.ONCE)
                .sex(Sex.ALL)
                .build();
        List<MeetingImage> meetingImageList3 = Arrays.asList(new MeetingImage(++id, meeting3, imgUrlList.get(0)));
        List<MeetingMember> meetingMemberList3 = Arrays.asList(new MeetingMember(++id, meeting3, member));

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
        String title = "수정 제목";
        String content = "수정 내용";
        boolean isOpened = false;

        Meeting editMeeting = createMeeting();
        editMeeting.setTitle(title);
        editMeeting.setContent(content);
        editMeeting.setIsOpened(isOpened);

        MeetingImage meetingImage1 = new MeetingImage(++id, editMeeting, imgUrlList.get(0));
        MeetingImage meetingImage2 = new MeetingImage(++id, editMeeting, imgUrlList.get(1));
        MeetingImage meetingImage3 = new MeetingImage(++id, editMeeting, imgUrlList.get(2));
        MeetingImage meetingImage4 = new MeetingImage(++id, editMeeting, imgUrlList.get(3));

        List<MeetingImage> meetingImageList = Arrays.asList(meetingImage1, meetingImage2, meetingImage3, meetingImage4);

        when(meetingRepository.findById(any())).thenReturn(Optional.ofNullable(editMeeting));
        doNothing().when(meetingImageRepository).deleteByMeetingId(any());
        when(meetingImageRepository.save(any()))
                .thenReturn(meetingImage1)
                .thenReturn(meetingImage2)
                .thenReturn(meetingImage3)
                .thenReturn(meetingImage4);

        MeetingEditRespDto result = new MeetingEditRespDto(editMeeting, meetingImageList);

        MeetingEditReqDto meetingEditReqDto = createMeetingEditReqDto();
        meetingEditReqDto.setTitle(title);
        meetingEditReqDto.setContent(content);
        meetingEditReqDto.setIsOpened(false);

        //when
        MeetingEditRespDto meetingEditRespDto = meetingService.edit(member, editMeeting.getId(), meetingEditReqDto);

        //then
        assertThat(meetingEditRespDto).isEqualTo(result);
    }

    @Test
    @DisplayName("회원 미팅 전체 조회 테스트")
    public void retrieveMemberMeetingTest() throws Exception {
        //given
        PageRequest pageRequest = PageRequest.of(0, 10);

        Meeting meeting1 = Meeting.builder()
                .id(++id)
                .member(member)
                .category(Category.AMITY)
                .meetingType(MeetingType.ONCE)
                .sex(Sex.ALL)
                .build();
        List<MeetingImage> meetingImageList1 = Arrays.asList(new MeetingImage(++id, meeting1, imgUrlList.get(0)));
        List<MeetingMember> meetingMemberList1 = Arrays.asList(new MeetingMember(++id, meeting1, member));

        Meeting meeting2 = Meeting.builder()
                .id(++id)
                .member(member)
                .category(Category.AMITY)
                .meetingType(MeetingType.ONCE)
                .sex(Sex.ALL)
                .build();
        List<MeetingImage> meetingImageList2 = Arrays.asList(new MeetingImage(++id, meeting2, imgUrlList.get(0)));
        List<MeetingMember> meetingMemberList2 = Arrays.asList(new MeetingMember(++id, meeting2, member));

        Meeting meeting3 = Meeting.builder()
                .id(++id)
                .member(member)
                .category(Category.AMITY)
                .meetingType(MeetingType.ONCE)
                .sex(Sex.ALL)
                .build();
        List<MeetingImage> meetingImageList3 = Arrays.asList(new MeetingImage(++id, meeting3, imgUrlList.get(0)));
        List<MeetingMember> meetingMemberList3 = Arrays.asList(new MeetingMember(++id, meeting3, member));

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

        //when
        Slice<MeetingRetrieveRespDto> respDtoSlice = meetingService.retrieveMemberMeeting(member, pageRequest);

        //then
        assertThat(respDtoSlice).isEqualTo(result);
    }

    private MeetingMember createMeetingMember() {
        return MeetingMember.builder()
                .id(++id)
                .meeting(meeting)
                .member(member)
                .build();
    }

    private MeetingCreateReqDto createMeetingCreateReqDto() {
        return MeetingCreateReqDto.builder()
                .title(title)
                .content(content)
                .doName(doName)
                .sigungu(sigungu)
                .sex(sex)
                .category(category)
                .conditions(conditions)
                .maxPeople(maxPeople)
                .image(imgUrlList)
                .build();
    }

    private MeetingEditReqDto createMeetingEditReqDto() {
        return MeetingEditReqDto.builder()
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
                .id(id++)
                .uid(uid)
                .email(email)
                .name(name)
                .nickname(nickname)
                .imgUrl(imgUrl)
                .introduce(introduce)
                .build();
    }

    private Meeting createMeeting() {
        return Meeting.builder()
                .id(++id)
                .member(member)
                .title(title)
                .content(content)
                .doName(doName)
                .sigungu(sigungu)
                .location(올림픽_공원)
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