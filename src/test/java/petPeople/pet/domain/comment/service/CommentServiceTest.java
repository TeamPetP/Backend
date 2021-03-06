package petPeople.pet.domain.comment.service;

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
import org.springframework.security.core.userdetails.UserDetailsService;
import petPeople.pet.controller.comment.dto.req.CommentEditReqDto;
import petPeople.pet.controller.comment.dto.req.CommentWriteReqDto;
import petPeople.pet.controller.comment.dto.resp.CommentEditRespDto;
import petPeople.pet.controller.comment.dto.resp.CommentWriteRespDto;
import petPeople.pet.controller.post.dto.req.PostWriteReqDto;
import petPeople.pet.controller.post.dto.resp.CommentRetrieveRespDto;
import petPeople.pet.controller.post.dto.resp.CommentRetrieveWithCountRespDto;
import petPeople.pet.domain.comment.entity.Comment;
import petPeople.pet.domain.comment.entity.CommentLike;
import petPeople.pet.domain.comment.repository.CommentLikeRepository;
import petPeople.pet.domain.comment.repository.CommentRepository;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.post.entity.Post;
import petPeople.pet.domain.post.entity.PostImage;
import petPeople.pet.domain.post.entity.PostLike;
import petPeople.pet.domain.post.entity.Tag;
import petPeople.pet.domain.post.repository.PostImageRepository;
import petPeople.pet.domain.post.repository.PostRepository;
import petPeople.pet.domain.post.repository.TagRepository;
import petPeople.pet.exception.CustomException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)//????????? ???????????? Mockito??? ???????????? ??????
class CommentServiceTest {

    final String uid = "jangg";
    final String email = "789456jang@naver.com";
    final String name = "?????????";
    final String nickname = "longstick0";
    final String imgUrl = "www.imgurl.com";
    final String introduce = "???????????? ???????????????";

    final List<String> postTags = Arrays.asList("??????", "?????????", "?????????");
    final List<String> postImgUrls = Arrays.asList("www.???????????????????.com", "www.imgaeABC.com");
    final String postContent = "????????? ??????";
    final String commentContent = "?????? ??????";

    @Mock
    CommentRepository commentRepository;
    @Mock
    CommentLikeRepository commentLikeRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    TagRepository tagRepository;
    @Mock
    PostImageRepository postImageRepository;
    @Mock
    UserDetailsService userDetailsService;

    @InjectMocks
    CommentService commentService;

    //????????? ?????? ????????? ??????? -> beforeEach??? ???????????? ?????? ??????
    Long id;
    
    Member member;
    Post post;
    PostWriteReqDto postWriteReqDto;
    List<Tag> tagList;
    List<PostImage> postImageList;
    List<PostLike> postLikeList;
    
    Comment comment;
    CommentWriteReqDto commentWriteReqDto;
    CommentEditReqDto commentEditReqDto;
    List<CommentLike> commentLikeList;


    //?????? ????????? ??? ?????????????????? ??????????
    @BeforeEach
    void beforeEach() {
        id = 1L;
        member = createMember(uid, email, name, nickname, imgUrl, introduce);

        //????????? ??????
        postWriteReqDto = createPostWriteReqDto(postContent, postTags, postImgUrls);
        post = createPost(member, postWriteReqDto.getContent());
        tagList = createTagList(postTags, post);
        postImageList = createPostImageList(postImgUrls, post);
        postLikeList = createPostLikeList();

        //?????? ??????
        comment = createComment(member, post, commentContent);
        commentWriteReqDto = createCommentWriteReqDto(commentContent);
        commentEditReqDto = createCommentEditReqDto(commentContent);
        commentLikeList = createCommentLikeList();

    }

    @Test
    @DisplayName("?????? ?????? ?????????")
    public void writeCommentTest() throws Exception {
        //given
        when(commentRepository.save(any())).thenReturn(comment);
        when(postRepository.findById(any())).thenReturn(Optional.ofNullable(post));

        CommentWriteRespDto respDto = new CommentWriteRespDto(comment);

        //when
        //@EqualsAndHashCode ??????
        CommentWriteRespDto result = commentService.writeComment(member, commentWriteReqDto, post.getId(), null);

        //then
        assertThat(result).isEqualTo(respDto);
    }

