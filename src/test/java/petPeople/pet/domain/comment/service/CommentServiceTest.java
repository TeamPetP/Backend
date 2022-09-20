package petPeople.pet.domain.comment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import petPeople.pet.config.auth.AuthFilterContainer;
import petPeople.pet.controller.comment.dto.req.CommentEditReqDto;
import petPeople.pet.controller.comment.dto.req.CommentWriteReqDto;
import petPeople.pet.controller.comment.dto.resp.CommentEditRespDto;
import petPeople.pet.controller.comment.dto.resp.CommentWriteRespDto;
import petPeople.pet.controller.post.dto.req.PostWriteReqDto;
import petPeople.pet.controller.post.dto.resp.CommentRetrieveRespDto;
import petPeople.pet.controller.post.dto.resp.CommentRetrieveWithCountRespDto;
import petPeople.pet.domain.comment.entity.Comment;
import petPeople.pet.domain.comment.entity.CommentLike;
import petPeople.pet.domain.comment.repository.commentLike.CommentLikeRepository;
import petPeople.pet.domain.comment.repository.comment.CommentRepository;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.member.repository.MemberRepository;
import petPeople.pet.domain.notification.repository.NotificationRepository;
import petPeople.pet.domain.post.entity.Post;
import petPeople.pet.domain.post.entity.PostImage;
import petPeople.pet.domain.post.entity.PostLike;
import petPeople.pet.domain.post.entity.Tag;
import petPeople.pet.domain.post.repository.post_image.PostImageRepository;
import petPeople.pet.domain.post.repository.post.PostRepository;
import petPeople.pet.domain.post.repository.tag.TagRepository;
import petPeople.pet.exception.CustomException;
import petPeople.pet.filter.MockJwtFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)//테스트 클래스가 Mockito를 사용함을 의미
class CommentServiceTest {

    final String uid = "jangg";
    final String email = "789456jang@naver.com";
    final String name = "장대영";
    final String nickname = "longstick0";
    final String imgUrl = "www.imgurl.com";
    final String introduce = "완다비전 개꿀잼이야";

    final String postContent = "게시글 내용";
    final String commentContent = "댓글 내용";

    @Mock
    CommentRepository commentRepository;
    @Mock
    CommentLikeRepository commentLikeRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    NotificationRepository notificationRepository;
    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    CommentService commentService;
    
