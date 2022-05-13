package petPeople.pet.domain.post.service;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import petPeople.pet.controller.post.dto.req.PostWriteReqDto;
import petPeople.pet.controller.post.dto.resp.PostEditRespDto;
import petPeople.pet.controller.post.dto.resp.PostRetrieveRespDto;
import petPeople.pet.controller.post.dto.resp.PostWriteRespDto;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.post.entity.*;
import petPeople.pet.domain.post.repository.*;
import petPeople.pet.exception.CustomException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)//테스트 클래스가 Mockito를 사용함을 의미합니다.
class PostServiceTest {

    final String uid = "abcd";
    final String email = "issiscv@naver.com";
    final String name = "김상운";
    final String nickname = "balladang";
    final String imgUrl = "www.imgurl.com";
    final String introduce = "잘지내요 우리";

    final List<String> tags = Arrays.asList("사진", "내새끼", "장난감");
    final List<String> imgUrls = Arrays.asList("www.방울이귀엽죠?.com", "www.imgaeABC.com");

    final String content = "게시글 및 피드입니다.";

    @Mock
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
    PostBookmarkRepository postBookmarkRepository;

    @InjectMocks
    PostService postService;

    Long id;
    Member member;
    PostWriteReqDto postWriteReqDto;
    Post post;
    List<Tag> tagList;
    List<PostImage> postImageList;
    List<PostLike> postLikeList;

    @BeforeEach
    void beforeEach() {
        id = 1L;
        member = createMember(uid, email, name, nickname, imgUrl, introduce);
        postWriteReqDto = createPostWriteReqDto(content, tags, imgUrls);
        post = createPost(member, postWriteReqDto.getContent());
        tagList = createTagList(tags, post);
        postImageList = createPostImageList(imgUrls, post);
        postLikeList = createPostLikeList();
    }

    @Test
    @DisplayName("게시글 작성 테스트")
    public void writePostTest() throws Exception {
        //given
        when(postRepository.save(any())).thenReturn(post);

        when(tagRepository.save(any()))
                .thenReturn(tagList.get(0))
                .thenReturn(tagList.get(1))
                .thenReturn(tagList.get(2));

        when(postImageRepository.save(any()))
                .thenReturn(postImageList.get(0))
                .thenReturn(postImageList.get(1));

        PostWriteRespDto respDto = new PostWriteRespDto(post, tagList, postImageList);

        //when
        PostWriteRespDto result = postService.write(member, postWriteReqDto);

        //then
        assertThat(result).isEqualTo(respDto);
    }

    @Test
    @DisplayName("로그인 하지 않고 게시글 단건 조회")
    public void noLoginRetrievePostTest() throws Exception {
        //given
        long likeCnt = 3L;
        when(postRepository.findByIdWithFetchJoinMember(any())).thenReturn(Optional.ofNullable(post));
        when(tagRepository.findByPostId(any())).thenReturn(tagList);
        when(postImageRepository.findByPostId(any())).thenReturn(postImageList);
        when(postLikeRepository.countByPostId(any())).thenReturn(likeCnt);

        PostRetrieveRespDto result = new PostRetrieveRespDto(post, tagList, postImageList, likeCnt, null);

        //when
        PostRetrieveRespDto postRetrieveRespDto = postService.localRetrieveOne(post.getId(), null);

        //then
        assertThat(postRetrieveRespDto).isEqualTo(result);
    }

    @Test
    @DisplayName("로그인 하고 좋아요 누른 게시글 단건 조회")
    public void loginRetrieveNotLikedPostTest() throws Exception {
        //given
        long likeCnt = 3L;

        String uid = "abcd";

        when(postRepository.findByIdWithFetchJoinMember(any())).thenReturn(Optional.ofNullable(post));
        when(tagRepository.findByPostId(any())).thenReturn(tagList);
        when(postImageRepository.findByPostId(any())).thenReturn(postImageList);
        when(postLikeRepository.countByPostId(any())).thenReturn(likeCnt);
        when(userDetailsService.loadUserByUsername(any())).thenReturn(member);
        when(postLikeRepository.findPostLikeByPostIdAndMemberId(any(), any())).thenReturn(Optional.ofNullable(postLikeList.get(0)));

        PostRetrieveRespDto result = new PostRetrieveRespDto(post, tagList, postImageList, likeCnt, true);

        //when
        PostRetrieveRespDto postRetrieveRespDto = postService.localRetrieveOne(post.getId(), uid);

        //then
        assertThat(postRetrieveRespDto).isEqualTo(result);
    }

