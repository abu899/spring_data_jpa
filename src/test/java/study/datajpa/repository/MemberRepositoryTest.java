package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

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

    @Test
    void pagingTest() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));
        memberRepository.save(new Member("member6", 10));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> page = memberRepository.findByAge(10, pageRequest);

        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalElements()).isEqualTo(6);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

        Page<MemberDto> toDto = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
    }

    @Test
    void bulkUpdateTest() {
        memberRepository.save(new Member("member1", 15));
        memberRepository.save(new Member("member2", 2));
        memberRepository.save(new Member("member3", 11));
        memberRepository.save(new Member("member4", 13));
        memberRepository.save(new Member("member5", 33));
        memberRepository.save(new Member("member6", 22));

        int num = memberRepository.bulkAgePlus(10); // 영속성 컨텍스트와 상관없이 데이터가 갱신됨(즉 더티체킹안됨)

//        em.flush();
//        em.clear();// 따라서 EntityManager를 초기화해줘야한다 , @Modifying(clearAutomatically = true)

        List<Member> result = memberRepository.findByUsername("member6");
        Member member6 = result.get(0);
        System.out.println("member6 = " + member6);

        assertThat(num).isEqualTo(5);
    }

    @Test
    void findMemberLazy() {
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = Member.createMember("member1", 10, teamA);
        Member member2 = Member.createMember("member2", 20, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findAll();
//        List<Member> members = memberRepository.findMemberFetchJoin();
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam() = " + member.getTeam());
        }
    }

    @Test
    void queryHintTest() {
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setAge(22);

        em.flush();
    }

    @Test
    void queryLockTest() {
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        Member findMember = memberRepository.findLockByUsername("member1");

        em.flush();
    }

    @Test
    void callCustom() {
        List<Member> memberCustom = memberRepository.findMemberCustom();
    }
}