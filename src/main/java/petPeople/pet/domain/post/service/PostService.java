package petPeople.pet.domain.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petPeople.pet.controller.post.dto.req.PostWriteReqDto;
import petPeople.pet.controller.post.dto.resp.PostRetrieveRespDto;
import petPeople.pet.controller.post.dto.resp.PostWriteRespDto;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.post.entity.Post;
import petPeople.pet.domain.post.entity.PostImage;
import petPeople.pet.domain.post.entity.Tag;
import petPeople.pet.domain.post.repository.PostImageRepository;
import petPeople.pet.domain.post.repository.PostRepository;
import petPeople.pet.domain.post.repository.TagRepository;
import petPeople.pet.exception.CustomException;
import petPeople.pet.exception.ErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostImageRepository postImageRepository;

    @Transactional
    public PostWriteRespDto write(Member member, PostWriteReqDto postWriteReqDto) {
        Post savePost = savePost(createPost(member, postWriteReqDto.getContent()));
        return new PostWriteRespDto(
                savePost,
                saveTagList(postWriteReqDto.getTagList(), savePost),
                savePostImageList(postWriteReqDto.getImgUrlList(), savePost)
        );
    }

    public PostRetrieveRespDto retrieveOne(Long postId) {
        return new PostRetrieveRespDto(
                validateOptionalPost(findOptionalPostFetchJoinedWithMember(postId)),
                findTagList(postId),
                findPostImageList(postId)
        );
    }

    @Transactional
    public PostWriteRespDto editPost(Member member, Long postId, PostWriteReqDto postWriteReqDto) {
        Post findPost = validateOptionalPost(findOptionalPost(postId));
        validateOwnPost(member, findPost.getMember());

        editPostContent(findPost, postWriteReqDto.getContent());

        //기존 태그, 이미지 삭제
        deleteTagByPostId(postId);
        deletePostImageByPostId(postId);

        return new PostWriteRespDto(
                findPost,
                saveTagList(postWriteReqDto.getTagList(), findPost),
                savePostImageList(postWriteReqDto.getImgUrlList(), findPost)
        );
    }

    private void validateOwnPost(Member member, Member postMember) {
        if (isaNotSameMember(member, postMember)) {
            throw new CustomException(ErrorCode.FORBIDDEN_MEMBER, "해당 게시글에 권한이 없습니다.");
        }
    }

    private boolean isaNotSameMember(Member member, Member postMember) {
        return member != postMember;
    }

    private void editPostContent(Post post, String content) {
        post.setContent(content);
    }

    private void deletePostImageByPostId(Long postId) {
        postImageRepository.deleteByPostId(postId);
    }

    private void deleteTagByPostId(Long postId) {
        tagRepository.deleteByPostId(postId);
    }

    private List<PostImage> findPostImageList(Long postId) {
        return postImageRepository.findByPostId(postId);
    }

    private List<Tag> findTagList(Long postId) {
        return tagRepository.findByPostId(postId);
    }

    private Optional<Post> findOptionalPost(Long postId) {
        return postRepository.findById(postId);
    }

    private Post validateOptionalPost(Optional<Post> optionalPost) {
        return optionalPost.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_POST, "존재하지 않은 게시글입니다."));
    }

    private Optional<Post> findOptionalPostFetchJoinedWithMember(Long postId) {
        return postRepository.findByIdWithFetchJoinMember(postId);
    }

    private List<PostImage> savePostImageList(List<String> urls, Post post) {
        List<PostImage> postImageList = new ArrayList<>();

        for (String url : urls) {
            postImageList.add(savePostImage(createPostImage(post, url)));
        }
        return postImageList;
    }

    private List<Tag> saveTagList(List<String> tags, Post post) {
        List<Tag> tagList = new ArrayList<>();

        for (String t : tags) {
            tagList.add(saveTag(createTag(post, t)));
        }
        return tagList;
    }

    private PostImage savePostImage(PostImage postImage) {
        return postImageRepository.save(postImage);
    }

    private Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }

    private Post savePost(Post post) {
        return postRepository.save(post);
    }

    private PostImage createPostImage(Post post, String url) {
        return PostImage.builder()
                .post(post)
                .imgUrl(url)
                .build();
    }

    private Tag createTag(Post post, String t) {
        return Tag.builder()
                .post(post)
                .tag(t)
                .build();
    }

    private Post createPost(Member member, String content) {
        return Post.builder()
                .member(member)
                .content(content)
                .build();
    }

}
