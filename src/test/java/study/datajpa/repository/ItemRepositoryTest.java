package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired ItemRepository itemRepository;

    @Test
    void save() {
        Item item = new Item("A"); // 이렇게 되면 PK에 값이 있다
        itemRepository.save(item); // 새로운 객체인데도 불구하고 em.persist 가 호출이 안된다?
    }

}