package petPeople.pet.domain.post.repository;

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
import petPeople.pet.domain.post.entity.PostBookmark;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PostBookmarkRepositoryTest extends BaseControllerTest {

    @Autowired
    EntityManagerFactory emf;
    @Autowired
    PostRepository postRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PostBookmarkRepository postBookmarkRepository;

    final String uid = "abcd";
    final String email = "issiscv@naver.com";
    final String name = "김상운";
    final String nickname = "balladang";
    final String imgUrl = "www.imgurl.com";
    final String introduce = "잘지내요 우리";

    final String content = "강아지를 사랑합니다.";

    @DisplayName("회원과 게시글의 id 로 북마크 조회")
    @Test
    public void findByMemberIdAndPostIdTest() throws Exception {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Post post = createPost(member, content);
        Member otherMember = createMember(uid + 1, email + 1, name + 1, nickname + 1, imgUrl + 1, introduce + 1);
        Post otherPost = createPost(otherMember, content);

        memberRepository.save(member);
        postRepository.save(post);
        memberRepository.save(otherMember);
        postRepository.save(otherPost);

        PostBookmark savePostBookmark = postBookmarkRepository.save(createPostBookmark(post, member));
        PostBookmark saveOtherPostBookmark = postBookmarkRepository.save(createPostBookmark(otherPost, otherMember));

        //when
        Optional<PostBookmark> optionalPostBookmark = postBookmarkRepository
                .findByMemberIdAndPostId(member.getId(), post.getId());

        //then
        assertThat(optionalPostBookmark.get()).isEqualTo(savePostBookmark);
        assertThat(optionalPostBookmark.get()).isNotEqualTo(saveOtherPostBookmark);//조회되 북마크와 다른 북마크
    }

    @DisplayName("DB 에 없는 회원과 게시글의 id 로 북마크 조회 시 실패 테스트")
    @Test
    public void notFoundByMemberIdAndPostIdTest() throws Exception {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Post post = createPost(member, content);

        memberRepository.save(member);
        postRepository.save(post);

        postBookmarkRepository.save(createPostBookmark(post, member));

        //when
        Optional<PostBookmark> optionalPostBookmark = postBookmarkRepository.findByMemberIdAndPostId(123L, 123L);

        //then
        assertThat(optionalPostBookmark.isEmpty()).isTrue();
    }

    @DisplayName("회원과 게시글의 id 로 북마크 삭제")
    @Test
    public void deleteByMemberIdAndPostIdTest() throws Exception {
        //given
        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Post post = createPost(member, content);
        Member otherMember = createMember(uid + 1, email + 1, name + 1, nickname + 1, imgUrl + 1, introduce + 1);
        Post otherPost = createPost(otherMember, content);

        memberRepository.save(member);
        postRepository.save(post);

        memberRepository.save(otherMember);
        postRepository.save(otherPost);

        postBookmarkRepository.save(createPostBookmark(post, member));
        postBookmarkRepository.save(createPostBookmark(otherPost, otherMember));

        //when
        postBookmarkRepository.deleteByMemberIdAndPostId(member.getId(), post.getId());
        Optional<PostBookmark> optionalPostBookmark = postBookmarkRepository.findByMemberIdAndPostId(member.getId(), post.getId());

        //then
        assertThat(optionalPostBookmark.isEmpty()).isTrue();
    }
    
    @DisplayName("게시글과 fetch join 하여 회원의 북마크 조회")
    @Test
    public void findByMemberIdWithFetchJoinPostTest() throws Exception {

        //given
        int size = 5;
        PageRequest pageRequest = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        Member member = createMember(uid, email, name, nickname, imgUrl, introduce);
        Member otherMember = createMember(uid, email, name, nickname, imgUrl, introduce);
        memberRepository.save(member);
        memberRepository.save(otherMember);

        List<PostBookmark> postBookmarkList = new ArrayList<>();

        //하나의 회원이 10개의 게시글을 북마크하여 저장
        for (int i = 1; i <= 10; i++) {
            Post savePost = postRepository.save(createPost(member, content+i));

            PostBookmark savePostBookmark1 = postBookmarkRepository.save(createPostBookmark(savePost, member));
            PostBookmark savePostBookmark2 = postBookmarkRepository.save(createPostBookmark(savePost, otherMember));
            postBookmarkList.add(savePostBookmark1);
        }

        //when
        Slice<PostBookmark> postBookmarkSlice = postBookmarkRepository.findByMemberIdWithFetchJoinPost(member.getId(), pageRequest);

        //then
        List<PostBookmark> postBookmarkContent = postBookmarkSlice.getContent();

        for (PostBookmark postBookmark : postBookmarkContent) {
            assertThat(emf.getPersistenceUnitUtil().isLoaded(postBookmark.getPost())).isTrue();
        }

        //member 회원의 북마크가 맞는지
        for (int i = 0; i < size; i++) {
            assertThat(postBookmarkList).contains(postBookmarkContent.get(i));
        }

        assertThat(postBookmarkContent.size()).isEqualTo(size);//pageRequest 의 의도대로 size 가 5개가 맞는지
        assertThat(postBookmarkSlice.hasPrevious()).isFalse();
        assertThat(postBookmarkSlice.hasNext()).isTrue();//총 10개를 저장하였기 때문에 있다.
    }


    private PostBookmark createPostBookmark(Post post, Member member) {

        return PostBookmark.builder()
                .post(post)
                .member(member)
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