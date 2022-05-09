package petPeople.pet.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostImageRepository postImageRepository;

    @Transactional
    public PostWriteRespDto write(Member member, PostWriteReqDto postWriteReqDto) {
        Post savePost = savePost(createPost(member, postWriteReqDto));

        List<Tag> tagList = createTagList(postWriteReqDto, savePost);
        List<PostImage> postImageList = createPostImageList(postWriteReqDto, savePost);

        return new PostWriteRespDto(savePost, tagList, postImageList);
    }

    private List<PostImage> createPostImageList(PostWriteReqDto postWriteReqDto, Post savePost) {
        List<PostImage> postImageList = new ArrayList<>();

        for (String url : postWriteReqDto.getImgUrlList()) {
            postImageList.add(savePostImage(createPostImage(savePost, url)));
        }
        return postImageList;
    }

    private List<Tag> createTagList(PostWriteReqDto postWriteReqDto, Post savePost) {
        List<Tag> tagList = new ArrayList<>();

        for (String t : postWriteReqDto.getTagList()) {
            tagList.add(saveTag(createTag(savePost, t)));
        }
        return tagList;
    }

    private PostImage savePostImage(PostImage postImage) {
        return postImageRepository.save(postImage);
    }

    private Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @NotNull
    private Post savePost(Post post) {
        return postRepository.save(post);
    }

    private PostImage createPostImage(Post savePost, String url) {
        return PostImage.builder()
                .post(savePost)
                .imgUrl(url)
                .build();
    }

    private Tag createTag(Post post, String t) {
        return Tag.builder()
                .post(post)
                .tag(t)
                .build();
    }

    private Post createPost(Member member, PostWriteReqDto postWriteReqDto) {
        return Post.builder()
                .member(member)
                .content(postWriteReqDto.getContent())
                .build();
    }

}