    @Test
    @DisplayName("댓글 작성 테스트")
    public void 댓글_작성_테스트() throws Exception {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Post post = createPost(member, postContent);
        Comment comment = createComment(member, post, commentContent);

        when(commentRepository.save(any())).thenReturn(comment);
        when(postRepository.findById(any())).thenReturn(Optional.ofNullable(post));

        CommentWriteReqDto commentWriteReqDto = new CommentWriteReqDto(commentContent);

        CommentWriteRespDto expected = new CommentWriteRespDto(comment);

        //when
        CommentWriteRespDto result = commentService.writeComment(member, commentWriteReqDto, post.getId());

        //then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("자식 댓글 작성 테스트")
    public void 자식_댓글_작성_테스트() throws Exception {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Post post = createPost(member, postContent);
        Comment parentComment = createComment(member, post, commentContent);
        Comment childComment = createComment(member, post, commentContent);

        when(postRepository.findById(any())).thenReturn(Optional.ofNullable(post));
        when(commentRepository.findById(any())).thenReturn(Optional.ofNullable(parentComment));
        when(commentRepository.save(any())).thenReturn(childComment);

        CommentWriteReqDto commentWriteReqDto = new CommentWriteReqDto(commentContent);

        CommentWriteRespDto expected = new CommentWriteRespDto(childComment);

        //when
        CommentWriteRespDto result = commentService.writeChildComment(member, commentWriteReqDto, 1L, 1L);

        //then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인 하지 않은 사용자의 댓글 전체 조회 테스트")
    void 전체_조회() {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Post post = createPost(member, postContent);

        List<Comment> commentList = new ArrayList<>();
        List<CommentLike> commentLikeList = new ArrayList<>();

        //반복 1
        Comment parentComment1 = createComment(member, post, commentContent);
        Comment childComment1 = createComment(member, post, commentContent);
        parentComment1.getChild().add(childComment1);

        //반복 2
        Comment parentComment2 = createComment(member, post, commentContent);
        Comment childComment2 = createComment(member, post, commentContent);
        parentComment2.getChild().add(childComment2);

        commentList.add(parentComment1);
        commentList.add(parentComment2);
        commentList.add(childComment1);
        commentList.add(childComment2);

        commentLikeList.add(createCommentLike(member, post, parentComment1));
        commentLikeList.add(createCommentLike(member, post, childComment1));
        commentLikeList.add(createCommentLike(member, post, parentComment2));
        commentLikeList.add(createCommentLike(member, post, childComment2));

        List<CommentRetrieveRespDto> map = Arrays.asList(
                createNoLoginCommentRetrieveRespDto(parentComment1, commentLikeList, 1),
                createNoLoginCommentRetrieveRespDto(parentComment2, commentLikeList, 1),
                createNoLoginCommentRetrieveRespDto(childComment1, commentLikeList, 1),
                createNoLoginCommentRetrieveRespDto(childComment2, commentLikeList, 1)
        );

        CommentRetrieveWithCountRespDto expected = new CommentRetrieveWithCountRespDto(Long.valueOf(commentList.size()), map);

        when(commentRepository.findAllByIdWithFetchJoinMemberPaging(any())).thenReturn(commentList);
        when(commentLikeRepository.findCommentLikesByPostId(any())).thenReturn(commentLikeList);
        when(commentRepository.countByPostId(any())).thenReturn(Long.valueOf(commentList.size()));

        //when
        CommentRetrieveWithCountRespDto result = commentService.localRetrieveAll(any(), null);

        //then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인 한 사용자의 댓글 전체 조회 테스트")
    void 로그인후_전체_조회() {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Post post = createPost(member, postContent);

        List<Comment> commentList = new ArrayList<>();
        List<CommentLike> commentLikeList = new ArrayList<>();

        //반복 1
        Comment parentComment1 = createComment(member, post, commentContent);
        Comment childComment1 = createComment(member, post, commentContent);
        parentComment1.getChild().add(childComment1);

        //반복 2
        Comment parentComment2 = createComment(member, post, commentContent);
        Comment childComment2 = createComment(member, post, commentContent);
        parentComment2.getChild().add(childComment2);

        commentList.add(parentComment1);
        commentList.add(parentComment2);
        commentList.add(childComment1);
        commentList.add(childComment2);

        commentLikeList.add(createCommentLike(member, post, parentComment1));
        commentLikeList.add(createCommentLike(member, post, childComment1));
        commentLikeList.add(createCommentLike(member, post, parentComment2));
        commentLikeList.add(createCommentLike(member, post, childComment2));

        List<CommentRetrieveRespDto> map = Arrays.asList(
                new CommentRetrieveRespDto(parentComment1, 1, true, commentLikeList, member),
                new CommentRetrieveRespDto(parentComment2, 1, true, commentLikeList, member),
                new CommentRetrieveRespDto(childComment1, 1, true, commentLikeList, member),
                new CommentRetrieveRespDto(childComment2, 1, true, commentLikeList, member)
        );

        CommentRetrieveWithCountRespDto expected = new CommentRetrieveWithCountRespDto(Long.valueOf(commentList.size()), map);

        when(memberRepository.findByUid(any())).thenReturn(Optional.ofNullable(member));
        when(commentRepository.findAllByIdWithFetchJoinMemberPaging(any())).thenReturn(commentList);
        when(commentLikeRepository.findCommentLikesByPostId(any())).thenReturn(commentLikeList);
        when(commentRepository.countByPostId(any())).thenReturn(Long.valueOf(commentList.size()));

        //when
        CommentRetrieveWithCountRespDto result = commentService.localRetrieveAll(any(), "mockHeader");

        //then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void deleteCommentTest() {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Post post = createPost(member, postContent);
        Comment parentComment = createComment(member, post, commentContent);

        when(commentRepository.findById(any())).thenReturn(Optional.ofNullable(parentComment));

        doNothing().when(commentLikeRepository).deleteByCommentId(any());
        doNothing().when(commentRepository).deleteById(any());
        doNothing().when(notificationRepository).deleteNotificationByOwnerMemberIdAndCommentId(any(), any());
        doNothing().when(notificationRepository).deleteNotificationByMemberIdAndWriteCommentId(any(), any());

        //when
        commentService.deleteComment(member, 1L);

        //then
        verify(commentLikeRepository, times(1)).deleteByCommentId(any());
        verify(commentRepository, times(1)).deleteById(any());
    }

    @Test
    @DisplayName("권한이 없는 댓글 삭제 테스트")
    void nonAuthorizationCommentDeleteTest() {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Post post = createPost(member, postContent);
        Comment parentComment = createComment(member, post, commentContent);

        //when
        //then
        assertThrows(CustomException.class, () -> commentService.deleteComment(member, parentComment.getId()));
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    public void editCommentTest() throws Exception {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Post post = createPost(member, postContent);
        Comment parentComment = createComment(member, post, commentContent);
        Long likeCnt = 10L;

        when(commentRepository.findById(any())).thenReturn(Optional.ofNullable(parentComment));
        when(commentLikeRepository.countByCommentId(any())).thenReturn(likeCnt);

        String editCommentContent = "하이";
        CommentEditReqDto commentEditRespDto = new CommentEditReqDto(editCommentContent);


        //when
        CommentEditRespDto expected = commentService.editComment(member, parentComment.getId(), commentEditRespDto);

        parentComment.setContent(editCommentContent);
        CommentEditRespDto result = new CommentEditRespDto(parentComment, likeCnt);
        //then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("권한이 없는 사용자 댓글 수정 테스트")
    public void editNoOwnCommentTest() throws Exception {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Post post = createPost(member, postContent);
        Comment parentComment = createComment(new Member(), post, commentContent);
        when(commentRepository.findById(any())).thenReturn(Optional.ofNullable(parentComment));

        //when
        //then
        assertThrows(CustomException.class, () -> commentService.editComment(member, 1L, any()));
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

    private CommentRetrieveRespDto createNoLoginCommentRetrieveRespDto(Comment comment, List<CommentLike> findCommentLikeList, int commentLikeSize) {
        return new CommentRetrieveRespDto(comment, commentLikeSize, null, findCommentLikeList, null);
    }

    private CommentLike createCommentLike(Member member, Post post, Comment comment) {
        return CommentLike.builder()
                .comment(comment)
                .member(member)
                .post(post)
                .build();
    }


    private Post createPost(Member member, String content) {
        return Post.builder()
                .member(member)
                .content(content)
                .build();
    }

    public Comment createComment(Member member, Post post, String content) {
        return Comment.builder()
                .member(member)
                .post(post)
                .content(content)
                .build();
    }

}