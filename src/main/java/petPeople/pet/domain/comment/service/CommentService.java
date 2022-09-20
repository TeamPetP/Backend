package petPeople.pet.domain.comment.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import petPeople.pet.config.auth.AuthFilterContainer;
import petPeople.pet.controller.comment.dto.req.CommentEditReqDto;
import petPeople.pet.controller.comment.dto.req.CommentWriteReqDto;
import petPeople.pet.controller.comment.dto.resp.CommentEditRespDto;
import petPeople.pet.controller.comment.dto.resp.CommentWriteRespDto;
import petPeople.pet.controller.post.dto.resp.CommentRetrieveRespDto;
import petPeople.pet.controller.post.dto.resp.CommentRetrieveWithCountRespDto;
import petPeople.pet.domain.comment.entity.Comment;
import petPeople.pet.domain.comment.entity.CommentLike;
import petPeople.pet.domain.comment.repository.commentLike.CommentLikeRepository;
import petPeople.pet.domain.comment.repository.comment.CommentRepository;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.member.repository.MemberRepository;
import petPeople.pet.domain.notification.entity.Notification;
import petPeople.pet.domain.notification.repository.NotificationRepository;
import petPeople.pet.domain.post.entity.Post;
import petPeople.pet.domain.post.repository.post.PostRepository;
import petPeople.pet.exception.CustomException;
import petPeople.pet.exception.ErrorCode;
import petPeople.pet.filter.JwtFilter;
import petPeople.pet.filter.MockJwtFilter;
import petPeople.pet.util.RequestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;
    private final NotificationRepository notificationRepository;
    private final FirebaseAuth firebaseAuth;
    private final MemberRepository memberRepository;
    private final AuthFilterContainer authFilterContainer;

    @Transactional
    public CommentWriteRespDto writeComment(Member member, CommentWriteReqDto commentWriteRequestDto, Long postId) {

        Post findPost = validateOptionalPost(findOptionalPostWithId(postId));

        Comment saveComment = saveComment(createParentComment(member, findPost, commentWriteRequestDto));
        saveNotification(saveComment, member, findPost);
        return new CommentWriteRespDto(saveComment);
    }

    @Transactional
    public CommentWriteRespDto writeChildComment(Member member, CommentWriteReqDto commentWriteRequestDto, Long postId, Long parentCommentId) {

        Post findPost = validateOptionalPost(findOptionalPostWithId(postId));

        Comment parent = validateOptionalComment(findOptionalComment(parentCommentId));

        if (parent.getParent() != null) {
            throwException(ErrorCode.BAD_REQUEST, "자식 댓글에 댓글을 달 수 없습니다.");
        }

        Comment saveComment = saveComment(createChildComment(member, findPost, commentWriteRequestDto, parent));
        saveNotification(saveComment, member, findPost);
        return new CommentWriteRespDto(saveComment);
    }

    public CommentRetrieveWithCountRespDto prodRetrieveAll(Long postId, String header) {
        List<Comment> commentList = findAllCommentByPostIdWithFetchJoinMember(postId);
        List<CommentLike> findCommentLikeList = getCommentLikesByPostId(postId);
        Long commentCnt = countCommentByPostId(postId);

        if (header == null) {
            return new CommentRetrieveWithCountRespDto(commentCnt, commentListMapToRespDtoWithNoLogin(commentList, findCommentLikeList));
        } else{
            Member member = getProdMemberByHeader(header);
            return new CommentRetrieveWithCountRespDto(commentCnt, commentListMapToRespDtoWithLogin(commentList, findCommentLikeList, member));
        }
    }

    public CommentRetrieveWithCountRespDto localRetrieveAll(Long postId, String header) {
        List<Comment> commentList = findAllCommentByPostIdWithFetchJoinMember(postId);
        List<CommentLike> findCommentLikeList = getCommentLikesByPostId(postId);
        Long commentCnt = countCommentByPostId(postId);

        if (header == null) {
            return new CommentRetrieveWithCountRespDto(commentCnt, commentListMapToRespDtoWithNoLogin(commentList, findCommentLikeList));
        } else{
            Member member = getLocalMemberByHeader(header);
            return new CommentRetrieveWithCountRespDto(commentCnt, commentListMapToRespDtoWithLogin(commentList, findCommentLikeList, member));
        }
    }

    private Long countCommentByPostId(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    private List<CommentRetrieveRespDto> commentListMapToRespDtoWithNoLogin(List<Comment> commentList, List<CommentLike> findCommentLikeList) {
        return commentList.stream().map(comment -> {
            List<CommentLike> commentLikeList = getCommentLikeListByComment(findCommentLikeList, comment);

            return new CommentRetrieveRespDto(comment, commentLikeList.size(), null, findCommentLikeList, null);
        }).collect(Collectors.toList());
    }

    private List<CommentRetrieveRespDto> commentListMapToRespDtoWithLogin(List<Comment> commentList, List<CommentLike> findCommentLikeList, Member member) {
        return commentList.stream().map(comment -> {
            List<CommentLike> commentLikeList = getCommentLikeListByComment(findCommentLikeList, comment);

            return new CommentRetrieveRespDto(comment, commentLikeList.size(), isMemberLikedComment(member, commentLikeList), findCommentLikeList, member);
        }).collect(Collectors.toList());
    }

    private Member getProdMemberByHeader(String header) {
        Member member;
        if (authFilterContainer.getFilter() instanceof JwtFilter) {
            FirebaseToken firebaseToken = decodeToken(header);
            member = validateOptionalMember(findOptionalMemberByUid(firebaseToken.getUid()));
        } else {
            member = validateOptionalMember(findOptionalMemberByUid(header));
        }
        return member;
    }

    private Member getLocalMemberByHeader(String header) {
        return validateOptionalMember(findOptionalMemberByUid(header));
    }

    @Transactional
    public CommentEditRespDto editComment(Member member, Long commentId, CommentEditReqDto commentEditReqDto) {
        Comment findComment = validateOptionalComment(findOptionalComment(commentId));
        validateAuthorization(member, findComment);

        editCommentContent(commentEditReqDto.getContent(), findComment);

        return new CommentEditRespDto(findComment, getCommentLikeCount(commentId));
    }


    @Transactional
    public void deleteComment(Member member, Long commentId) {
        Comment comment = validateOptionalComment(findOptionalComment(commentId));
        validateAuthorization(member, comment);

        if (comment.getParent() == null) {
            deleteChildComment(comment);
        }

        deleteCommentLikeByCommentId(commentId);
        notificationRepository.deleteNotificationByOwnerMemberIdAndCommentId(member.getId(), commentId);
        notificationRepository.deleteNotificationByMemberIdAndWriteCommentId(member.getId(), commentId);
        deleteCommentByCommentId(commentId);
    }

    private void throwException(ErrorCode errorCode, String message) {
        throw new CustomException(errorCode, message);
    }

    private Member validateOptionalMember(Optional<Member> optionalMember) {
        return optionalMember
                .orElseThrow(() ->
                        new CustomException(ErrorCode.NOT_FOUND_MEMBER, "존재하지 않은 회원입니다."));
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

    @Transactional
    public Long likeComment(Member member, Long commentId) {
        Comment findComment = validateOptionalComment(findOptionalComment(commentId));
        Long postId = findComment.getPost().getId();
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_POST, "해당하는 게시글이 존재하지 않습니다."));

        if(isOptionalCommentLikePresent(member, commentId)){
            deleteCommentLikeByCommentIdAndMemberId(member, commentId);
        } else {
            savePostLike(member, commentId, findPost);
            saveNotification(member, commentId, findComment, findPost);
        }
        return commentLikeRepository.countByCommentId(commentId);
    }

    private List<CommentLike> getCommentLikesByPostId(Long postId) {
        return commentLikeRepository.findCommentLikesByPostId(postId);
    }

    private Long getCommentLikeCount(Long commentId) {
        Long commentLikeCount = commentLikeRepository.countByCommentId(commentId);
        return commentLikeCount;
    }

    private void deleteChildComment(Comment comment) {
        List<Comment> childComment = comment.getChild();
        for (Comment c : childComment) {
            Member childMember = c.getMember();
            deleteComment(childMember, c.getId());
        }
    }

    private void saveNotification(Member member, Long commentId, Comment findComment, Post findPost) {
        if (isNotSameMember(member, findComment.getMember())) {
            Optional<Notification> optionalNotification = notificationRepository.findByMemberIdAndCommentId(member.getId(), commentId);
            createLikeCommentNotification(member, findComment, optionalNotification, findPost);
        }
    }

    private void createLikeCommentNotification(Member member, Comment findComment, Optional<Notification> optionalNotification, Post findPost) {
        if (!optionalNotification.isPresent()) {
            Notification notification = Notification.builder()
                    .comment(findComment)//댓글 엔티티
                    .post(findPost)
                    .member(member)//좋아요를 누른 회원 엔티티
                    .ownerMember(findComment.getMember())//게시글의 회원
                    .build();

            notificationRepository.save(notification);
        }
    }

    private void saveNotification(Comment saveComment, Member member, Post findPost) {
        if (isNotSameMember(member, findPost.getMember())) {
            saveRepositoryNotification(createNotification(member, findPost, saveComment));
        }
    }

    private Notification createNotification(Member member, Post post, Comment writeComment) {
        return Notification.builder()
                .writeComment(writeComment)
                .post(post)
                .ownerMember(post.getMember()) //게시글 작성자
                .member(member) //게시글에 댓글을 단 사용자
                .build();
    }

    private void saveRepositoryNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    private void savePostLike(Member member, Long commentId, Post post) {
        commentLikeRepository.save(createCommentLike(member, commentId, post));
    }

    private CommentLike createCommentLike(Member member, Long commentId, Post post) {
        return CommentLike.builder()
                .member(member)
                .comment(validateOptionalComment(findOptionalComment(commentId)))
                .post(post)
                .build();
    }

    private void deleteCommentLikeByCommentIdAndMemberId(Member member, Long commentId) {
        commentLikeRepository.deleteByCommentIdAndMemberId(member.getId(), commentId);
    }

    private boolean isOptionalCommentLikePresent(Member member, Long commentId) {
        return findOptionalCommentLikeByMemberIdAndCommentId(member, commentId).isPresent();
    }

    private Optional<CommentLike> findOptionalCommentLikeByMemberIdAndCommentId(Member member, Long commentId) {
        return commentLikeRepository.findCommentLikeByCommentIdAndMemberId(member.getId(), commentId);
    }

    private void deleteCommentByCommentId(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    private void deleteCommentLikeByCommentId(Long commentId) {
        commentLikeRepository.deleteByCommentId(commentId);
    }


    private Optional<Member> findOptionalMemberByUid(String uid) {
        return memberRepository.findByUid(uid);
    }

    private ArrayList<CommentLike> getCommentLikeListByComment(List<CommentLike> findCommentLikeList, Comment comment) {
        ArrayList<CommentLike> commentLikeList = new ArrayList<>();

        for (CommentLike commentLike : findCommentLikeList) {
            if (commentLike.getComment() == comment) {
                commentLikeList.add(commentLike);
            }
        }
        return commentLikeList;
    }

    private boolean isMemberLikedComment(Member member, List<CommentLike> commentLikeList) {
        boolean flag = false;
        for (CommentLike commentLike : commentLikeList) {
            if (commentLike.getMember() == member) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    private List<CommentLike> findCommentLikesByCommentIds(List<Long> ids) {
        return commentLikeRepository.findCommentLikesByCommentIds(ids);
    }

    private List<Long> getCommentId(List<Comment> content) {
        ArrayList<Long> ids = new ArrayList<>();
        for (Comment comment : content) {
            ids.add(comment.getId());
        }
        return ids;
    }

    private List<Comment> findAllCommentByPostIdWithFetchJoinMember(Long postId) {
        return commentRepository.findAllByIdWithFetchJoinMemberPaging(postId);
    }

    private Optional<Post> findOptionalPostWithId(Long postId) {
        return postRepository.findById(postId);
    }

    private Comment validateOptionalComment(Optional<Comment> optionalComment) {
        return optionalComment.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COMMENT, "존재하지 않는 댓글입니다."));
    }

    private Post validateOptionalPost(Optional<Post> optionalPost) {
        return optionalPost.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_POST, "존재하지 않은 게시글입니다."));
    }

    private void editCommentContent(String content, Comment findComment) {
        findComment.setContent(content);
    }

    private void validateAuthorization(Member member, Comment findComment) {
        if (isNotSameMember(member, findComment.getMember())) {
            throw new CustomException(ErrorCode.FORBIDDEN_MEMBER, "해당 댓글에 대한 권한이 없습니다.");
        }
    }

    private boolean isNotSameMember(Member member, Member findMember) {
        return findMember != member;
    }

    private Optional<Comment> findOptionalComment(Long commentId) {
        return commentRepository.findById(commentId);
    }

    private Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    private Comment createParentComment(Member member, Post findPost, CommentWriteReqDto commentWriteRequestDto) {
        return Comment.builder()
                .member(member)
                .content(commentWriteRequestDto.getContent())
                .post(findPost)
                .build();
    }

    private Comment createChildComment(Member member, Post findPost, CommentWriteReqDto commentWriteRequestDto, Comment parentComment) {
        return Comment.builder()
                .member(member)
                .parent(parentComment)
                .content(commentWriteRequestDto.getContent())
                .post(findPost)
                .build();
    }
}
