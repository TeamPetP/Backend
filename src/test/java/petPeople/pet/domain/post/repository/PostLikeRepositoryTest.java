package petPeople.pet.domain.post.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import petPeople.pet.common.BaseControllerTest;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.member.repository.MemberRepository;
import petPeople.pet.domain.post.entity.Post;
import petPeople.pet.domain.post.entity.PostLike;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class PostLikeRepositoryTest extends BaseControllerTest {

    @Autowired
    PostLikeRepository postLikeRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    MemberRepository memberRepository;

    @DisplayName("여러 post PK 를 통해 postLike 조회")
    @Test
    public void findPostLikesByPostIds() throws Exception {
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

        memberRepository.save(member);
        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        List<Long> idList = new ArrayList<>();
        idList.add(post1.getId());
        idList.add(post3.getId());

        for (int i = 1; i <= 10; i++) {
            PostLike postLike = createPostLike(member, post1);
            postLikeRepository.save(postLike);
        }

        for (int i = 1; i <= 5; i++) {
            PostLike postLike = createPostLike(member, post2);
            postLikeRepository.save(postLike);
        }

        for (int i = 1; i <= 3; i++) {
            PostLike postLike = createPostLike(member, post3);
            postLikeRepository.save(postLike);
        }


        //when
        List<PostLike> postLikeList = postLikeRepository.findPostLikesByPostIds(idList);

        //then
        assertThat(postLikeList.size()).isEqualTo(13);
    }

    @DisplayName("post 와 member 를 fk 로 갖고 있는 postLike 조회")
    @Test
    public void findPostLikeByPostIdAndMemberIdTest() throws Exception {
        //given
        String uid = "abcd";
        String email = "issiscv@naver.com";
        String name = "김상운";
        String nickname = "balladang";
        String imgUrl = "www.imgurl.com";
        String introduce = "잘지내요 우리";

        String content = "강아지 좋아해요";

        Member member1 = createMember(uid, email, name, nickname, imgUrl, introduce);
        Member member2 = createMember(uid, email, name, nickname, imgUrl, introduce);
        Member member3 = createMember(uid, email, name, nickname, imgUrl, introduce);

        Post post1 = createPost(member1, content);
        Post post2 = createPost(member2, content);
        Post post3 = createPost(member3, content);

        PostLike postLike1 = createPostLike(member1, post1);
        PostLike postLike2 = createPostLike(member2, post2);
        PostLike postLike3 = createPostLike(member3, post3);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);


        postLikeRepository.save(postLike1);
        postLikeRepository.save(postLike2);
        postLikeRepository.save(postLike3);

        //when
        Optional<PostLike> postLikeOptional1 = postLikeRepository
                .findPostLikeByPostIdAndMemberId(post1.getId(), member1.getId());
        Optional<PostLike> postLikeOptional2 = postLikeRepository
                .findPostLikeByPostIdAndMemberId(post2.getId(), member2.getId());
        Optional<PostLike> postLikeOptional3 = postLikeRepository
                .findPostLikeByPostIdAndMemberId(post3.getId(), member3.getId());

        //then
        assertThat(postLikeOptional1.get()).isEqualTo(postLike1);
        assertThat(postLikeOptional2.get()).isEqualTo(postLike2);
        assertThat(postLikeOptional3.get()).isEqualTo(postLike3);
    }

    @DisplayName("post 를 fk 로 갖고 있는 postLike 삭제")
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

        Member member1 = createMember(uid, email, name, nickname, imgUrl, introduce);
        Member member2 = createMember(uid, email, name, nickname, imgUrl, introduce);
        Member member3 = createMember(uid, email, name, nickname, imgUrl, introduce);

        Post post1 = createPost(member1, content);
        Post post2 = createPost(member2, content);
        Post post3 = createPost(member3, content);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);


        postLikeRepository.save(createPostLike(member1, post1));
        postLikeRepository.save(createPostLike(member1, post2));
        postLikeRepository.save(createPostLike(member1, post3));

        postLikeRepository.save(createPostLike(member2, post1));
        postLikeRepository.save(createPostLike(member2, post2));
        postLikeRepository.save(createPostLike(member2, post3));

        postLikeRepository.save(createPostLike(member3, post1));
        postLikeRepository.save(createPostLike(member3, post2));
        postLikeRepository.save(createPostLike(member3, post3));

        //when
        Long a = postLikeRepository.deleteByPostId(post1.getId());
        Long b = postLikeRepository.deleteByPostId(post2.getId());
        Long c = postLikeRepository.deleteByPostId(post3.getId());

        //then
        assertThat(a).isEqualTo(3L);
        assertThat(b).isEqualTo(3L);
        assertThat(c).isEqualTo(3L);
    }

    @DisplayName("post 와 member 를 fk 로 갖고 있는 postLike 삭제")
    @Test
    public void deleteByPostIdAndMemberIdTest() throws Exception {
        //given
        String uid = "abcd";
        String email = "issiscv@naver.com";
        String name = "김상운";
        String nickname = "balladang";
        String imgUrl = "www.imgurl.com";
        String introduce = "잘지내요 우리";

        String content = "강아지 좋아해요";

        Member member1 = createMember(uid, email, name, nickname, imgUrl, introduce);
        Member member2 = createMember(uid, email, name, nickname, imgUrl, introduce);
        Member member3 = createMember(uid, email, name, nickname, imgUrl, introduce);

        Post post1 = createPost(member1, content);
        Post post2 = createPost(member2, content);
        Post post3 = createPost(member3, content);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);


        postLikeRepository.save(createPostLike(member1, post1));
        postLikeRepository.save(createPostLike(member1, post2));
        postLikeRepository.save(createPostLike(member1, post3));

        //when
        postLikeRepository.deleteByPostIdAndMemberId(post1.getId(), member1.getId());
        postLikeRepository.deleteByPostIdAndMemberId(post1.getId(), member2.getId());
        postLikeRepository.deleteByPostIdAndMemberId(post1.getId(), member3.getId());

        Optional<PostLike> postLikeOptional1 = postLikeRepository.findPostLikeByPostIdAndMemberId(post1.getId(), member1.getId());
        Optional<PostLike> postLikeOptional2 = postLikeRepository.findPostLikeByPostIdAndMemberId(post1.getId(), member2.getId());
        Optional<PostLike> postLikeOptional3 = postLikeRepository.findPostLikeByPostIdAndMemberId(post1.getId(), member3.getId());

        //then
        assertThat(postLikeOptional1.isEmpty()).isTrue();
        assertThat(postLikeOptional2.isEmpty()).isTrue();
        assertThat(postLikeOptional3.isEmpty()).isTrue();
    }

    @DisplayName("post fk 로 갖고 있는 postLike 개수 조회")
    @Test
    public void countByPostIdTest() throws Exception {
        //given
        String uid = "abcd";
        String email = "issiscv@naver.com";
        String name = "김상운";
        String nickname = "balladang";
        String imgUrl = "www.imgurl.com";
        String introduce = "잘지내요 우리";

        String content = "강아지 좋아해요";

        Member member1 = createMember(uid, email, name, nickname, imgUrl, introduce);
        Member member2 = createMember(uid, email, name, nickname, imgUrl, introduce);
        Member member3 = createMember(uid, email, name, nickname, imgUrl, introduce);

        Post post1 = createPost(member1, content);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        postRepository.save(post1);

        postLikeRepository.save(createPostLike(member1, post1));
        postLikeRepository.save(createPostLike(member2, post1));
        postLikeRepository.save(createPostLike(member3, post1));

        //when
        Long count = postLikeRepository.countByPostId(post1.getId());

        //then
        assertThat(count).isEqualTo(3L);
    }
    
    @Test
    public void findByMemberIdWithFetchJoinPostTest() throws Exception {
        int size = 3;
        PageRequest pageRequest = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        //given
        String uid = "abcd";
        String email = "issiscv@naver.com";
        String name = "김상운";
        String nickname = "balladang";
        String imgUrl = "www.imgurl.com";
        String introduce = "잘지내요 우리";

        String content = "강아지 좋아해요";

        Member member1 = createMember(uid, email, name, nickname, imgUrl, introduce);

        Post post1 = createPost(member1, content);
        Post post2 = createPost(member1, content);
        Post post3 = createPost(member1, content);
        Post post4 = createPost(member1, content);
        Post post5 = createPost(member1, content);
        Post post6 = createPost(member1, content);

        memberRepository.save(member1);

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);
        postRepository.save(post4);
        postRepository.save(post5);
        postRepository.save(post6);

        List<PostLike> postLikeList = new ArrayList<>();


        postLikeList.add(postLikeRepository.save(createPostLike(member1, post1)));
        postLikeList.add(postLikeRepository.save(createPostLike(member1, post2)));
        postLikeList.add(postLikeRepository.save(createPostLike(member1, post3)));
        postLikeList.add(postLikeRepository.save(createPostLike(member1, post4)));
        postLikeList.add(postLikeRepository.save(createPostLike(member1, post5)));
        postLikeList.add(postLikeRepository.save(createPostLike(member1, post6)));

        //when
        Slice<PostLike> postLikeSlice = postLikeRepository.findByMemberIdWithFetchJoinPost(member1.getId(), pageRequest);

        //then
        assertThat(postLikeList.containsAll(postLikeSlice.getContent())).isTrue();
        assertThat(postLikeSlice.getContent().size()).isEqualTo(3);
        assertThat(postLikeSlice.hasNext()).isTrue();
        assertThat(postLikeSlice.hasPrevious()).isFalse();
    }

    private PostLike createPostLike(Member member, Post post) {
        return PostLike.builder()
                .member(member)
                .post(post)
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