package petPeople.pet.domain.post.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import petPeople.pet.controller.post.dto.req.PostWriteReqDto;
import petPeople.pet.controller.post.dto.resp.PostWriteRespDto;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.post.entity.Post;
import petPeople.pet.domain.post.entity.PostImage;
import petPeople.pet.domain.post.entity.Tag;
import petPeople.pet.domain.post.repository.PostImageRepository;
import petPeople.pet.domain.post.repository.PostRepository;
import petPeople.pet.domain.post.repository.TagRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)//테스트 클래스가 Mockito를 사용함을 의미합니다.
class PostServiceTest {

    @Mock
    PostRepository postRepository;
    @Mock
    TagRepository tagRepository;
    @Mock
    PostImageRepository postImageRepository;

    @InjectMocks
    PostService postService;

    final Member member = createMember();
    final List<String> tags = Arrays.asList("사진", "내새끼", "장난감");
    final List<String> imgUrls = Arrays.asList("www.방울이귀엽죠?.com", "www.qkddnfdlrnlduqwy.com");

    @Test
    @DisplayName("게시글 작성 테스트")
    public void writePostTest() throws Exception {
        //given
        PostWriteReqDto postWriteReqDto = new PostWriteReqDto("게시글 및 피드입니다.", tags, imgUrls);
        Post post = createPost(postWriteReqDto);

        when(postRepository.save(any())).thenReturn(post);

        List<Tag> tagList = createTagList(tags, post);
        when(tagRepository.save(any()))
                .thenReturn(tagList.get(0))
                .thenReturn(tagList.get(1))
                .thenReturn(tagList.get(2));

        List<PostImage> postImageList = createPostImageList(imgUrls, post);
        when(postImageRepository.save(any()))
                .thenReturn(postImageList.get(0))
                .thenReturn(postImageList.get(1));

        PostWriteRespDto respDto = new PostWriteRespDto(post, tagList, postImageList);

        //when
        PostWriteRespDto result = postService.write(member, postWriteReqDto);

        //then
        assertThat(result).isEqualTo(respDto);
    }

    private Member createMember() {
        return Member.builder()
                .id(1L)
                .uid("abcd")
                .email("abcd@daum.com")
                .name("성이름")
                .nickname("abcd")
                .imgUrl("https://www.balladang.com")
                .introduce("잘지내요 우리")
                .build();
    }

    private Post createPost(PostWriteReqDto postWriteReqDto) {
        return Post.builder()
                .id(2L)
                .member(member)
                .content(postWriteReqDto.getContent())
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
                .post(post)
                .tag(t)
                .build();
    }

    private PostImage createPostImage(Post savePost, String url) {
        return PostImage.builder()
                .post(savePost)
                .imgUrl(url)
                .build();
    }

    public List<PostImage> createPostImageList(List<String> urls, Post savePost) {
        List<PostImage> postImageList = new ArrayList<>();

        for (String url : urls) {
            postImageList.add((createPostImage(savePost, url)));
        }
        return postImageList;
    }
    
}