package petPeople.pet.domain.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petPeople.pet.controller.post.dto.req.PostWriteReqDto;
import petPeople.pet.controller.post.dto.resp.PostRetrieveRespDto;
import petPeople.pet.controller.post.dto.resp.PostWriteRespDto;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.post.entity.Post;
import petPeople.pet.domain.post.entity.PostImage;
import petPeople.pet.domain.post.entity.PostLike;
import petPeople.pet.domain.post.entity.Tag;
import petPeople.pet.domain.post.repository.PostImageRepository;
import petPeople.pet.domain.post.repository.PostLikeRepository;
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
    private final PostLikeRepository postLikeRepository;

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
                findPostImageList(postId),
                countPostLikeByPostId(postId)
        );
    }

    @Transactional
    public PostWriteRespDto editPost(Member member, Long postId, PostWriteReqDto postWriteReqDto) {
        Post findPost = validateOptionalPost(findOptionalPost(postId));
        validateAuthorization(member, findPost.getMember());

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

    public Page<PostRetrieveRespDto> retrieveAll(Pageable pageable) {
        Page<Post> postPage = findAllPostByIdWithFetchJoinMemberPaging(pageable);

        List<Long> ids = getPostId(postPage.getContent());

        List<Tag> findTagList = findTagsByPostIds(ids);
        List<PostImage> findPostImageList = findPostImagesByPostIds(ids);
        List<PostLike> findPostLikeList = findPostLikesByPostIds(ids);

        Page<PostRetrieveRespDto> respDtoPage = postPage.map(post -> {
            List<Tag> tagList = getTagListByPost(findTagList, post);
            List<PostImage> postImageList = getPostImageListByPost(findPostImageList, post);
            List<PostLike> postLikeList = getPostLikeListByPost(findPostLikeList, post);

            return new PostRetrieveRespDto(post, tagList, postImageList, Long.valueOf(postLikeList.size()));
        });
        return respDtoPage;
    }

    @Transactional
    public Long like(Member member, Long postId) {

        if (isOptionalPostLikePresent(findOptionalPostLike(member, postId))) {
            deletePostLikeByPostId(postId);
        } else {
            savePostLike(createPostLike(member, validateOptionalPost(findOptionalPost(postId))));
        }

        return countPostLikeByPostId(postId);
    }

    @Transactional
    public void delete(Member member, Long postId) {
        Post post = validateOptionalPost(findOptionalPost(postId));
        validateAuthorization(member, post.getMember());

        deleteTagByPostId(postId);
        deletePostImageByPostId(postId);
        deletePostLikeByPostId(postId);
        deletePostByPostId(postId);
    }

    private void deletePostByPostId(Long postId) {
        postRepository.deleteById(postId);
    }

    private boolean isOptionalPostLikePresent(Optional<PostLike> optionalPostLike) {
        return optionalPostLike.isPresent();
    }

    private void deletePostLikeByPostId(Long postId) {
        postLikeRepository.deleteByPostId(postId);
    }

    private PostLike savePostLike(PostLike postLike) {
        return postLikeRepository.save(postLike);
    }

    private Optional<PostLike> findOptionalPostLike(Member member, Long postId) {
        return postLikeRepository.findPostLikeByPostIdAndMemberId(postId, member.getId());
    }

    private PostLike createPostLike(Member member, Post post) {
        return PostLike.builder()
                .post(post)
                .member(member)
                .build();
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

    private List<PostImage> getPostImageListByPost(List<PostImage> findPostImageList, Post post) {
        List<PostImage> postImageList = new ArrayList<>();
        for (PostImage postImage : findPostImageList) {
            if (postImage.getPost() == post) {
                postImageList.add(postImage);
            }
        }
        return postImageList;
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

    private List<PostLike> findPostLikesByPostIds(List<Long> ids) {
        return postLikeRepository.findPostLikesByPostIds(ids);
    }

    private List<PostImage> findPostImagesByPostIds(List<Long> ids) {
        return postImageRepository.findPostImagesByPostIds(ids);
    }

    private List<Tag> findTagsByPostIds(List<Long> ids) {
        return tagRepository.findTagsByPostIds(ids);
    }

    private List<Long> getPostId(List<Post> content) {
        List<Long> ids = new ArrayList<>();
        for (Post post : content) {
            ids.add(post.getId());
        }
        return ids;
    }

    private Page<Post> findAllPostByIdWithFetchJoinMemberPaging(Pageable pageable) {
        return postRepository.findAllPostByIdWithFetchJoinMemberPaging(pageable);
    }

    private Long countPostLikeByPostId(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }

    private void validateAuthorization(Member member, Member targetMember) {
        if (isaNotSameMember(member, targetMember)) {
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
