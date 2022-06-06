package petPeople.pet.domain.post.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import petPeople.pet.controller.post.dto.req.PostWriteReqDto;
import petPeople.pet.controller.post.dto.resp.PostEditRespDto;
import petPeople.pet.controller.post.dto.resp.PostRetrieveRespDto;
import petPeople.pet.controller.post.dto.resp.PostWriteRespDto;
import petPeople.pet.domain.datastructure.PostChildList;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.member.repository.MemberRepository;
import petPeople.pet.domain.post.entity.*;
import petPeople.pet.domain.post.repository.*;
import petPeople.pet.exception.CustomException;
import petPeople.pet.exception.ErrorCode;
import petPeople.pet.util.RequestUtil;

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
    private final UserDetailsService userDetailsService;
    private final PostBookmarkRepository postBookmarkRepository;
    private final FirebaseAuth firebaseAuth;
    private final MemberRepository memberRepository;


    //의도와 구현을 분리
    @Transactional
    public PostWriteRespDto write(Member member, PostWriteReqDto postWriteReqDto) {
        Post savePost = savePost(createPost(member, postWriteReqDto.getContent()));
        return new PostWriteRespDto(
                savePost,
                saveTagList(postWriteReqDto.getTagList(), savePost),
                savePostImageList(postWriteReqDto.getImgUrlList(), savePost)
        );
    }

    public PostRetrieveRespDto localRetrieveOne(Long postId, Optional<String> optionalHeader) {

        Post post = validateOptionalPost(findOptionalPostFetchJoinedWithMember(postId));
        List<Tag> tagList = findTagList(postId);
        List<PostImage> postImageList = findPostImageList(postId);
        Long likeCnt = countPostLikeByPostId(postId);

        PostRetrieveRespDto respDto;

        if (isLogined(optionalHeader)) {
            Long memberId = getLocalMemberByHeader(optionalHeader.get()).getId();
            boolean optionalPostLikePresent = isOptionalPostLikePresent(findOptionalPostLikeByMemberIdAndPostId(memberId, postId));
            respDto = createLoginPostRetrieveRespDto(post, tagList, postImageList, likeCnt, optionalPostLikePresent);
        } else {
            respDto = createNoLoginPostRetrieveRespDto(post, tagList, postImageList, likeCnt);
        }

        return respDto;
    }

    public PostRetrieveRespDto retrieveOne(Long postId, Optional<String> optionalHeader) {

        Post post = validateOptionalPost(findOptionalPostFetchJoinedWithMember(postId));
        List<Tag> tagList = findTagList(postId);
        List<PostImage> postImageList = findPostImageList(postId);
        Long likeCnt = countPostLikeByPostId(postId);

        PostRetrieveRespDto respDto;

        if (isLogined(optionalHeader)) {
            String uid = decodeToken(optionalHeader.get()).getUid();
            Member member = validateOptionalMember(findOptionalMemberByUid(uid));

            boolean optionalPostLikePresent = isOptionalPostLikePresent(findOptionalPostLikeByMemberIdAndPostId(member.getId(), postId));
            respDto = createLoginPostRetrieveRespDto(post, tagList, postImageList, likeCnt, optionalPostLikePresent);
        } else {
            respDto = createNoLoginPostRetrieveRespDto(post, tagList, postImageList, likeCnt);
        }

        return respDto;
    }

    private Member validateOptionalMember(Optional<Member> optionalMember) {
        return optionalMember
                .orElseThrow(() ->
                        new CustomException(ErrorCode.NOT_FOUND_MEMBER, "존재하지 않은 회원입니다."));
    }

    private Optional<Member> findOptionalMemberByUid(String uid) {
        return memberRepository.findByUid(uid);
    }

    public FirebaseToken decodeToken(String header) {
        try {
            String token = RequestUtil.getAuthorizationToken(header);
            return firebaseAuth.verifyIdToken(token);
        } catch (IllegalArgumentException | FirebaseAuthException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "{\"code\":\"INVALID_TOKEN\", \"message\":\"" + e.getMessage() + "\"}");
        }
    }

    public Slice<PostRetrieveRespDto> localRetrieveAll(Pageable pageable, Optional<String> optionalTag, Optional<String> optionalHeader) {

        Slice<Post> postSlice;
        if (isSearchTag(optionalTag)) {
            postSlice = findAllPostByTagSlicing(pageable, optionalTag.get());
        } else {
            postSlice = findAllPostSlicing(pageable);
        }
        return postSliceMapToRespDtoSlice(optionalHeader, postSlice);
    }

    public Slice<PostRetrieveRespDto> retrieveAll(Pageable pageable, Optional<String> optionalTag, Optional<String> optionalHeader) {

        Slice<Post> postSlice;
        if (isSearchTag(optionalTag)) {
            postSlice = findAllPostByTagSlicing(pageable, optionalTag.get());
        } else {
            postSlice = findAllPostSlicing(pageable);
        }
        return postSliceMapToRespDtoSlice(optionalHeader, postSlice);
    }

    @Transactional
    public PostEditRespDto editPost(Member member, Long postId, PostWriteReqDto postWriteReqDto) {
        Post findPost = validateOptionalPost(findOptionalPost(postId));
        validateMemberAuthorization(member, findPost.getMember());

        editPostContent(findPost, postWriteReqDto.getContent());

        //기존 태그, 이미지 삭제
        deleteTagByPostId(postId);
        deletePostImageByPostId(postId);

        return new PostEditRespDto(
                findPost,
                saveTagList(postWriteReqDto.getTagList(), findPost),
                savePostImageList(postWriteReqDto.getImgUrlList(), findPost),
                countPostLikeByPostId(postId)
        );
    }

    @Transactional
    public Long like(Member member, Long postId) {

        if (isOptionalPostLikePresent(findOptionalPostLikeByMemberIdAndPostId(member.getId(), postId))) {
            deletePostLikeByPostIdAndMemberId(postId, member.getId());
        } else {
            savePostLike(createPostLike(member, validateOptionalPost(findOptionalPost(postId))));
        }

        return countPostLikeByPostId(postId);
    }

    @Transactional
    public void delete(Member member, Long postId) {
        Post post = validateOptionalPost(findOptionalPost(postId));
        validateMemberAuthorization(member, post.getMember());

        deleteTagByPostId(postId);
        deletePostImageByPostId(postId);
        deletePostLikeByPostId(postId);
        deletePostByPostId(postId);
    }

    @Transactional
    public void bookmark(Member member, Long postId) {
        if (isOptionalPostBookmarkPresent(findPostBookmarkByMemberIdAndPostId(member.getId(), postId))) {
            throwException(ErrorCode.BOOKMARKED_POST, "이미 북마크를 눌렀습니다.");
        } else {
            savePostBookmark(createPostBookmark(member, validateOptionalPost(findOptionalPost(postId))));
        }
    }

    @Transactional
    public void deleteBookmark(Member member, Long postId) {
        if (isOptionalPostBookmarkPresent(findPostBookmarkByMemberIdAndPostId(member.getId(), postId))) {
            postBookmarkRepository.deleteByMemberIdAndPostId(member.getId(), postId);
        } else {
            throwException(ErrorCode.NEVER_BOOKMARKED_POST, "북마크 하지 않은 피드입니다.");
        }
    }

    public Slice<PostRetrieveRespDto> retrieveMemberPost(Member member, Pageable pageable, String header) {
        Slice<Post> postSlice = findAllPostByMemberIdSlicing(member, pageable);
        List<Long> ids = getPostId(postSlice.getContent());
        PostChildList postChildList = createPostChildList(findTagsByPostIds(ids), findPostImagesByPostIds(ids), findPostLikesByPostIds(ids));
        return postSliceMapToRespDtoWithLogin(header, postSlice, postChildList);
    }

    private Slice<Post> findAllPostByMemberIdSlicing(Member member, Pageable pageable) {
        return postRepository.findAllByMemberIdSlicing(member.getId(), pageable);
    }

    private void savePostBookmark(PostBookmark postBookmark) {
        postBookmarkRepository.save(postBookmark);
    }

    private Optional<PostBookmark> findPostBookmarkByMemberIdAndPostId(Long memberId, Long postId) {
        return postBookmarkRepository.findByMemberIdAndPostId(memberId, postId);
    }

    private void throwException(ErrorCode errorCode, String message) {
        throw new CustomException(errorCode, message);
    }

    private boolean isOptionalPostBookmarkPresent(Optional<PostBookmark> optionalPostBookmark) {
        return optionalPostBookmark.isPresent();
    }

    private PostBookmark createPostBookmark(Member member, Post post) {
        return PostBookmark.builder()
                .post(post)
                .member(member)
                .build();
    }

    private PostChildList createPostChildList(List<Tag> findTagList, List<PostImage> findPostImageList, List<PostLike> findPostLikeList) {
        return PostChildList.builder()
                .tagList(findTagList)
                .postImageList(findPostImageList)
                .postLikeList(findPostLikeList)
                .build();
    }

    private Member getLocalMemberByHeader(String header) {
        return (Member) userDetailsService.loadUserByUsername(header);
    }

    private boolean isSearchTag(Optional<String> optionalTag) {
        return optionalTag.isPresent();
    }

    private Slice<PostRetrieveRespDto> postSliceMapToRespDtoSlice(Optional<String> optionalHeader, Slice<Post> postSlice) {
        List<Long> ids = getPostId(postSlice.getContent());

        PostChildList postChildList = createPostChildList(findTagsByPostIds(ids), findPostImagesByPostIds(ids), findPostLikesByPostIds(ids));

        if (isLogined(optionalHeader)) {
            return postSliceMapToRespDtoWithLogin(optionalHeader.get(), postSlice, postChildList);
        } else {
            return postSliceMapToRespDtoWithNoLogin(postSlice, postChildList);
        }
    }

    private boolean isLogined(Optional<String> optionalHeader) {
        return optionalHeader.isPresent();
    }

    private Slice<Post> findAllPostByTagSlicing(Pageable pageable, String tag) {
        return postRepository.findPostSlicingByTag(pageable, tag);
    }

    private Slice<PostRetrieveRespDto> postSliceMapToRespDtoWithLogin(String header, Slice<Post> postPage, PostChildList postChildList) {
        FirebaseToken firebaseToken = decodeToken(header);
        Member member = validateOptionalMember(findOptionalMemberByUid(firebaseToken.getUid()));
        return postPage.map(post -> {
            List<Tag> tagList = getTagListByPost(postChildList.getTagList(), post);
            List<PostImage> postImageList = getPostImageListByPost(postChildList.getPostImageList(), post);
            List<PostLike> postLikeList = getPostLikeListByPost(postChildList.getPostLikeList(), post);

            return createLoginPostRetrieveRespDto(post, tagList, postImageList, Long.valueOf(postLikeList.size()), isMemberLikedPostInPostLikeList(member, postLikeList));
        });
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

    private Slice<PostRetrieveRespDto> postSliceMapToRespDtoWithNoLogin(Slice<Post> postPage, PostChildList postChildList) {
        return postPage.map(post -> {
            List<Tag> tagList = getTagListByPost(postChildList.getTagList(), post);
            List<PostImage> postImageList = getPostImageListByPost(postChildList.getPostImageList(), post);
            List<PostLike> postLikeList = getPostLikeListByPost(postChildList.getPostLikeList(), post);

            return createNoLoginPostRetrieveRespDto(post, tagList, postImageList, Long.valueOf(postLikeList.size()));
        });
    }

    private PostRetrieveRespDto createLoginPostRetrieveRespDto(Post post, List<Tag> tagList, List<PostImage> postImageList, Long likeCnt, boolean flag) {
        return new PostRetrieveRespDto(post, tagList, postImageList, likeCnt, flag);
    }

    private PostRetrieveRespDto createNoLoginPostRetrieveRespDto(Post post, List<Tag> tagList, List<PostImage> postImageList, Long likeCnt) {
        return new PostRetrieveRespDto(post, tagList, postImageList, likeCnt, null);
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

    private void deletePostLikeByPostIdAndMemberId(Long postId, Long memberId) {
        postLikeRepository.deleteByPostIdAndMemberId(postId, memberId);
    }

    private PostLike savePostLike(PostLike postLike) {
        return postLikeRepository.save(postLike);
    }

    private Optional<PostLike> findOptionalPostLikeByMemberIdAndPostId(Long memberId, Long postId) {
        return postLikeRepository.findPostLikeByPostIdAndMemberId(postId, memberId);
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

    private Slice<Post> findAllPostSlicing(Pageable pageable) {
        return postRepository.findAllSlicing(pageable);
    }

    private Long countPostLikeByPostId(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }

    private void validateMemberAuthorization(Member member, Member targetMember) {
        if (isaNotSameMember(member, targetMember)) {
            throwException(ErrorCode.FORBIDDEN_MEMBER, "해당 게시글에 권한이 없습니다.");
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