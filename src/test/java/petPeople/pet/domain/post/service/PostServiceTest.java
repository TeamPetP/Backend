package petPeople.pet.domain.post.service;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import petPeople.pet.controller.post.dto.req.PostWriteReqDto;
import petPeople.pet.controller.post.dto.resp.PostEditRespDto;
import petPeople.pet.controller.post.dto.resp.PostRetrieveRespDto;
import petPeople.pet.controller.post.dto.resp.PostWriteRespDto;
import petPeople.pet.domain.comment.entity.Comment;
import petPeople.pet.domain.comment.repository.comment.CommentRepository;
import petPeople.pet.domain.comment.repository.commentLike.CommentLikeRepository;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.member.repository.MemberRepository;
import petPeople.pet.domain.notification.repository.NotificationRepository;
import petPeople.pet.domain.post.entity.*;
import petPeople.pet.domain.post.repository.post.PostRepository;
import petPeople.pet.domain.post.repository.post_bookmark.PostBookmarkRepository;
import petPeople.pet.domain.post.repository.post_image.PostImageRepository;
import petPeople.pet.domain.post.repository.post_like.PostLikeRepository;
import petPeople.pet.domain.post.repository.tag.TagRepository;
import petPeople.pet.exception.CustomException;
import petPeople.pet.exception.ErrorCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)//테스트 클래스가 Mockito를 사용함을 의미합니다.
class PostServiceTest {

    @Mock //mock 객체를 생성함
    PostRepository postRepository;
    @Mock
    TagRepository tagRepository;
    @Mock
    PostLikeRepository postLikeRepository;
    @Mock
    PostImageRepository postImageRepository;
    @Mock
    UserDetailsService userDetailsService;
    @Mock
    CommentRepository commentRepository;
    @Mock
    MemberRepository memberRepository;

    @InjectMocks //생성한 mock 객체를 @InjectMocks이 붙은 객체에 주입 주로 @InjectMocks(Service) @Mock(Repository) 이러한 식으로 Service 테스트 목객체에 DAO 목객체를 주입시켜 사용함
    PostService postService;

    final String uid = "abcd";
    final String email = "issiscv@naver.com";
    final String name = "김상운";
    final String nickname = "balladang";
    final String imgUrl = "www.imgurl.com";
    final String introduce = "잘지내요 우리";

    final String content = "강아지 좋아해요";

    final String tagName = "태그";

    final String postImageUrl = "www.postimgurl.com";

    @DisplayName("게시글 정상 작성 테스트")
    @Test
    public void 게시글_작성() throws Exception {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Post post = createPost(member, content);

        List<String> tagStrList = new ArrayList<>();
        List<Tag> tagList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String tagName = this.tagName + i;
            tagStrList.add(tagName);

            Tag tag = createTag(post, tagName);
            tagList.add(tag);
        }

        List<String> postImageStrList = new ArrayList<>();
        List<PostImage> postImageList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String postImageUrl = this.postImageUrl + i;
            postImageStrList.add(postImageUrl);

