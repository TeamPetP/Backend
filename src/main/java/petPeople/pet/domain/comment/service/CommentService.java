package petPeople.pet.domain.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petPeople.pet.controller.comment.dto.req.CommentWriteReqDto;
import petPeople.pet.controller.comment.dto.resp.CommentWriteRespDto;
import petPeople.pet.domain.comment.entity.Comment;
import petPeople.pet.domain.comment.repository.CommentRepository;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.post.entity.Post;
import petPeople.pet.domain.post.repository.PostRepository;
import petPeople.pet.exception.CustomException;
import petPeople.pet.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentWriteRespDto write(Member member, CommentWriteReqDto commentWriteRequestDto, Long postId) {

        Post findPost = validateOptionalPost(postId);
        Comment saveComment = saveComment(createComment(member, findPost, commentWriteRequestDto));

        return new CommentWriteRespDto(saveComment);
    }

    private Post validateOptionalPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> {
                    throw new CustomException(ErrorCode.NOT_FOUND_POST, "게시글 ID에 맞는 게시글을 찾을 수 없습니다.");
                });
    }

    private Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    private Comment createComment(Member member, Post findPost, CommentWriteReqDto commentWriteRequestDto) {
        return Comment.builder()
                .member(member)
                .content(commentWriteRequestDto.getContent())
                .post(findPost)
                .createdDate(LocalDateTime.now())
                .build();
    }
}
