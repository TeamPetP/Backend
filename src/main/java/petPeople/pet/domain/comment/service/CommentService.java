package petPeople.pet.domain.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petPeople.pet.controller.comment.dto.req.CommentEditReqDto;
import petPeople.pet.controller.comment.dto.req.CommentWriteReqDto;
import petPeople.pet.controller.comment.dto.resp.CommentEditRespDto;
import petPeople.pet.controller.comment.dto.resp.CommentWriteRespDto;
import petPeople.pet.controller.post.dto.resp.CommentRetrieveRespDto;
import petPeople.pet.domain.comment.entity.Comment;
import petPeople.pet.domain.comment.entity.CommentLike;
import petPeople.pet.domain.comment.repository.CommentLikeRepository;
import petPeople.pet.domain.comment.repository.CommentRepository;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.notification.entity.Notification;
import petPeople.pet.domain.notification.repository.NotificationRepository;
import petPeople.pet.domain.post.entity.Post;
import petPeople.pet.domain.post.repository.PostRepository;
import petPeople.pet.exception.CustomException;
import petPeople.pet.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;
    private final UserDetailsService userDetailsService;
    private final NotificationRepository notificationRepository;

    @Transactional
    public CommentWriteRespDto writeComment(Member member, CommentWriteReqDto commentWriteRequestDto, Long postId) {

        Post findPost = validateOptionalPost(findOptionalPostWithId(postId));

        if (commentWriteRequestDto.getParentId() == null) {
            Comment saveComment = saveComment(createParentComment(member, findPost, commentWriteRequestDto));
            saveNotification(saveComment, member, findPost);
            return new CommentWriteRespDto(saveComment);
        }
        Comment parent = commentRepository.findById(commentWriteRequestDto.getParentId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COMMENT));
        Comment child = createChildComment(member, findPost, commentWriteRequestDto, parent);
        Comment saveComment = saveComment(child);
        saveNotification(saveComment, member, findPost);
        return new CommentWriteRespDto(saveComment);
    }


    public Slice<CommentRetrieveRespDto> retrieveAll(Long postId, String header, Pageable pageable) {
        Slice<Comment> commentSlice = findAllCommentByPostIdWithFetchJoinMember(postId, pageable);
        List<CommentLike> findCommentLikeList = getCommentLikesByPostId(postId);
        /**
         * 모든 CommentLike는 현재 post엔티티가 없음. 그러므로 commentLike엔티티에 postId를 식별할 수 있는 엔티티를 추가하여
         * 모든 commentLike를 가져오는 게 아니라 해당 post에 맞는 commentLike만 가져오도록 변경 ㅇㄸ
         * 그러면 그걸 dto에 넘겨줘서 거기에 맞게 로직 설정
         *
         * 그리고 PostCommentController에서 댓글 작성 API는 dto로 parentCommentId를 받는 게 낫지 않을까?
         * 그러면 댓글작성 api + 대댓글 작성 api도 하나 더 만들어야 할텐데
         */


        //@OneToMany를 쓰지 않기 위해 이런 방법으로?
        if (header == null) {
            return commentSlice.map(comment -> {
                ArrayList<CommentLike> commentLikeList = getCommentLikeListByComment(findCommentLikeList, comment);

                return createNoLoginCommentRetrieveRespDto(comment, commentLikeList, findCommentLikeList);
            });
        } else{
            Member member = getLocalMemberByHeader(header);
            return commentSlice.map(comment -> {
                ArrayList<CommentLike> commentLikeList = getCommentLikeListByComment(findCommentLikeList, comment);

                boolean flag = commentLikeFlag(member, commentLikeList);
                return new CommentRetrieveRespDto(comment, (long) commentLikeList.size(), flag, findCommentLikeList, member.getId());
            });
        }
    }

    private List<CommentLike> getCommentLikesByPostId(Long postId) {
        return commentLikeRepository.findCommentLikesByPostId(postId);
    }

    @Transactional
    public CommentEditRespDto editComment(Member member, Long commentId, CommentEditReqDto commentEditReqDto) {
        Comment findComment = validateOptionalComment(findOptionalComment(commentId));
        validateAuthorization(member, findComment);

        editCommentContent(commentEditReqDto.getContent(), findComment);

        return new CommentEditRespDto(findComment, getCommentLikeCount(commentId));
    }

    private Long getCommentLikeCount(Long commentId) {
        Long commentLikeCount = commentLikeRepository.countByCommentId(commentId);
        return commentLikeCount;
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

    private void deleteChildComment(Comment comment) {
        List<Comment> childComment = comment.getChild();
        for (Comment c : childComment) {
            Member childMember = c.getMember();
            deleteComment(childMember, c.getId());
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

    private Member getLocalMemberByHeader(String header) {
        return (Member) userDetailsService.loadUserByUsername(header);
    }

    private CommentRetrieveRespDto createNoLoginCommentRetrieveRespDto(Comment comment, ArrayList<CommentLike> commentLikeList, List<CommentLike> findCommentLikeList) {
        return new CommentRetrieveRespDto(comment, (long) commentLikeList.size(), null, findCommentLikeList, null);
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

    private boolean commentLikeFlag(Member member, ArrayList<CommentLike> commentLikeList) {
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

    private Slice<Comment> findAllCommentByPostIdWithFetchJoinMember(Long postId, Pageable pageable) {
        return commentRepository.findAllByIdWithFetchJoinMemberPaging(postId, pageable);
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
                .createdDate(LocalDateTime.now())
                .build();
    }

    private Comment createChildComment(Member member, Post findPost, CommentWriteReqDto commentWriteRequestDto, Comment parentComment) {
        return Comment.builder()
                .member(member)
                .parent(parentComment)
                .content(commentWriteRequestDto.getContent())
                .post(findPost)
                .createdDate(LocalDateTime.now())
                .build();
    }
}