    @Test
    @DisplayName("?????? ?????? ?????????")
    public void editCommentTest() throws Exception {

        Long likeCnt = 10L;

        //given
        when(commentRepository.findById(any())).thenReturn(Optional.ofNullable(comment));
        when(commentLikeRepository.countByCommentId(any())).thenReturn(likeCnt);

        CommentEditRespDto result = new CommentEditRespDto(comment, likeCnt);

        //when
        CommentEditRespDto respDto = commentService.editComment(member, comment.getId(), commentEditReqDto);

        //then
        assertThat(result).isEqualTo(respDto);
    }

    @Test
    @DisplayName("????????? ?????? ????????? ?????? ?????? ?????????")
    public void editNoOwnCommentTest() throws Exception {

        //given
        Member commentMember = createMember(uid, email, name, nickname, imgUrl, introduce);
        when(commentRepository.findById(any())).thenReturn(Optional.ofNullable(comment));

        //when
        //then
        assertThrows(CustomException.class, () -> commentService.editComment(commentMember, comment.getId(), commentEditReqDto));
    }

    @Test
    @DisplayName("????????? ?????? ?????? ???????????? ?????? ?????? ?????? ?????????")
    void noLoginRetrieveAll() {
        //given
        Long likeCnt = 11L;
        PageRequest pageRequest = PageRequest.of(0, 10);

        Member commentMember = createMember(uid, email, name, nickname, imgUrl, introduce);


        CommentWriteReqDto writeReqDto1 = createCommentWriteReqDto("??????1");
        CommentWriteReqDto writeReqDto2 = createCommentWriteReqDto("??????2");
        CommentWriteReqDto writeReqDto3 = createCommentWriteReqDto("??????3");

        Comment comment1 = createComment(member, post, writeReqDto1.getContent());
        Comment comment2 = createComment(member, post, writeReqDto2.getContent());
        Comment comment3 = createComment(member, post, writeReqDto3.getContent());

        List<Comment> commentList = Arrays.asList(comment1, comment2, comment3);

        CommentLike commentLike1 = new CommentLike(id++, comment1, createMember(uid, email, name, nickname, imgUrl, introduce), post);
        CommentLike commentLike2 = new CommentLike(id++, comment2, createMember(uid, email, name, nickname, imgUrl, introduce), post);
        CommentLike commentLike3 = new CommentLike(id++, comment3, createMember(uid, email, name, nickname, imgUrl, introduce), post);

        List<CommentLike> commentLikeList = Arrays.asList(commentLike1, commentLike2, commentLike3);

        SliceImpl<Comment> commentSlice = new SliceImpl<>(commentList, pageRequest, false);

        List<Comment> commentSliceContent = commentSlice.getContent();

//        when(commentRepository.findAllByIdWithFetchJoinMemberPaging(any(), any())).thenReturn(commentSlice);
        when(commentLikeRepository.findCommentLikesByCommentIds(any())).thenReturn(commentLikeList);

        Slice<CommentRetrieveRespDto> result = commentSlice.map(comment -> new CommentRetrieveRespDto(comment, likeCnt, false, commentLikeList, commentMember.getId()));

        //when

//        CommentRetrieveWithCountRespDto reqsDto = commentService.retrieveAll(post.getId(), null, pageRequest);

        //then
//        assertThat(reqsDto).isEqualTo(result);
    }

    @Test
    @DisplayName("????????? ??? ???????????? ?????? ?????? ?????? ?????????")
    void longinRetrieveAll() {
        //given
        Long likeCnt = 12L;
        PageRequest pageRequest = PageRequest.of(0, 10);

        Member commentMember = createMember(uid, email, name, nickname, imgUrl, introduce);

        CommentWriteReqDto writeReqDto1 = createCommentWriteReqDto("??????1");
        CommentWriteReqDto writeReqDto2 = createCommentWriteReqDto("??????2");
        CommentWriteReqDto writeReqDto3 = createCommentWriteReqDto("??????3");

        Comment comment1 = createComment(member, post, writeReqDto1.getContent());
        Comment comment2 = createComment(member, post, writeReqDto2.getContent());
        Comment comment3 = createComment(member, post, writeReqDto3.getContent());

        List<Comment> commentList = Arrays.asList(comment1, comment2, comment3);

        CommentLike commentLike1 = new CommentLike(id++, comment1, createMember(uid, email, name, nickname, imgUrl, introduce), post);
        CommentLike commentLike2 = new CommentLike(id++, comment2, createMember(uid, email, name, nickname, imgUrl, introduce), post);
        CommentLike commentLike3 = new CommentLike(id++, comment3, createMember(uid, email, name, nickname, imgUrl, introduce), post);

        List<CommentLike> commentLikeList = Arrays.asList(commentLike1, commentLike2, commentLike3);

        SliceImpl<Comment> commentSlice = new SliceImpl<>(commentList, pageRequest, false);

        List<Comment> commentSliceContent = commentSlice.getContent();

        List<Long> commentIdList = getCommentId(commentSliceContent);

//        when(commentRepository.findAllByIdWithFetchJoinMemberPaging(post.getId(), pageRequest)).thenReturn(commentSlice);
        when(commentLikeRepository.findCommentLikesByCommentIds(commentIdList)).thenReturn(commentLikeList);
        when(userDetailsService.loadUserByUsername(any()));

        Slice<CommentRetrieveRespDto> result = commentSlice.map(comment -> new CommentRetrieveRespDto(comment, likeCnt, false, commentLikeList, commentMember.getId()));

        //when

//        CommentRetrieveWithCountRespDto reqsDto = commentService.retrieveAll(post.getId(), member.getUid(), pageRequest);

        //then
//        assertThat(reqsDto).isEqualTo(result);
    }

