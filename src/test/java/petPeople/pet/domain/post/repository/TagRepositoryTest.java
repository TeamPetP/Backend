package petPeople.pet.domain.post.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import petPeople.pet.common.BaseControllerTest;
import petPeople.pet.domain.member.entity.Member;
import petPeople.pet.domain.member.repository.MemberRepository;
import petPeople.pet.domain.post.entity.Post;
import petPeople.pet.domain.post.entity.Tag;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class TagRepositoryTest extends BaseControllerTest {

    @Autowired
    TagRepository tagRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PostRepository postRepository;

    final String uid = "abcd";
    final String email = "issiscv@naver.com";
    final String name = "김상운";
    final String nickname = "balladang";
    final String imgUrl = "www.imgurl.com";
    final String introduce = "잘지내요 우리";

    final String content = "강아지 좋아해요";
    final String tagName = "tag";

    @DisplayName("post pk 로 tag 조회")
    @Test
    public void findByPostIdTest() throws Exception {
        //given

        Member saveMember = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));

        Post savePost1 = postRepository.save(createPost(saveMember, "게시글 입니다1."));
        Post savePost2 = postRepository.save(createPost(saveMember, "게시글 입니다2."));
        Post savePost3 = postRepository.save(createPost(saveMember, "게시글 입니다3."));

        List<Tag> tagList1 = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            tagList1.add(tagRepository.save(createTag(savePost1, tagName + i)));
        }

        List<Tag> tagList2 = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            tagList2.add(tagRepository.save(createTag(savePost2, tagName + i)));
        }

        List<Tag> tagList3 = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            tagList3.add(tagRepository.save(createTag(savePost3, tagName + i)));
        }

        //when
        List<Tag> tags1 = tagRepository.findByPostId(savePost1.getId());
        List<Tag> tags2 = tagRepository.findByPostId(savePost2.getId());
        List<Tag> tags3 = tagRepository.findByPostId(savePost3.getId());

        //then
        assertThat(tagList1.containsAll(tags1));
        assertThat(tagList2.containsAll(tags2));
        assertThat(tagList3.containsAll(tags3));
    }

    @DisplayName("여러 post pk 로 tag 조회")
    @Test
    public void findTagsByPostIdsTest() throws Exception {
        //given
        String tagName = "tag";

        Member saveMember = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));

        Post savePost1 = postRepository.save(createPost(saveMember, "게시글 입니다1."));
        Post savePost2 = postRepository.save(createPost(saveMember, "게시글 입니다2."));
        Post savePost3 = postRepository.save(createPost(saveMember, "게시글 입니다3."));

        List<Long> idList1 = new ArrayList<>();
        List<Tag> tagList1 = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Tag tag = tagRepository.save(createTag(savePost1, tagName + i));
            tagList1.add(tag);
            idList1.add(tag.getId());
        }

        List<Long> idList2 = new ArrayList<>();
        List<Tag> tagList2 = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Tag tag = tagRepository.save(createTag(savePost2, tagName + i));
            tagList2.add(tag);
            idList2.add(tag.getId());
        }

        List<Long> idList3 = new ArrayList<>();
        List<Tag> tagList3 = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Tag tag = tagRepository.save(createTag(savePost3, tagName + i));
            tagList3.add(tag);
            idList3.add(tag.getId());
        }

        //when
        List<Tag> tags1 = tagRepository.findTagsByPostIds(idList1);
        List<Tag> tags2 = tagRepository.findTagsByPostIds(idList2);
        List<Tag> tags3 = tagRepository.findTagsByPostIds(idList3);

        //then
        assertThat(tagList1.containsAll(tags1)).isTrue();
        assertThat(tagList2.containsAll(tags2)).isTrue();
        assertThat(tagList3.containsAll(tags3)).isTrue();
    }

    @DisplayName("post pk 로 tag 삭제")
    @Test
    public void deleteByPostIdTest() throws Exception {
        //given

        String tagName = "tag";

        Member saveMember = memberRepository.save(createMember(uid, email, name, nickname, imgUrl, introduce));

        Post savePost1 = postRepository.save(createPost(saveMember, "게시글 입니다1."));
        Post savePost2 = postRepository.save(createPost(saveMember, "게시글 입니다2."));
        Post savePost3 = postRepository.save(createPost(saveMember, "게시글 입니다3."));

        List<Tag> tagList1 = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Tag tag = tagRepository.save(createTag(savePost1, tagName + i));
            tagList1.add(tag);
        }

        List<Tag> tagList2 = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            Tag tag = tagRepository.save(createTag(savePost2, tagName + i));
            tagList2.add(tag);
        }

        List<Tag> tagList3 = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Tag tag = tagRepository.save(createTag(savePost3, tagName + i));
            tagList3.add(tag);
        }

        //when
        Long count1 = tagRepository.deleteByPostId(savePost1.getId());
        Long count2 = tagRepository.deleteByPostId(savePost2.getId());
        Long count3 = tagRepository.deleteByPostId(savePost3.getId());

        List<Tag> tags1 = tagRepository.findByPostId(savePost1.getId());
        List<Tag> tags2 = tagRepository.findByPostId(savePost2.getId());
        List<Tag> tags3 = tagRepository.findByPostId(savePost3.getId());

        //then
        assertThat(tags1.size()).isEqualTo(0);
        assertThat(tags2.size()).isEqualTo(0);
        assertThat(tags3.size()).isEqualTo(0);

        assertThat(count1).isEqualTo(3);
        assertThat(count2).isEqualTo(4);
        assertThat(count3).isEqualTo(5);
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