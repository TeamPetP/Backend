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
import petPeople.pet.controller.member.dto.resp.MeetingJoinApplyRespDto;
import petPeople.pet.controller.member.dto.resp.MeetingWaitingMemberRespDto;
import petPeople.pet.domain.meeting.entity.Meeting;
import petPeople.pet.domain.meeting.entity.MeetingMember;
import petPeople.pet.domain.meeting.entity.MeetingWaitingMember;
import petPeople.pet.domain.meeting.entity.vo.Category;
import petPeople.pet.domain.meeting.entity.vo.JoinRequestStatus;
import petPeople.pet.domain.meeting.entity.vo.MeetingType;
import petPeople.pet.domain.meeting.entity.vo.Sex;
import petPeople.pet.domain.meeting.repository.meeting.MeetingRepository;
import petPeople.pet.domain.meeting.repository.meeting_member.MeetingMemberRepository;
import petPeople.pet.domain.meeting.repository.meeting_waiting_member.MeetingWaitingMemberRepository;
import petPeople.pet.domain.member.entity.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeetingWaitingMemberServiceTest {

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
    MeetingMemberRepository meetingMemberRepository;
    @Mock
    MeetingWaitingMemberRepository meetingWaitingMemberRepository;
    @Mock
    MeetingRepository meetingRepository;

    @InjectMocks
    MeetingWaitingMemberService meetingWaitingMemberService;

    @Test
    public void 모임_가입_신청자_조회() throws Exception {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Meeting meeting = createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople);

        MeetingWaitingMember meetingWaitingMember1 = createMeetingWaitingMember(member, meeting, JoinRequestStatus.WAITING);
        MeetingWaitingMember meetingWaitingMember2 = createMeetingWaitingMember(member, meeting, JoinRequestStatus.WAITING);
        MeetingWaitingMember meetingWaitingMember3 = createMeetingWaitingMember(member, meeting, JoinRequestStatus.WAITING);

        List<MeetingWaitingMember> meetingWaitingMemberList =
                List.of(
                meetingWaitingMember1,
                meetingWaitingMember2,
                meetingWaitingMember3);

        List<MeetingWaitingMemberRespDto> result = meetingWaitingMemberList.stream()
                .map(mwm -> createMeetingWaitingMemberRespDto(mwm))
                .collect(Collectors.toList());

        when(meetingRepository.findById(any())).thenReturn(Optional.ofNullable(meeting));
        when(meetingWaitingMemberRepository.findAllByMeetingIdFetchJoinMember(any())).thenReturn(meetingWaitingMemberList);

        //when
        List<MeetingWaitingMemberRespDto> expected = meetingWaitingMemberService.retrieveMeetingWaitingMember(member, any());

        //then
        Assertions.assertThat(expected).isEqualTo(result);
    }

    @Test
    public void 내가_가입_신청한_모임_현황() throws Exception {
        //given
        PageRequest mockPage = PageRequest.of(0, 3);

        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Meeting meeting = createMeeting(member, title, content, doName, sigungu, location, sex, category, meetingType, period, conditions, maxPeople);

        MeetingMember meetingMember = createMeetingMember(member, meeting);

        MeetingWaitingMember meetingWaitingMember1 = createMeetingWaitingMember(member, meeting, JoinRequestStatus.WAITING);
        MeetingWaitingMember meetingWaitingMember2 = createMeetingWaitingMember(member, meeting, JoinRequestStatus.WAITING);
        MeetingWaitingMember meetingWaitingMember3 = createMeetingWaitingMember(member, meeting, JoinRequestStatus.WAITING);

        List<MeetingWaitingMember> meetingWaitingMemberList =
                List.of(
                        meetingWaitingMember1,
                        meetingWaitingMember2,
                        meetingWaitingMember3);

        List<MeetingMember> meetingMemberList = List.of(meetingMember);

        SliceImpl<MeetingWaitingMember> meetingWaitingMemberSlice = new SliceImpl<>(meetingWaitingMemberList);

        Slice<MeetingJoinApplyRespDto> result = meetingWaitingMemberSlice.map(mwm -> {
            List<Member> joinMembers = new ArrayList<>();
            for (MeetingMember mm : meetingMemberList) {
                if (mm.getMeeting() == mwm.getMeeting()) {
                    joinMembers.add(mm.getMember());
                }
            }
            return new MeetingJoinApplyRespDto(mwm, joinMembers);
        });

        when(meetingWaitingMemberRepository.findAllByMemberIdFetchJoinMemberAndMeeting(any(), any())).thenReturn(meetingWaitingMemberSlice);
        when(meetingMemberRepository.findByMeetingIds(any())).thenReturn(meetingMemberList);

        //when
        Slice<MeetingJoinApplyRespDto> expected = meetingWaitingMemberService.retrieveMeetingWaitingMemberApply(mockPage, member);

        //then
        Assertions.assertThat(expected).isEqualTo(result);
    }

    private MeetingMember createMeetingMember(Member member, Meeting meeting) {
        return MeetingMember.builder()
                .member(member)
                .meeting(meeting)
                .build();
    }

    private MeetingWaitingMemberRespDto createMeetingWaitingMemberRespDto(MeetingWaitingMember meetingWaitingMember) {
        return MeetingWaitingMemberRespDto.builder()
                .memberId(meetingWaitingMember.getMember().getId())
                .memberImgUrl(meetingWaitingMember.getMember().getImgUrl())
                .meetingId(meetingWaitingMember.getMeeting().getId())
                .nickname(meetingWaitingMember.getMember().getNickname())
                .introduce(meetingWaitingMember.getMember().getIntroduce())
                .joinRequestStatus(meetingWaitingMember.getJoinRequestStatus().getDetail())
                .createDate(meetingWaitingMember.getCreatedDate())
                .build();
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