    @Test
    @DisplayName("로그인 하고 좋아요 누르지 않은 게시글 단건 조회")
    public void loginRetrieveLikedPostTest() throws Exception {
        //given
        long likeCnt = 3L;

        String uid = "abcd";

        when(postRepository.findByIdWithFetchJoinMember(any())).thenReturn(Optional.ofNullable(post));
        when(tagRepository.findByPostId(any())).thenReturn(tagList);
        when(postImageRepository.findByPostId(any())).thenReturn(postImageList);
        when(postLikeRepository.countByPostId(any())).thenReturn(likeCnt);
        when(userDetailsService.loadUserByUsername(any())).thenReturn(member);
        when(postLikeRepository.findPostLikeByPostIdAndMemberId(any(), any())).thenReturn(Optional.empty());

        PostRetrieveRespDto result = new PostRetrieveRespDto(post, tagList, postImageList, likeCnt, false);

        //when
        PostRetrieveRespDto postRetrieveRespDto = postService.localRetrieveOne(post.getId(), uid);

        //then
        assertThat(postRetrieveRespDto).isEqualTo(result);
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회")
    public void retrieveNotFoundPostTest() throws Exception {
        //given
        when(postRepository.findByIdWithFetchJoinMember(any())).thenReturn(Optional.empty());

        //when
        //then
        assertThrows(CustomException.class, () -> postService.localRetrieveOne(post.getId(), null));
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    public void editPostTest() throws Exception {
        //given

        Long likeCnt = 3L;

        when(postRepository.findById(any())).thenReturn(Optional.ofNullable(post));

        when(tagRepository.save(any()))
                .thenReturn(tagList.get(0))
                .thenReturn(tagList.get(1))
                .thenReturn(tagList.get(2));

        when(postImageRepository.save(any()))
                .thenReturn(postImageList.get(0))
                .thenReturn(postImageList.get(1));

        when(postLikeRepository.countByPostId(any())).thenReturn(likeCnt);

        PostEditRespDto result = new PostEditRespDto(post, tagList, postImageList, likeCnt);

        //when
        PostEditRespDto respDto = postService.editPost(member, post.getId(), postWriteReqDto);

        //then
        verify(tagRepository, times(1)).deleteByPostId(any());
        verify(postImageRepository, times(1)).deleteByPostId(any());
        assertThat(respDto).isEqualTo(result);
    }

    @Test
    @DisplayName("권한 없는 게시글 수정 테스트")
    public void editNotOwnPostTest() throws Exception {
        //given
        Member postMember = createMember(uid, email, name, nickname, imgUrl, introduce);
        when(postRepository.findById(any())).thenReturn(Optional.ofNullable(post));

        //when
        //then
        assertThrows(CustomException.class, () -> postService.editPost(postMember, post.getId(), postWriteReqDto));
    }

    @Test
    @DisplayName("로그인 하지 않고 게시글 전체 조회 테스트")
    public void retrieveAllPostTest() throws Exception {
        //given
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<String> tags1 = Arrays.asList("사진1", "내새끼1", "장난감1");
        List<String> tags2 = Arrays.asList("사진2", "내새끼2", "장난감2");
        List<String> tags3 = Arrays.asList("사진3", "내새끼3", "장난감3");

        List<String> imgUrls1 = Arrays.asList("www.방울이귀엽죠?.com1", "www.qkddnfdlrnlduqwy.com1");
        List<String> imgUrls2 = Arrays.asList("www.방울이귀엽죠?.com2", "www.qkddnfdlrnlduqwy.com2");
        List<String> imgUrls3 = Arrays.asList("www.방울이귀엽죠?.com3", "www.qkddnfdlrnlduqwy.com3");

        PostWriteReqDto writeReqDto1 = createPostWriteReqDto("게시글1", tags1, imgUrls1);
        PostWriteReqDto writeReqDto2 = createPostWriteReqDto("게시글2", tags2, imgUrls2);
        PostWriteReqDto writeReqDto3 = createPostWriteReqDto("게시글3", tags3, imgUrls3);

        List<PostWriteReqDto> writeReqDtoList = Arrays.asList(writeReqDto1, writeReqDto2, writeReqDto3);

        List<Post> postList = createPostList(writeReqDtoList);

        PostLike postLike1 = new PostLike(id++, postList.get(0), createMember(uid, email, name, nickname, imgUrl, introduce));
        PostLike postLike2 = new PostLike(id++, postList.get(1), createMember(uid, email, name, nickname, imgUrl, introduce));
        PostLike postLike3 = new PostLike(id++, postList.get(2), createMember(uid, email, name, nickname, imgUrl, introduce));

        PageImpl<Post> postPage = new PageImpl<>(postList, pageRequest, postList.size());

        List<Tag> allTagList = addAllTagList(tags1, tags2, tags3, postList);
        List<PostImage> allPostImageList = addAllPostImageList(imgUrls1, imgUrls2, imgUrls3, postList);
        List<PostLike> allPostLikeList = Arrays.asList(postLike1, postLike2, postLike3);

        when(postRepository.findAllPostByIdWithFetchJoinMemberPaging(any())).thenReturn(postPage);
        when(tagRepository.findTagsByPostIds(any())).thenReturn(allTagList);
        when(postImageRepository.findPostImagesByPostIds(any())).thenReturn(allPostImageList);
        when(postLikeRepository.findPostLikesByPostIds(any())).thenReturn(allPostLikeList);

        Page<PostRetrieveRespDto> result = postPage.map(post -> {
            List<Tag> tags = getTagsByPost(allTagList, post);
            List<PostImage> postImages = getPostImagesByPost(allPostImageList, post);
            List<PostLike> postLikes = getPostsLikeByPost(allPostLikeList, post);

            return new PostRetrieveRespDto(post, tags, postImages, Long.valueOf(postLikes.size()), null);
        });

        //when
        Page<PostRetrieveRespDto> respDtoPage = postService.localRetrieveAll(pageRequest, null);

        //then
        assertThat(respDtoPage).isEqualTo(result);
    }

    @Test
    @DisplayName("중복 게시글 좋아요")
    public void duplicateLikePostTest() throws Exception {
        //given
        long result = 1L;

        PostLike postLike = new PostLike(id++, post, member);

        when(postLikeRepository.findPostLikeByPostIdAndMemberId(any(), any())).thenReturn(Optional.ofNullable(postLike));
        doNothing().when(postLikeRepository).deleteByPostIdAndMemberId(any(), any());
        when(postLikeRepository.countByPostId(any())).thenReturn(--result);

        //when
        Long likeCnt = postService.like(member, post.getId());

        //then
        assertThat(likeCnt).isEqualTo(0L);
        verify(postLikeRepository, times(1)).deleteByPostIdAndMemberId(any(), any());
    }

    @Test
    @DisplayName("게시글 좋아요")
    public void likePostTest() throws Exception {
        //given
        Long result = 0L;

        PostLike postLike = new PostLike(id++, post, member);

        when(postLikeRepository.findPostLikeByPostIdAndMemberId(any(), any())).thenReturn(Optional.empty());
        when(postRepository.findById(any())).thenReturn(Optional.ofNullable(post));
        when(postLikeRepository.save(any())).thenReturn(postLike);
        when(postLikeRepository.countByPostId(any())).thenReturn(++result);

        //when
        Long likeCnt = postService.like(member, post.getId());

        //then
        assertThat(likeCnt).isEqualTo(1L);

    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    public void deletePostTest() throws Exception {
        //given
        when(postRepository.findById(any())).thenReturn(Optional.ofNullable(post));

        doNothing().when(tagRepository).deleteByPostId(any());
        doNothing().when(postImageRepository).deleteByPostId(any());
        doNothing().when(postLikeRepository).deleteByPostId(any());
        doNothing().when(postRepository).deleteById(any());

        //when
        postService.delete(member, post.getId());

        //then
        verify(tagRepository, times(1)).deleteByPostId(any());
        verify(postImageRepository, times(1)).deleteByPostId(any());
        verify(postLikeRepository, times(1)).deleteByPostId(any());
        verify(postRepository, times(1)).deleteById(any());
    }

    @Test
    @DisplayName("없는 게시글 삭제 테스트")
    public void deleteNotOwnPostTest() throws Exception {
        //given
        when(postRepository.findById(any())).thenReturn(Optional.empty());

        //when
        //then
        assertThrows(CustomException.class, () -> postService.like(member, post.getId()));
    }

    // TODO: 2022-05-13 권한 없이 게시글 삭제 테스트 
    
    @Test
    @DisplayName("북마크하지 않은 게시글 북마크 테스트")
    public void bookmarkPost() throws Exception {
        //given
        PostBookmark postBookMark = createPostBookMark();
        when(postBookmarkRepository.findByMemberIdAndPostId(any(), any())).thenReturn(Optional.empty());
        when(postRepository.findById(any())).thenReturn(Optional.ofNullable(post));
        when(postBookmarkRepository.save(any())).thenReturn(postBookMark);

        //when
        postService.bookmark(member, post.getId());
        //then
        verify(postBookmarkRepository, times(1)).save(any());
    }
    
    @Test
    @DisplayName("북마크한 게시글 북마크 취소 테스트")
    public void deleteBookmarkPost() throws Exception {
        //given
        PostBookmark postBookMark = createPostBookMark();
        when(postBookmarkRepository.findByMemberIdAndPostId(any(), any())).thenReturn(Optional.ofNullable(postBookMark));
        doNothing().when(postBookmarkRepository).deleteByMemberIdAndPostId(any(), any());

        //when
        postService.deleteBookmark(member, post.getId());

        //then
        verify(postBookmarkRepository, times(1)).deleteByMemberIdAndPostId(any(), any());

    }

    @Test
    @DisplayName("북마크하지 않은 게시글 북마크 취소 테스트")
    public void deleteNeverBookmarkPost() throws Exception {
        //given
        when(postBookmarkRepository.findByMemberIdAndPostId(any(), any())).thenReturn(Optional.empty());
        //when
        assertThrows(CustomException.class, () -> postService.deleteBookmark(member, post.getId()));

    }

    private PostBookmark createPostBookMark() {
        return PostBookmark.builder()
                .id(id++)
                .member(member)
                .post(post)
                .build();
    }

    private List<PostLike> getPostsLikeByPost(List<PostLike> allPostLikeList, Post post) {
        List<PostLike> postLikes = new ArrayList<>();
        for (PostLike postLike : allPostLikeList) {
            if (postLike.getPost() == post) {
                postLikes.add(postLike);
            }
        }
        return postLikes;
    }

    private List<PostImage> addAllPostImageList(List<String> imgUrls1, List<String> imgUrls2, List<String> imgUrls3, List<Post> postList) {
        List<PostImage> postImageList = new ArrayList<>();
        postImageList.addAll(createPostImageList(imgUrls1, postList.get(0)));
        postImageList.addAll(createPostImageList(imgUrls2, postList.get(1)));
        postImageList.addAll(createPostImageList(imgUrls3, postList.get(2)));
        return postImageList;
    }

    private List<Tag> addAllTagList(List<String> tags1, List<String> tags2, List<String> tags3, List<Post> postList) {
        List<Tag> tagList = new ArrayList<>();
        tagList.addAll(createTagList(tags1, postList.get(0)));
        tagList.addAll(createTagList(tags2, postList.get(1)));
        tagList.addAll(createTagList(tags3, postList.get(2)));
        return tagList;
    }

    private List<PostImage> getPostImagesByPost(List<PostImage> postImageList, Post post) {
        List<PostImage> posts = new ArrayList<>();
        for (PostImage postImage : postImageList) {
            if (postImage.getPost() == post) {
                posts.add(postImage);
            }
        }
        return posts;
    }

    private List<Tag> getTagsByPost(List<Tag> tagList, Post post) {
        List<Tag> tags = new ArrayList<>();
        for (Tag tag : tagList) {
            if (tag.getPost() == post) {
                tags.add(tag);
            }
        }
        return tags;
    }

    private List<Post> createPostList(List<PostWriteReqDto> writeReqDtoList) {
        List<Post> postList = new ArrayList<>();
        for (PostWriteReqDto writeReqDto : writeReqDtoList) {
            postList.add(createPost(member, writeReqDto.getContent()));
        }
        return postList;
    }

    private Post createPost(Member member, String content) {
        return Post.builder()
                .id(id++)
                .member(member)
                .content(content)
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
        return Arrays.asList(new PostLike(id++, post, member), new PostLike(id++, post, member), new PostLike(id++, post, member));
    }

    private PostWriteReqDto createPostWriteReqDto(String content, List<String> tags, List<String> imgUrls) {
        return new PostWriteReqDto(content, tags, imgUrls);
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

}