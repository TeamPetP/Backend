package petPeople.pet.domain.post.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import petPeople.pet.common.BaseControllerTest;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.member.repository.MemberRepository;
import petPeople.pet.domain.post.entity.Post;
import petPeople.pet.domain.post.entity.PostImage;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class PostImageRepositoryTest extends BaseControllerTest {

    @Autowired
    EntityManagerFactory emf;

    @Autowired
    PostImageRepository postImageRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    MemberRepository memberRepository;

    @DisplayName("post id 로 postImage 삭제")
    @Test
    public void deleteByPostIdTest() throws Exception {
        //given
        String uid = "abcd";
        String email = "issiscv@naver.com";
        String name = "김상운";
        String nickname = "balladang";
        String imgUrl = "www.imgurl.com";
        String introduce = "잘지내요 우리";

        String content = "강아지 좋아해요";

        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Post post = createPost(member, content);

        memberRepository.save(member);
        postRepository.save(post);

        for (int i = 1; i <= 10; i++) {
            postImageRepository.save(createPostImage(imgUrl + i, post));
        }

        //when
        postImageRepository.deleteByPostId(post.getId());
        List<PostImage> postImages = postImageRepository.findByPostId(post.getId());

        //then
        assertThat(postImages.isEmpty()).isTrue();//전부 삭제 후 조회를 하기 때문에 empty
    }


    @DisplayName("여러 post id 를 in 절에 넣어 postImage 조회")
    @Test
    public void findPostImagesByPostIdsTest() throws Exception {
        //given
        String uid = "abcd";
        String email = "issiscv@naver.com";
        String name = "김상운";
        String nickname = "balladang";
        String imgUrl = "www.imgurl.com";
        String introduce = "잘지내요 우리";

        String content = "강아지 좋아해요";

        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);

        Post post1 = createPost(member, content);
        Post post2 = createPost(member, content);
        Post post3 = createPost(member, content);

        List<Long> idList = new ArrayList<>();

        memberRepository.save(member);
        Post savePost1 = postRepository.save(post1);
        Post savePost2 = postRepository.save(post2);
        Post savePost3 = postRepository.save(post3);

        idList.add(savePost1.getId());
        idList.add(savePost2.getId());

        for (int i = 1; i <= 10; i++) {
            postImageRepository.save(createPostImage(imgUrl + i, savePost1));
        }

        for (int i = 1; i <= 5; i++) {
            postImageRepository.save(createPostImage(imgUrl + i, savePost2));
        }

        for (int i = 1; i <= 3; i++) {
            postImageRepository.save(createPostImage(imgUrl + i, post3));
        }

        //when
        List<PostImage> postImageList = postImageRepository.findPostImagesByPostIds(idList);

        //then
        assertThat(postImageList.size()).isEqualTo(15);
    }

    @DisplayName("postId 를 FK 로 갖고 있는 postImage 조회")
    @Test
    public void findByPostIdTest() throws Exception {
        //given
        String uid = "abcd";
        String email = "issiscv@naver.com";
        String name = "김상운";
        String nickname = "balladang";
        String imgUrl = "www.imgurl.com";
        String introduce = "잘지내요 우리";

        String content = "강아지 좋아해요";


        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Post post = createPost(member, content);

        memberRepository.save(member);
        postRepository.save(post);

        List<PostImage> postImageList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            postImageList.add(postImageRepository.save(createPostImage(imgUrl + i, post)));
        }

        //when
        List<PostImage> postImages = postImageRepository.findByPostId(post.getId());

        //then
        assertThat(postImageList.containsAll(postImages)).isTrue();
        assertThat(postImages.size()).isEqualTo(10);
    }

    private PostImage createPostImage(String postImgUrl, Post post) {
        return PostImage
                .builder()
                .post(post)
                .imgUrl(postImgUrl)
                .build();
    }

    private Member createMember(String uid, String email, String name, String nickname, String imgUrl, String introduce) {
        return Member.builder()
                .uid(uid)
                .email(email)
                .name(name)
                .nickname(nickname)
                .imgUrl(imgUrl)
                .introduce(introduce)
                .build();
    }

    private Post createPost(Member member, String content) {
        return Post.builder()
                .member(member)
                .content(content)
                .build();
    }
}