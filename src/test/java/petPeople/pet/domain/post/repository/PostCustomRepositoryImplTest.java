package petPeople.pet.domain.post.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.member.repository.MemberRepository;
import petPeople.pet.domain.post.entity.Post;
import petPeople.pet.domain.post.entity.Tag;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("local")
@Transactional
@SpringBootTest
class PostCustomRepositoryImplTest {

    final String uid = "abcd";
    final String email = "issiscv@naver.com";
    final String name = "김상운";
    final String nickname = "balladang";
    final String imgUrl = "www.imgurl.com";
    final String introduce = "잘지내요 우리";

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private MemberRepository memberRepository;

    private Member member;
    
    private Post post1;
    private Post post2;
    private Post post3;

    @BeforeEach
    void beforeEach() {
        member = createMember(uid, email, name, nickname, imgUrl, introduce);

        memberRepository.save(member);
        post1 = postRepository.save(createPost(member, "content1"));
        post2 = postRepository.save(createPost(member, "content2"));
        post3 = postRepository.save(createPost(member, "content3"));

    }

    @Test
    public void retrieveAllPostByTag() throws Exception {
        //given
        PageRequest pageRequest = PageRequest.of(0, 10);

        tagRepository.save(createTag(post1, "A"));
        tagRepository.save(createTag(post2, "A"));
        tagRepository.save(createTag(post3, "B"));

        List<Post> result = Arrays.asList(post2, post1);

        //when
        List<Post> postList = postRepository.findPostSlicingByTag(pageRequest, "A").getContent();

        //then
        assertThat(result).isEqualTo(postList);

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

    private Tag createTag(Post post, String tag) {
        return Tag.builder()
                .post(post)
                .tag(tag)
                .build();
    }

    private Post createPost(Member member, String content) {
        return Post.builder()
                .member(member)
                .content(content)
                .build();
    }

}