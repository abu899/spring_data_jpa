package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    void memberTest() {
        Member member = new Member("memberA");
        Member savedMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(savedMember.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    }

    @Test
    void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> members = memberJpaRepository.findAll();
        assertThat(members.size()).isEqualTo(2);

        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        count = memberJpaRepository.count();
        assertThat(count).isEqualTo(0);
    }

    @Test
    void findByUsernameAndAgeTest() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("aaa", 20);

        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> findMembers = memberJpaRepository.findByUsernameAndAgeGreaterThan("aaa", 15);

        assertThat(findMembers.size()).isEqualTo(1);
        assertThat(findMembers.get(0).getUsername()).isEqualTo("aaa");
    }

    @Test
    void findByUsernameNamedQueryTest() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("aaa", 20);

        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> findMembers = memberJpaRepository.findByUsername("aaa");

        assertThat(findMembers.size()).isEqualTo(2);
    }

    @Test
    void pagingTest() {
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 10));
        memberJpaRepository.save(new Member("member3", 10));
        memberJpaRepository.save(new Member("member4", 10));
        memberJpaRepository.save(new Member("member5", 10));
        memberJpaRepository.save(new Member("member6", 10));

        List<Member> members = memberJpaRepository.findByPage(10, 0, 3);
        long totalCount = memberJpaRepository.totalCount(10);

        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(6);
    }

    @Test
    void bulkUpdateTest() {
        memberJpaRepository.save(new Member("member1", 15));
        memberJpaRepository.save(new Member("member2", 2));
        memberJpaRepository.save(new Member("member3", 11));
        memberJpaRepository.save(new Member("member4", 13));
        memberJpaRepository.save(new Member("member5", 33));
        memberJpaRepository.save(new Member("member6", 22));

        int num = memberJpaRepository.bulkAgePlus(10);

        assertThat(num).isEqualTo(5);
    }
}