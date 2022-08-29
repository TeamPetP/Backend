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
import petPeople.pet.config.auth.AuthFilterContainer;
import petPeople.pet.controller.post.dto.req.PostWriteReqDto;
import petPeople.pet.controller.post.dto.resp.PostEditRespDto;
import petPeople.pet.controller.post.dto.resp.PostRetrieveRespDto;
import petPeople.pet.controller.post.dto.resp.PostWriteRespDto;
import petPeople.pet.datastructure.PostRelatedEntity;
import petPeople.pet.domain.comment.entity.Comment;
import petPeople.pet.domain.comment.repository.commentLike.CommentLikeRepository;
import petPeople.pet.domain.comment.repository.comment.CommentRepository;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.member.repository.MemberRepository;
import petPeople.pet.domain.notification.entity.Notification;
import petPeople.pet.domain.notification.repository.NotificationRepository;
import petPeople.pet.domain.post.entity.*;
import petPeople.pet.domain.post.repository.post.PostRepository;
import petPeople.pet.domain.post.repository.post_bookmark.PostBookmarkRepository;
import petPeople.pet.domain.post.repository.post_image.PostImageRepository;
import petPeople.pet.domain.post.repository.post_like.PostLikeRepository;
import petPeople.pet.domain.post.repository.tag.TagRepository;
import petPeople.pet.exception.CustomException;
import petPeople.pet.exception.ErrorCode;
import petPeople.pet.filter.MockJwtFilter;
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
    private final AuthFilterContainer authFilterContainer;
    private final NotificationRepository notificationRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    //의도와 구현을 분리
    @Transactional
    public PostWriteRespDto write(Member member, PostWriteReqDto postWriteReqDto) {

        Post savePost = savePost(createPost(member, postWriteReqDto.getContent()));
        List<Tag> tags = saveTagList(postWriteReqDto.getTagList(), savePost);
        List<PostImage> imgUrls = savePostImageList(postWriteReqDto.getImgUrlList(), savePost);

        return new PostWriteRespDto(savePost, tags, imgUrls);
    }

    public PostRetrieveRespDto localRetrieveOne(Long postId, Optional<String> optionalHeader) {

        Post post = validateOptionalPost(findOptionalPostFetchJoinedWithMember(postId));
        List<Tag> tagList = findTagList(postId);
        List<PostImage> postImageList = findPostImageList(postId);
        Long likeCnt = countPostLikeByPostId(postId);
        Long commentCnt = countCommentByPostId(postId);

        if (isLogined(optionalHeader)) {//로그인할 경우
            Member member = getLocalMemberByHeader(optionalHeader.get());
            Long memberId = member.getId();

            //좋아요를 눌렀는지
            boolean optionalPostLikePresent = isOptionalPostLikePresent(findOptionalPostLikeByMemberIdAndPostId(memberId, postId));
            return createLoginPostRetrieveRespDto(post, tagList, postImageList, likeCnt, optionalPostLikePresent, commentCnt, member);
        } else {//로그인하지 않을 경우
            return createNoLoginPostRetrieveRespDto(post, tagList, postImageList, likeCnt, commentCnt);
        }
    }

    public Slice<PostRetrieveRespDto> localRetrieveAll(Pageable pageable, Optional<String> optionalTag, Optional<String> optionalHeader) {
        Slice<Post> postSlice = findAllPostSlicing(pageable, optionalTag);
        List<Long> ids = getPostIdList(postSlice.getContent());

        PostRelatedEntity postRelatedEntity = createPostChildList(findTagsByPostIds(ids), findPostImagesByPostIds(ids), findPostLikesByPostIds(ids), findCommentsByPostIds(ids));

        if (isLogined(optionalHeader)) {
            Member member = validateOptionalMember(findOptionalMemberByUid(optionalHeader.get()));
            return getPostRetrieveRespDtos(postSlice, postRelatedEntity, member);
        } else {
            return getPostNoLoginRetrieveRespDtos(postSlice, postRelatedEntity);
        }
    }

    public PostRetrieveRespDto retrieveOne(Long postId, Optional<String> optionalHeader) {

        Post post = validateOptionalPost(findOptionalPostFetchJoinedWithMember(postId));
        List<Tag> tagList = findTagList(postId);
        List<PostImage> postImageList = findPostImageList(postId);
        Long likeCnt = countPostLikeByPostId(postId);
        Long commentCnt = countCommentByPostId(postId);

        if (isLogined(optionalHeader)) {
            String uid = decodeToken(optionalHeader.get()).getUid();
            Member member = validateOptionalMember(findOptionalMemberByUid(uid));

            boolean optionalPostLikePresent = isOptionalPostLikePresent(findOptionalPostLikeByMemberIdAndPostId(member.getId(), postId));
            return createLoginPostRetrieveRespDto(post, tagList, postImageList, likeCnt, optionalPostLikePresent, commentCnt, member);
        } else {
            return createNoLoginPostRetrieveRespDto(post, tagList, postImageList, likeCnt, commentCnt);
        }
    }

    public Slice<PostRetrieveRespDto> retrieveAll(Pageable pageable, Optional<String> optionalTag, Optional<String> optionalHeader) {
        Slice<Post> postSlice = findAllPostSlicing(pageable, optionalTag);
        List<Long> ids = getPostIdList(postSlice.getContent());

        PostRelatedEntity postRelatedEntity = createPostChildList(findTagsByPostIds(ids), findPostImagesByPostIds(ids), findPostLikesByPostIds(ids), findCommentsByPostIds(ids));

        if (isLogined(optionalHeader)) {
            FirebaseToken firebaseToken = decodeToken(optionalHeader.get());
            Member member = validateOptionalMember(findOptionalMemberByUid(firebaseToken.getUid()));
            return getPostRetrieveRespDtos(postSlice, postRelatedEntity, member);
        } else {
            return getPostNoLoginRetrieveRespDtos(postSlice, postRelatedEntity);
        }
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
        Post findPost = validateOptionalPost(findOptionalPost(postId));

        if (isOptionalPostLikePresent(findOptionalPostLikeByMemberIdAndPostId(member.getId(), postId))) {
            deletePostLikeByPostIdAndMemberId(postId, member.getId());
        } else {
            savePostLike(createPostLike(member, validateOptionalPost(findOptionalPost(postId))));
            saveNotification(postId, member, findPost);
        }
        return countPostLikeByPostId(postId);
    }

    @Transactional
    public void delete(Member member, Long postId) {
        Post post = validateOptionalPost(findOptionalPost(postId));
        List<Comment> findCommentList = findByCommentByPostId(postId);
        validateMemberAuthorization(member, post.getMember());

        deleteTagByPostId(postId);
        deletePostImageByPostId(postId);
        deletePostLikeByPostId(postId);
        deleteNotificationByMemberIdAndPostId(member, postId);

        for (Comment comment : findCommentList) {
            deleteCommentLikeByCommentId(comment);
            deleteNotificationByMemberIdAndCommentId(member, comment);
            deleteNotification(member, comment);
        }

        findCommentList = findByCommentByPostId(postId);

        List<Long> childCommentIds = new ArrayList<>();
        for (Comment comment : findCommentList) {
            List<Comment> child = comment.getChild();
            for (Comment childComment : child) {
                childCommentIds.add(childComment.getId());
            }
        }

        commentRepository.deleteByCommentIds(childCommentIds);
        commentRepository.deleteCommentByPostId(postId);

        deletePostByPostId(postId);
    }

    public Slice<PostRetrieveRespDto> retrieveMemberLikedPost(Member member, Pageable pageable) {

        Slice<PostLike> postLikesSlice = findPostLikeByMemberId(member, pageable);

        List<Long> ids = getPostIdByPostLikeId(postLikesSlice.getContent());

        PostRelatedEntity postRelatedEntity = createPostChildList(findTagsByPostIds(ids), findPostImagesByPostIds(ids), findPostLikesByPostIds(ids), findCommentsByPostIds(ids));

        return postLikesSlice.map(postLike -> {
            List<Tag> tagList = getTagListByPost(postRelatedEntity.getTagList(), postLike.getPost());
            List<PostImage> postImageList = getPostImageListByPost(postRelatedEntity.getPostImageList(), postLike.getPost());
            List<PostLike> postLikeList = getPostLikeListByPost(postRelatedEntity.getPostLikeList(), postLike.getPost());
            List<Comment> commentList = getCommentListByPost(postRelatedEntity.getCommentList(), postLike.getPost());

            return new PostRetrieveRespDto(postLike.getPost(), tagList, postImageList, Long.valueOf(postLikeList.size()), isMemberLikedPostInPostLikeList(member, postLikeList), Long.valueOf(commentList.size()), member);
        });
    }

    private void deleteNotification(Member member, Comment comment) {
        notificationRepository.deleteNotificationByMemberIdAndWriteCommentId(member.getId(), comment.getId());
    }

    private void deleteNotificationByMemberIdAndCommentId(Member member, Comment comment) {
        notificationRepository.deleteNotificationByOwnerMemberIdAndCommentId(member.getId(), comment.getId());
    }

    private void deleteCommentLikeByCommentId(Comment comment) {
        commentLikeRepository.deleteByCommentId(comment.getId());
    }

    private List<Comment> findByCommentByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    public long countMemberPost(Member member) {
        return postRepository.countByMemberId(member.getId());
    }

    public Slice<PostRetrieveRespDto> retrieveMemberPost(Member member, Pageable pageable, String header) {
        Slice<Post> postSlice = findAllPostByMemberIdSlicing(member, pageable);
        List<Long> ids = getPostIdList(postSlice.getContent());

        PostRelatedEntity postRelatedEntity = createPostChildList(findTagsByPostIds(ids), findPostImagesByPostIds(ids), findPostLikesByPostIds(ids), findCommentsByPostIds(ids));
        return postSliceMapToRespDtoWithLogin(header, postSlice, postRelatedEntity);
    }

    public FirebaseToken decodeToken(String header) {
        try {
            header = "Bearer " + header;
            String token = RequestUtil.getAuthorizationToken(header);
            return firebaseAuth.verifyIdToken(token);
        } catch (IllegalArgumentException | FirebaseAuthException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "{\"code\":\"INVALID_TOKEN\", \"message\":\"" + e.getMessage() + "\"}");
        }
    }

    private void saveNotification(Long postId, Member member, Post findPost) {
        if (isNotSameMember(member, findPost.getMember())) {
            if (!isExistMemberLikePostNotification(findPost.getId(), member)) {
                saveNotification(createNotification(member, findPost));
            }
        }
    }

    private Notification createNotification(Member member, Post post) {
        return Notification.builder()
                .post(post)
                .ownerMember(post.getMember()) //게시글 작성자
                .member(member) //게시글에 좋아요한 사용자
                .build();
    }


    private void deleteNotificationByMemberIdAndPostId(Member member, Long postId) {
        notificationRepository.deleteNotificationByMemberIdAndPostId(member.getId(), postId);
    }

    private boolean isExistMemberLikePostNotification(Long postId, Member member) {
        return notificationRepository.findByMemberIdAndPostId(member.getId(), postId).isPresent();
    }

    private void saveNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    private Slice<PostLike> findPostLikeByMemberId(Member member, Pageable pageable) {
        return postLikeRepository.findByMemberIdWithFetchJoinPost(member.getId(), pageable);
    }

    private Slice<Post> findAllPostByMemberIdSlicing(Member member, Pageable pageable) {
        return postRepository.findAllByMemberIdSlicing(member.getId(), pageable);
    }

    private Member validateOptionalMember(Optional<Member> optionalMember) {
        return optionalMember
                .orElseThrow(() ->
                        new CustomException(ErrorCode.NOT_FOUND_MEMBER, "존재하지 않은 회원입니다."));
    }

    private Optional<Member> findOptionalMemberByUid(String uid) {
        return memberRepository.findByUid(uid);
    }

    private void throwException(ErrorCode errorCode, String message) {
        throw new CustomException(errorCode, message);
    }

    private PostRelatedEntity createPostChildList(List<Tag> findTagList, List<PostImage> findPostImageList, List<PostLike> findPostLikeList, List<Comment> findCommentList) {
        return PostRelatedEntity.builder()
                .tagList(findTagList)
                .postImageList(findPostImageList)
                .postLikeList(findPostLikeList)
                .commentList(findCommentList)
                .build();
    }

    private Member getLocalMemberByHeader(String header) {
        return (Member) userDetailsService.loadUserByUsername(header);
    }

    private Slice<PostRetrieveRespDto> postSliceMapToRespDtoWithLogin(String header, Slice<Post> postPage, PostRelatedEntity postRelatedEntity) {

        if (authFilterContainer.getFilter() instanceof MockJwtFilter) {
            Member member = validateOptionalMember(findOptionalMemberByUid(header));
            return getPostRetrieveRespDtos(postPage, postRelatedEntity, member);
        } else {
            FirebaseToken firebaseToken = decodeToken(header);
            Member member = validateOptionalMember(findOptionalMemberByUid(firebaseToken.getUid()));
            return getPostRetrieveRespDtos(postPage, postRelatedEntity, member);
        }
    }

    private Slice<PostRetrieveRespDto> getPostRetrieveRespDtos(Slice<Post> postPage, PostRelatedEntity postRelatedEntity, Member member) {
        return postPage.map(post -> {
            List<Tag> tagList = getTagListByPost(postRelatedEntity.getTagList(), post);
            List<PostImage> postImageList = getPostImageListByPost(postRelatedEntity.getPostImageList(), post);
            List<PostLike> postLikeList = getPostLikeListByPost(postRelatedEntity.getPostLikeList(), post);
            List<Comment> commentList = getCommentListByPost(postRelatedEntity.getCommentList(), post);

            return new PostRetrieveRespDto(post, tagList, postImageList, Long.valueOf(postLikeList.size()), isMemberLikedPostInPostLikeList(member, postLikeList), Long.valueOf(commentList.size()), member);
        });
    }

    private Slice<PostRetrieveRespDto> getPostNoLoginRetrieveRespDtos(Slice<Post> postPage, PostRelatedEntity postRelatedEntity) {
        return postPage.map(post -> {
            List<Tag> tagList = getTagListByPost(postRelatedEntity.getTagList(), post);
            List<PostImage> postImageList = getPostImageListByPost(postRelatedEntity.getPostImageList(), post);
            List<PostLike> postLikeList = getPostLikeListByPost(postRelatedEntity.getPostLikeList(), post);
            return new PostRetrieveRespDto(post, tagList, postImageList, Long.valueOf(postLikeList.size()), null, Long.valueOf(getCommentListByPost(postRelatedEntity.getCommentList(), post).size()), null);
        });
    }

    private List<Comment> findCommentsByPostIds(List<Long> ids) {
        return commentRepository.findByPostIds(ids);
    }

    private boolean isLogined(Optional<String> optionalHeader) {
        return optionalHeader.isPresent();
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



    private PostRetrieveRespDto createLoginPostRetrieveRespDto(Post post, List<Tag> tagList, List<PostImage> postImageList, Long likeCnt, boolean flag, Long commentCnt, Member member) {
        return new PostRetrieveRespDto(post, tagList, postImageList, likeCnt, flag, commentCnt, member);
    }

    private PostRetrieveRespDto createNoLoginPostRetrieveRespDto(Post post, List<Tag> tagList, List<PostImage> postImageList, Long likeCnt, Long commentCnt) {
        return new PostRetrieveRespDto(post, tagList, postImageList, likeCnt, null, commentCnt, null);
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

    private List<Comment> getCommentListByPost(List<Comment> findComment, Post post) {
        List<Comment> commentList = new ArrayList<>();
        for (Comment comment : findComment) {
            if (comment.getPost() == post) {
                commentList.add(comment);
            }
        }
        return commentList;
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

    private List<Long> getPostIdList(List<Post> content) {
        List<Long> ids = new ArrayList<>();
        for (Post post : content) {
            ids.add(post.getId());
        }
        return ids;
    }

    private List<Long> getPostIdByPostLikeId(List<PostLike> content) {
        List<Long> ids = new ArrayList<>();
        for (PostLike postLike : content) {
            ids.add(postLike.getPost().getId());
        }
        return ids;
    }

    private Slice<Post> findAllPostSlicing(Pageable pageable, Optional<String> tag) {
        return postRepository.findAllSlicing(pageable, tag);
    }

    private Long countPostLikeByPostId(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }

    private void validateMemberAuthorization(Member member, Member targetMember) {
        if (isNotSameMember(member, targetMember)) {
            throwException(ErrorCode.FORBIDDEN_MEMBER, "해당 게시글에 권한이 없습니다.");
        }
    }

    private boolean isNotSameMember(Member member, Member postMember) {
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

    private Long countCommentByPostId(Long postId) {
        return commentRepository.countByPostId(postId);
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

    private Post findPostOrElseThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> {
                    throw new CustomException(ErrorCode.NOT_FOUND_POST, "게시글 ID 에 맞는 게시글이 없습니다.");
                });
    }

}