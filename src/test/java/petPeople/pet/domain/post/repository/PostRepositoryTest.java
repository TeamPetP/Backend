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
import petPeople.pet.domain.post.entity.Tag;

import javax.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class PostRepositoryTest extends BaseControllerTest {

    @Autowired
    EntityManagerFactory emf;

    @Autowired
    PostRepository postRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TagRepository tagRepository;

    @DisplayName("member 와 fetchJoin 한 post 를 pk 로 조회")
    @Test
    void findByIdWithFetchJoinMemberTest() {

        //given
        String uid = "abcd";
        String email = "issiscv@naver.com";
        String name = "김상운";
        String nickname = "balladang";
        String imgUrl = "www.imgurl.com";
        String introduce = "잘지내요 우리";

        Member saveMember = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));

        Post post1 = createPost(saveMember, "게시글 입니다1.");
        Post post2 = createPost(saveMember, "게시글 입니다2.");
        Post post3 = createPost(saveMember, "게시글 입니다3.");


        Post savePost1 = postRepository.save(post1);
        Post savePost2 = postRepository.save(post2);
        Post savePost3 = postRepository.save(post3);

        //when
        Post findPost1 = postRepository.findByIdWithFetchJoinMember(post1.getId()).get();
        Post findPost2 = postRepository.findByIdWithFetchJoinMember(post2.getId()).get();
        Post findPost3 = postRepository.findByIdWithFetchJoinMember(post3.getId()).get();

        //then
        assertThat(findPost1).isEqualTo(savePost1);
        assertThat(findPost2).isEqualTo(savePost2);
        assertThat(findPost3).isEqualTo(savePost3);
        
        //post 의 member 들이 페치조인 후 영속성 컨텍스트에 있는지 확인
        assertThat(emf.getPersistenceUnitUtil().isLoaded(findPost1.getMember())).isEqualTo(true);
        assertThat(emf.getPersistenceUnitUtil().isLoaded(findPost2.getMember())).isEqualTo(true);
        assertThat(emf.getPersistenceUnitUtil().isLoaded(findPost3.getMember())).isEqualTo(true);
    }
    
    @DisplayName("게시글을 페이징하여 전체 조회")
    @Test
    public void findAllSlicing() throws Exception {

        //given
        int size = 5;
        PageRequest pageRequest = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        String uid = "abcd";
        String email = "issiscv@naver.com";
        String name = "김상운";
        String nickname = "balladang";
        String imgUrl = "www.imgurl.com";
        String introduce = "잘지내요 우리";

        Member saveMember = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        List<Post> postList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Post post = createPost(saveMember, "게시글 입니다 " + i);
            postList.add(postRepository.save(post));
        }

        //when
        Slice<Post> postSlice = postRepository.findAllSlicing(pageRequest);

        //then
        assertThat(postSlice.getSize()).isEqualTo(size);
        assertThat(postList.containsAll(postSlice.getContent())).isTrue();
        assertThat(postSlice.hasNext()).isTrue();
        assertThat(postSlice.hasPrevious()).isFalse();
    }

    @DisplayName("태그로 게시글 검색")
    @Test
    public void findPostSlicingByTagTest() throws Exception {
        //given
        int size = 5;
        PageRequest pageRequest = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        String uid = "abcd";
        String email = "issiscv@naver.com";
        String name = "김상운";
        String nickname = "balladang";
        String imgUrl = "www.imgurl.com";
        String introduce = "잘지내요 우리";

        Member saveMember = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));

        List<Post> postList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Post post = createPost(saveMember, "게시글 입니다 " + i);
            String tagName = "강아지";

            tagRepository.save(createTag(post, tagName + 1 + " " + i));
            tagRepository.save(createTag(post, tagName + 2 + " " + i));
            tagRepository.save(createTag(post, tagName + 3 + " " + i));

            Post savePost = postRepository.save(post);
            postList.add(savePost);
        }

        //when
        Slice<Post> postSliceByTag = postRepository.findPostSlicingByTag(pageRequest, "강아지1 1");
        Slice<Post> postSliceByAllTag = postRepository.findPostSlicingByTag(pageRequest, "강아지");

        //then
        List<Post> tagContent = postSliceByTag.getContent();
        List<Post> allTagContent = postSliceByAllTag.getContent();

        assertThat(tagContent.size()).isEqualTo(2);
        assertThat(allTagContent.size()).isEqualTo(5);
    }
        
    @DisplayName("회원이 작성한 게시글 페이징 조회")
    @Test
    public void findAllByMemberIdSlicingTest() throws Exception {
        //given
        int size = 5;
        PageRequest pageRequest = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        String uid = "abcd";
        String email = "issiscv@naver.com";
        String name = "김상운";
        String nickname = "balladang";
        String imgUrl = "www.imgurl.com";
        String introduce = "잘지내요 우리";

        Member saveMember = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Member otherSaveMember = memberRepository.save(createMember(uid+1, email+1, name+1, nickname+1, imgUrl+1, introduce+1));

        List<Post> memberPostList = new ArrayList<>();
        List<Post> otherMemberPostList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Post savePost = postRepository.save(createPost(saveMember, "게시글 입니다 " + i));
            memberPostList.add(savePost);
        }

        for (int i = 1; i <= 5; i++) {
            //회원이 otherSaveMember 로 다르다.
            Post savePost = postRepository.save(createPost(otherSaveMember, "게시글 입니다 " + i));
            otherMemberPostList.add(savePost);
        }

        //when
        Slice<Post> postSlice = postRepository.findAllByMemberIdSlicing(saveMember.getId(), pageRequest);

        //then
        List<Post> content = postSlice.getContent();
        
        assertThat(content.size()).isEqualTo(5);//saveMember 가 작성한 글이 5개가 맞는지
        assertThat(content.containsAll(memberPostList)).isTrue();//saveMember 가 작성한 게시글 5개가 정확히 맞는지
        assertThat(content).contains(memberPostList.get(0), memberPostList.get(1), memberPostList.get(2), memberPostList.get(3), memberPostList.get(4));
        assertThat(content).doesNotContain(otherMemberPostList.get(0), otherMemberPostList.get(1), otherMemberPostList.get(2), otherMemberPostList.get(3), otherMemberPostList.get(4));
    }

    @DisplayName("회원이 작성한 게시글 개수 조회")
    @Test
    public void countByMemberIdTest() throws Exception {
        //given
        int size = 5;

        String uid = "abcd";
        String email = "issiscv@naver.com";
        String name = "김상운";
        String nickname = "balladang";
        String imgUrl = "www.imgurl.com";
        String introduce = "잘지내요 우리";

        Member saveMember = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));
        Member otherSaveMember = memberRepository.save(createMember(uid+1, email+1, name+1, nickname+1, imgUrl+1, introduce+1));

        List<Post> memberPostList = new ArrayList<>();
        List<Post> otherMemberPostList = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            Post savePost = postRepository.save(createPost(saveMember, "게시글 입니다 " + i));
            memberPostList.add(savePost);
        }

        for (int i = 1; i <= 3; i++) {
            //회원이 otherSaveMember 로 다르다.
            Post savePost = postRepository.save(createPost(otherSaveMember, "게시글 입니다 " + i));
            otherMemberPostList.add(savePost);
        }

        //when
        Long count = postRepository.countByMemberId(saveMember.getId());

        //then
        assertThat(count).isEqualTo(size);

    }

    private Tag createTag(Post post, String tagName) {
        return Tag.builder()
                .post(post)
                .tag(tagName)
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