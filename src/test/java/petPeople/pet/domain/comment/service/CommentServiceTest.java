package petPeople.pet.domain.comment.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import petPeople.pet.domain.comment.repository.CommentLikeRepository;
import petPeople.pet.domain.comment.repository.CommentRepository;
import petPeople.pet.domain.post.repository.PostRepository;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)//테스트 클래스가 Mockito를 사용함을 의미
class CommentServiceTest {

    final String uid = "jang";
    final String email = "789456jang@naver.com";
    final String name = "장대영";
    final String nickname = "longstick0";
    final String imgUrl = "www.imgurl.com";
    final String introduce = "완다비전 개꿀잼이야";

    @Mock
    CommentRepository commentRepository;
    @Mock
    CommentLikeRepository commentLikeRepository;
    @Mock
    PostRepository postRepository;

    @InjectMocks
    CommentService commentService;



    @Test
    void write() {
    }

    @Test
    void retrieveAll() {
    }

    @Test
    void editComment() {
    }

    @Test
    void deleteComment() {
    }

    @Test
    void likeComment() {
    }
}