    private List<Long> getCommentId(List<Comment> commentSliceContent) {
        List<Long> ids = new ArrayList<>();
        for (Comment com : commentSliceContent) {
            ids.add(com.getId());
        }
        return ids;
    }

    @Test
    @DisplayName("?????? ?????? ?????????")
    void deleteCommentTest() {
        //given
        when(commentRepository.findById(any())).thenReturn(Optional.ofNullable(comment));

        doNothing().when(commentLikeRepository).deleteByCommentId(any());
        doNothing().when(commentRepository).deleteById(any());

        //when
        commentService.deleteComment(member, comment.getId());

        //then
        verify(commentLikeRepository, times(1)).deleteByCommentId(any());
        verify(commentRepository, times(1)).deleteById(any());
    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????? ?????????")
    void nonAuthorizationCommentDeleteTest() {
        //given
        Member commentMember = createMember(uid, email, name, nickname, imgUrl, introduce);
        when(commentRepository.findById(any())).thenReturn(Optional.ofNullable(comment));

        //when
        //then
        assertThrows(CustomException.class, () -> commentService.deleteComment(commentMember, comment.getId()));
    }

    @Test
    @DisplayName("?????? ????????? ?????????")
    void loginLikeComment() {
        //given
//        when(commentRepository.findAllByIdWithFetchJoinMemberPaging(any(), any())).thenReturn();
        //when
        //then
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

    private CommentWriteReqDto createCommentWriteReqDto(String content) {
        return new CommentWriteReqDto(content);
    }

    private Post createPost(Member member, String content) {
        return Post.builder()
                .id(id)
                .member(member)
                .content(content)
                .build();
    }

    public List<Tag> createTagList(List<String> tags, Post post) {
        List<Tag> tagList = new ArrayList<>();

        for (String t : tags) {
            tagList.add((createTag(post, t)));
        }
        return tagList;
    }

    private Tag createTag(Post post, String t) {
        return Tag.builder()
                .id(id++)
                .post(post)
                .tag(t)
                .build();
    }

    private List<PostLike> createPostLikeList() {
        return Arrays.asList(
                new PostLike(id++, post, member),
                new PostLike(id++, post, member),
                new PostLike(id++, post, member));
    }

    private List<CommentLike> createCommentLikeList() {
        return Arrays.asList(
                new CommentLike(id++, comment, member, post),
                new CommentLike(id++, comment, member, post),
                new CommentLike(id++, comment, member, post));
    }

    private PostWriteReqDto createPostWriteReqDto(String postContent, List<String> postTags, List<String> postImgUrls) {
        return new PostWriteReqDto(postContent, postTags, postImgUrls);
    }
    private PostImage createPostImage(Post post, String url) {
        return PostImage.builder()
                .id(id++)
                .post(post)
                .imgUrl(url)
                .build();
    }
    public List<PostImage> createPostImageList(List<String> urls, Post post) {
        List<PostImage> postImageList = new ArrayList<>();

        for (String url : urls) {
            postImageList.add((createPostImage(post, url)));
        }
        return postImageList;
    }

    public Comment createComment(Member member, Post post, String content) {
        return Comment.builder()
                .id(id)
                .member(member)
                .post(post)
                .content(content)
                .build();
    }

    private CommentEditReqDto createCommentEditReqDto(String commentContent) {
        return new CommentEditReqDto(commentContent);
    }


}