            PostImage postImage = createPostImage(postImageUrl, post);
            postImageList.add(postImage);
        }

        when(postRepository.save(any())).thenReturn(post);
        when(tagRepository.save(any()))
                .thenReturn(tagList.get(0))
                .thenReturn(tagList.get(1))
                .thenReturn(tagList.get(2));
        when(postImageRepository.save(any()))
                .thenReturn(postImageList.get(0))
                .thenReturn(postImageList.get(1))
                .thenReturn(postImageList.get(2));

        PostWriteRespDto expected = new PostWriteRespDto(post, tagList, postImageList);

        //when
        PostWriteRespDto postWriteRespDto = postService.write(member, new PostWriteReqDto(content, tagStrList, postImageStrList));

        //then
        assertThat(postWriteRespDto).isEqualTo(expected);
    }

    @DisplayName("로그인 하지 않고 게시글 단건 조회 성공")
    @Test
    public void 로그인하지않고_게시글_단건조회_성공() throws Exception {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Post post = createPost(member, content);

        List<Tag> tagList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Tag tag = createTag(post, this.tagName + i);
            tagList.add(tag);
        }

        List<PostImage> postImageList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            PostImage postImage = createPostImage(this.postImageUrl + i, post);
            postImageList.add(postImage);
        }

        long postLikeCnt = 3;
        long commentCnt = 5;

        when(postRepository.findByIdWithFetchJoinMember(any())).thenReturn(Optional.ofNullable(post));
        when(tagRepository.findByPostId(any())).thenReturn(tagList);
        when(postImageRepository.findByPostId(any())).thenReturn(postImageList);
        when(postLikeRepository.countByPostId(any())).thenReturn(postLikeCnt);
        when(commentRepository.countByPostId(any())).thenReturn(commentCnt);

        PostRetrieveRespDto expected = new PostRetrieveRespDto(post, tagList, postImageList, postLikeCnt, null, commentCnt, null);

        //when
        PostRetrieveRespDto postRetrieveRespDto = postService.localRetrieveOne(any(), Optional.empty());

        //then
        assertThat(postRetrieveRespDto).isEqualTo(expected);
    }

    @DisplayName("로그인 하지 않고 게시글 단건 조회 실패")
    @Test
    public void 게시글_단건조회_실패() throws Exception {
        //given
        when(postRepository.findByIdWithFetchJoinMember(any())).thenReturn(Optional.empty());//게시글 단건 검색시 empty

        //when
        //then
        Assertions.assertThrows(CustomException.class, () -> postService.localRetrieveOne(any(), Optional.empty()));
    }

    @DisplayName("로그인 후 좋아요 한 게시글 단건 조회 성공")
    @Test
    public void 로그인후_좋아요_누른_게시글_단건조회_성공() throws Exception {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Post post = createPost(member, content);
        PostLike postLike = createPostLike(member, post);

        List<Tag> tagList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Tag tag = createTag(post, this.tagName + i);
            tagList.add(tag);
        }

        List<PostImage> postImageList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            PostImage postImage = createPostImage(this.postImageUrl + i, post);
            postImageList.add(postImage);
        }

        long postLikeCnt = 3;
        long commentCnt = 5;

        when(postRepository.findByIdWithFetchJoinMember(any())).thenReturn(Optional.ofNullable(post));
        when(tagRepository.findByPostId(any())).thenReturn(tagList);
        when(postImageRepository.findByPostId(any())).thenReturn(postImageList);
        when(postLikeRepository.countByPostId(any())).thenReturn(postLikeCnt);
        when(commentRepository.countByPostId(any())).thenReturn(commentCnt);
        when(postLikeRepository.findPostLikeByPostIdAndMemberId(any(), any())).thenReturn(Optional.empty());
        when(userDetailsService.loadUserByUsername(any())).thenReturn(member);

        PostRetrieveRespDto expected = new PostRetrieveRespDto(post, tagList, postImageList, postLikeCnt, false, commentCnt, member);

        //when
        PostRetrieveRespDto postRetrieveRespDto = postService.localRetrieveOne(any(), Optional.ofNullable(member.getUid()));

        //then
        assertThat(postRetrieveRespDto).isEqualTo(expected);
    }

    @DisplayName("로그인 후 게시글 단건 조회 성공")
    @Test
    public void 로그인후_게시글_단건조회_성공() throws Exception {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Post post = createPost(member, content);
        PostLike postLike = createPostLike(member, post);

        List<Tag> tagList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Tag tag = createTag(post, this.tagName + i);
            tagList.add(tag);
        }

        List<PostImage> postImageList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            PostImage postImage = createPostImage(this.postImageUrl + i, post);
            postImageList.add(postImage);
        }

        long postLikeCnt = 3;
        long commentCnt = 5;

        when(postRepository.findByIdWithFetchJoinMember(any())).thenReturn(Optional.ofNullable(post));
        when(tagRepository.findByPostId(any())).thenReturn(tagList);
        when(postImageRepository.findByPostId(any())).thenReturn(postImageList);
        when(postLikeRepository.countByPostId(any())).thenReturn(postLikeCnt);
        when(commentRepository.countByPostId(any())).thenReturn(commentCnt);
        when(postLikeRepository.findPostLikeByPostIdAndMemberId(any(), any())).thenReturn(Optional.ofNullable(postLike));
        when(userDetailsService.loadUserByUsername(any())).thenReturn(member);

        PostRetrieveRespDto expected = new PostRetrieveRespDto(post, tagList, postImageList, postLikeCnt, true, commentCnt, member);

        //when
        PostRetrieveRespDto postRetrieveRespDto = postService.localRetrieveOne(any(), Optional.ofNullable(member.getUid()));

        //then
        assertThat(postRetrieveRespDto).isEqualTo(expected);
    }

    @DisplayName("로그인하지 않고 게시글 전체 조회")
    @Test
    public void 로그인_하지않고_게시글_전체_조회() throws Exception {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);

        Post post1 = createPost(member, content);
        post1.setId(1L);
        Post post2 = createPost(member, content);
        post2.setId(2L);
        Post post3 = createPost(member, content);
        post3.setId(3L);

        List<Tag> tagList = new ArrayList<>();
        List<PostImage> postImageList = new ArrayList<>();
        List<PostLike> postLikeList = new ArrayList<>();
        List<Comment> commentList = new ArrayList<>();


        for (int i = 0; i < 3; i++) {
            tagList.add(createTag(post1, "tag"));
            postImageList.add(createPostImage("postImageUrl", post1));
            postLikeList.add(createPostLike(member, post1));
            commentList.add(createComment(member, post1));
        }

        for (int i = 0; i < 3; i++) {
            tagList.add(createTag(post2, "tag"));
            postImageList.add(createPostImage("postImageUrl", post2));
            postLikeList.add(createPostLike(member, post2));
            commentList.add(createComment(member, post2));
        }

        for (int i = 0; i < 3; i++) {
            tagList.add(createTag(post3, "tag"));
            postImageList.add(createPostImage("postImageUrl", post3));
            postLikeList.add(createPostLike(member, post3));
            commentList.add(createComment(member, post3));
        }


        SliceImpl<Post> postSlice = new SliceImpl<>(List.of(post1, post2, post3));

        when(postRepository.findAllSlicing(any(), any())).thenReturn(postSlice);
        when(tagRepository.findTagsByPostIds(any())).thenReturn(tagList);
        when(postImageRepository.findPostImagesByPostIds(any())).thenReturn(postImageList);
        when(postLikeRepository.findPostLikesByPostIds(any())).thenReturn(postLikeList);
        when(commentRepository.findByPostIds(any())).thenReturn(commentList);

        Slice<PostRetrieveRespDto> expected = postSlice.map(post -> {
            List<Tag> tagByPostList = getTagListByPost(tagList, post);
            List<PostImage> postImageByTagList = getPostImageListByPost(postImageList, post);
            List<PostLike> postLikeByPostList = getPostLikeListByPost(postLikeList, post);
            List<Comment> commentListByPost = getCommentListByPost(commentList, post);
            return new PostRetrieveRespDto(post, tagByPostList, postImageByTagList, Long.valueOf(postLikeByPostList.size()), null, Long.valueOf(commentListByPost.size()), null);
        });

        //when
        Slice<PostRetrieveRespDto> retrieveRespDtoSlice = postService.localRetrieveAll(PageRequest.of(0, 3), Optional.empty(), Optional.empty());

        //then
        assertThat(retrieveRespDtoSlice).isEqualTo(expected);
    }

    @DisplayName("로그인 하고 게시글 전체 조회")
    @Test
    public void 로그인_하고_게시글_전체_조회() throws Exception {
        //given
        String loginUid = "qwer";
        Member memberLogined = createMember(loginUid, email, name, nickname, imgUrl, introduce);
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);

        Post post1 = createPost(member, content);
        post1.setId(1L);
        Post post2 = createPost(member, content);
        post2.setId(2L);
        Post post3 = createPost(member, content);
        post3.setId(3L);

        List<Tag> tagList = new ArrayList<>();
        List<PostImage> postImageList = new ArrayList<>();
        List<PostLike> postLikeList = new ArrayList<>();
        List<Comment> commentList = new ArrayList<>();


        for (int i = 0; i < 3; i++) {
            tagList.add(createTag(post1, "tag"));
            postImageList.add(createPostImage("postImageUrl", post1));
            postLikeList.add(createPostLike(member, post1));
            commentList.add(createComment(member, post1));
        }

        for (int i = 0; i < 3; i++) {
            tagList.add(createTag(post2, "tag"));
            postImageList.add(createPostImage("postImageUrl", post2));
            postLikeList.add(createPostLike(member, post2));
            commentList.add(createComment(member, post2));
        }

        for (int i = 0; i < 3; i++) {
            tagList.add(createTag(post3, "tag"));
            postImageList.add(createPostImage("postImageUrl", post3));
            postLikeList.add(createPostLike(member, post3));
            commentList.add(createComment(member, post3));
        }


        SliceImpl<Post> postSlice = new SliceImpl<>(List.of(post1, post2, post3));

        when(postRepository.findAllSlicing(any(), any())).thenReturn(postSlice);
        when(tagRepository.findTagsByPostIds(any())).thenReturn(tagList);
        when(postImageRepository.findPostImagesByPostIds(any())).thenReturn(postImageList);
        when(postLikeRepository.findPostLikesByPostIds(any())).thenReturn(postLikeList);
        when(commentRepository.findByPostIds(any())).thenReturn(commentList);
        when(memberRepository.findByUid(any())).thenReturn(Optional.ofNullable(memberLogined));

        Slice<PostRetrieveRespDto> expected = postSlice.map(post -> {
            List<Tag> tagByPostList = getTagListByPost(tagList, post);
            List<PostImage> postImageByTagList = getPostImageListByPost(postImageList, post);
            List<PostLike> postLikeByPostList = getPostLikeListByPost(postLikeList, post);
            List<Comment> commentListByPost = getCommentListByPost(commentList, post);
            return new PostRetrieveRespDto(post, tagByPostList, postImageByTagList, Long.valueOf(postLikeByPostList.size()), isMemberLikedPostInPostLikeList(memberLogined, postLikeByPostList), Long.valueOf(commentListByPost.size()), memberLogined);
        });

        //when
        Slice<PostRetrieveRespDto> retrieveRespDtoSlice = postService.localRetrieveAll(PageRequest.of(0, 3), Optional.empty(), Optional.ofNullable(loginUid));

        //then
        assertThat(retrieveRespDtoSlice).isEqualTo(expected);
    }

    @DisplayName("로그인 하고 태그로 게시글 전체 조회")
    @Test
    public void 로그인_하고_태그로_게시글_전체_조회() throws Exception {
        //given
        String loginUid = "qwer";
        Member memberLogined = createMember(loginUid, email, name, nickname, imgUrl, introduce);
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);

        Post post1 = createPost(member, content);
        post1.setId(1L);
        Post post2 = createPost(member, content);
        post2.setId(2L);
        Post post3 = createPost(member, content);
        post3.setId(3L);

        List<Tag> tagList = new ArrayList<>();
        List<PostImage> postImageList = new ArrayList<>();
        List<PostLike> postLikeList = new ArrayList<>();
        List<Comment> commentList = new ArrayList<>();
        
        for (int i = 0; i < 3; i++) {
            tagList.add(createTag(post1, "tag1"));
            postImageList.add(createPostImage("postImageUrl", post1));
            postLikeList.add(createPostLike(member, post1));
            commentList.add(createComment(member, post1));
        }

        for (int i = 0; i < 3; i++) {
            tagList.add(createTag(post2, "tag2"));
            postImageList.add(createPostImage("postImageUrl", post2));
            postLikeList.add(createPostLike(member, post2));
            commentList.add(createComment(member, post2));
        }

        for (int i = 0; i < 3; i++) {
            tagList.add(createTag(post3, "tag3"));
            postImageList.add(createPostImage("postImageUrl", post3));
            postLikeList.add(createPostLike(member, post3));
            commentList.add(createComment(member, post3));
        }


        SliceImpl<Post> postSlice = new SliceImpl<>(List.of(post1));

        when(postRepository.findAllSlicing(any(), any())).thenReturn(postSlice);
        when(tagRepository.findTagsByPostIds(any())).thenReturn(tagList);
        when(postImageRepository.findPostImagesByPostIds(any())).thenReturn(postImageList);
        when(postLikeRepository.findPostLikesByPostIds(any())).thenReturn(postLikeList);
        when(commentRepository.findByPostIds(any())).thenReturn(commentList);
        when(memberRepository.findByUid(any())).thenReturn(Optional.ofNullable(memberLogined));

        Slice<PostRetrieveRespDto> expected = postSlice.map(post -> {
            List<Tag> tagByPostList = getTagListByPost(tagList, post);
            List<PostImage> postImageByTagList = getPostImageListByPost(postImageList, post);
            List<PostLike> postLikeByPostList = getPostLikeListByPost(postLikeList, post);
            List<Comment> commentListByPost = getCommentListByPost(commentList, post);
            return new PostRetrieveRespDto(post, tagByPostList, postImageByTagList, Long.valueOf(postLikeByPostList.size()), isMemberLikedPostInPostLikeList(memberLogined, postLikeByPostList), Long.valueOf(commentListByPost.size()), memberLogined);
        });

        //when
        Slice<PostRetrieveRespDto> retrieveRespDtoSlice = postService.localRetrieveAll(PageRequest.of(0, 3), Optional.of("tag1"), Optional.ofNullable(loginUid));

        //then
        assertThat(retrieveRespDtoSlice).isEqualTo(expected);
    }
    
    @DisplayName("게시글 수정")
    @Test
    public void 게시글_수정() throws Exception {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Post post = createPost(member, content);
        post.setId(1L);

        String editContent = "수정 내용";
        List<String> tagStrList = List.of("tag1", "tag2", "tag3");
        List<Tag> tagList = tagStrList.stream().map(t -> createTag(post, t)).collect(Collectors.toList());

        List<String> postImgStrList = List.of("img1", "img2", "img3");
        List<PostImage> postImageList = postImgStrList.stream().map(p -> createPostImage(p, post)).collect(Collectors.toList());

        long likeCnt = 3L;

        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(tagRepository.deleteByPostId(any())).thenReturn(3L);
        doNothing().when(postImageRepository).deleteByPostId(any());

        when(tagRepository.save(any()))
                .thenReturn(createTag(post, tagStrList.get(0)))
                .thenReturn(createTag(post, tagStrList.get(1)))
                .thenReturn(createTag(post, tagStrList.get(2)));

        when(postImageRepository.save(any()))
                .thenReturn(createPostImage(postImgStrList.get(0), post))
                .thenReturn(createPostImage(postImgStrList.get(1), post))
                .thenReturn(createPostImage(postImgStrList.get(2), post));

        when(postLikeRepository.countByPostId(any())).thenReturn(likeCnt);

        PostWriteReqDto postWriteReqDto = new PostWriteReqDto(editContent, tagStrList, postImgStrList);


        //when
        PostEditRespDto result = postService.editPost(member, post.getId(), postWriteReqDto);

        post.setContent(editContent);
        PostEditRespDto expected = new PostEditRespDto(post, tagList, postImageList, likeCnt);

        //then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("좋아요 하지 않은 게시글 좋아요")
    @Test
    public void 게시글_좋아요() throws Exception {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Post post = createPost(member, content);
        post.setId(1L);

        Long likeCnt = 5L;

        when(postRepository.findById(any())).thenReturn(Optional.ofNullable(post));
        when(postLikeRepository.findPostLikeByPostIdAndMemberId(any(), any())).thenReturn(Optional.empty());
        when(postLikeRepository.countByPostId(any())).thenReturn(likeCnt);

        //when
        Long result = postService.like(member, post.getId());

        //then
        assertThat(result).isEqualTo(likeCnt);
    }

    @DisplayName("이미 좋아요 한 게시글 좋아요")
    @Test
    public void 이미_좋아요_한_게시글_좋아요() throws Exception {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Post post = createPost(member, content);
        PostLike postLike = createPostLike(member, post);
        post.setId(1L);

        Long likeCnt = 5L;

        Long expected = likeCnt - 1;
        when(postRepository.findById(any())).thenReturn(Optional.ofNullable(post));
        when(postLikeRepository.findPostLikeByPostIdAndMemberId(any(), any())).thenReturn(Optional.of(postLike));
        when(postLikeRepository.deleteByPostIdAndMemberId(any(), any())).thenReturn(expected);
        when(postLikeRepository.countByPostId(any())).thenReturn(expected);

        //when
        Long result = postService.like(member, post.getId());

        //then
        assertThat(result).isEqualTo(expected);
    }

    private boolean isMemberLikedPostInPostLikeList(Member member, List<PostLike> postLikeList) {
        boolean flag = false;

        for (PostLike postLike : postLikeList) {
            if (postLike.getMember() == member) {
                flag = true;
                break;
            }
        }
        return flag;
    }


    private List<Tag> getTagListByPost(List<Tag> findTagList, Post post) {
        List<Tag> tagList = new ArrayList<>();
        for (Tag tag : findTagList) {
            if (tag.getPost() == post) {
                tagList.add(tag);
            }
        }
        return tagList;
    }

    private List<PostImage> getPostImageListByPost(List<PostImage> findPostImageList, Post post) {
        List<PostImage> postImageList = new ArrayList<>();
        for (PostImage postImage : findPostImageList) {
            if (postImage.getPost() == post) {
                postImageList.add(postImage);
            }
        }
        return postImageList;
    }

    private List<Comment> getCommentListByPost(List<Comment> findComment, Post post) {
        List<Comment> commentList = new ArrayList<>();
        for (Comment comment : findComment) {
            if (comment.getPost() == post) {
                commentList.add(comment);
            }
        }
        return commentList;
    }

    private List<PostLike> getPostLikeListByPost(List<PostLike> findPostLikeList, Post post) {
        List<PostLike> postLikeList = new ArrayList<>();
        for (PostLike postLike : findPostLikeList) {
            if (postLike.getPost() == post) {
                postLikeList.add(postLike);
            }
        }
        return postLikeList;
    }

    private Comment createComment(Member member, Post post1) {
        return Comment.builder()
                .content(content)
                .member(member)
                .post(post1)
                .build();
    }

    private Tag createTag(Post post, String tagName) {
        return Tag.builder()
                .post(post)
                .tag(tagName)
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

    private Post createPost(Member member, String content) {
        return Post.builder()
                .member(member)
                .content(content)
                .build();
    }

    private PostLike createPostLike(Member member, Post post) {
        return PostLike.builder()
                .member(member)
                .post(post)
                .build();
    }

    private PostImage createPostImage(String postImgUrl, Post post) {
        return PostImage
                .builder()
                .post(post)
                .imgUrl(postImgUrl)
                .build();
    }

    private PostBookmark createPostBookmark(Post post, Member member) {

        return PostBookmark.builder()
                .post(post)
                .member(member)
                .build();
    }
}