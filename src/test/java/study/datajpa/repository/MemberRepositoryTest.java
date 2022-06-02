package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;

    @Test
    void memberTest() {
        System.out.println("memberRepository.getClass() = " + memberRepository.getClass());
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).orElse(null);

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    }

    @Test
    void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> members = memberRepository.findAll();
        assertThat(members.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        count = memberRepository.count();
        assertThat(count).isEqualTo(0);
    }

    @Test
    void findByUsernameAndAgeTest() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("aaa", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> findMembers = memberRepository.findByUsernameAndAgeGreaterThan("aaa", 15);

        assertThat(findMembers.size()).isEqualTo(1);
        assertThat(findMembers.get(0).getUsername()).isEqualTo("aaa");
    }

    @Test
    void findByUsernameNamedQueryTest() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("aaa", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> findMembers = memberRepository.findByUsername("aaa");

        assertThat(findMembers.size()).isEqualTo(2);
    }

    @Test
    void findUserTest() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("aaa", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> findMembers = memberRepository.findUser("aaa", 10);

        assertThat(findMembers.size()).isEqualTo(1);
        assertThat(findMembers.get(0).getUsername()).isEqualTo("aaa");
    }

    @Test
    void findUserNameListTest() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();

        assertThat(usernameList.size()).isEqualTo(2);
    }

    @Test
    void findMemberDtoTest() {
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        Member memberA = Member.createMember("aaa", 10, teamA);
        memberRepository.save(memberA);

        List<MemberDto> memberDto = memberRepository.findMemberDto();

        assertThat(memberDto.size()).isEqualTo(1);
        System.out.println("memberDto.get(0) = " + memberDto.get(0));
    }

    @Test
    void findByNamesTest() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> members = memberRepository.findByNames(List.of("aaa", "bbb"));

        assertThat(members.size()).isEqualTo(2);
    }
}