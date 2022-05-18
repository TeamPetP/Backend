package petPeople.pet.domain.meeting.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import petPeople.pet.controller.meeting.dto.req.MeetingCreateReqDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingCreateRespDto;
import petPeople.pet.controller.meeting.dto.resp.MeetingRetrieveRespDto;
import petPeople.pet.domain.meeting.entity.*;
import petPeople.pet.domain.meeting.repository.MeetingImageRepository;
import petPeople.pet.domain.meeting.repository.MeetingMemberRepository;
import petPeople.pet.domain.meeting.repository.MeetingRepository;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.exception.CustomException;

import java.time.LocalDateTime;
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
    final Integer maxAge = 25;
    final Integer minAge = 20;
    final LocalDateTime endDate = LocalDateTime.of(2022, 05, 15, 5, 15);
    final LocalDateTime meetingDate = LocalDateTime.of(2022, 05, 15, 5, 15);
    final Integer maxPeople = 5;
    final List<String> imgUrlList = Arrays.asList("www.abc.com", "www.abc.com", "www.abc.com", "www.abc.com");

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
    @DisplayName("미팅 날짜가 모집 마감 날짜 이후 테스트")
    public void endDateAfterMeetingDateTest() throws Exception {
        //given
        //when

        //then
        MeetingCreateReqDto meetingCreateReqDto = createMeetingCreateReqDto();
        meetingCreateReqDto.setEndDate(LocalDateTime.of(2022, 1, 30, 15, 00));
        meetingCreateReqDto.setMeetingDate(LocalDateTime.of(2022, 1, 30, 14, 00));
        assertThrows(CustomException.class, () -> meetingService.create(member, meetingCreateReqDto));
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

        MeetingRetrieveRespDto result = new MeetingRetrieveRespDto(meeting, meetingImageList, meetingMemberList);

        //when
        MeetingRetrieveRespDto respDto = meetingService.retrieveOne(meeting.getId());

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
        assertThrows(CustomException.class, () -> meetingService.retrieveOne(meeting.getId()));
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
                .maxAge(maxAge)
                .minAge(minAge)
                .endDate(endDate)
                .meetingDate(meetingDate)
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
                .sex(sex)
                .category(category)
                .conditions(conditions)
                .maxAge(maxAge)
                .minAge(minAge)
                .endDate(endDate)
                .meetingDate(meetingDate)
                .maxPeople(maxPeople)
                .isOpened(true)
                .build();
    }

}