# Spring Data JPA

## 새로운 엔티티를 구별하는 방법

`JpaRepository`는 `SimpleJpaRepository`의 구현이다.
SimpleJpaRepository 의 `save`구현을 살펴보면 다음과 같다.
```java
class SimpleJpaRepository {
    @Override
    public <S extends T> S save(S entity) {

        Assert.notNull(entity, "Entity must not be null.");

        if (entityInformation.isNew(entity)) {
            em.persist(entity);
            return entity;
        } else {
            return em.merge(entity);
        }
    }
}
```

보면 알겠지만 새로운 객체인지 판단하고 새로운 객체인 경우 `persist` 아닌 경우 `merge`를 실행한다.
새로운 객체인지 판단하는 기준은
- 객체의 경우 null 인지
- java primitive type 인 경우 0인지
- `Persistable` 인터페이스를 구현해서 판단 로직 변경이 가능하다.

보통 `Entity`를 만들고 PK 값은 `@GeneratedValue`로 증가하는 Long 값을 쓰지만, 만약 uuid 나 특정 String 을 사용하면 어떻게 될까?
```java
class Item {
    @Id
    private String id;
    
    public Item(String id){
        this.id = id;
    }
}

class Item {
    @Id @GeneratedValue
    private Long id;
}
```

새로운 객체를 save 하고 어떤 흐름을 타는지 디버그로 살펴보면, `GeneratedValue`를 사용한 경우 정상적으로 `persist`가 호출된다.
하지만 id 값을 생성자로 입력해주는 경우 앞서 객체를 판단하는 기준에 어긋나기 때문에 `merge`가 호출되는 걸 볼 수있다
이렇게 merge 가 호출되고 나면 호출의 흐름은 다음과 같다
1. DB 에서 id에 해당하는 값을 찾아서 가져오는 시도 진행
2. DB 에도 데이터가 없는 것을 확인
3. 객체가 없는 것을 판단하고 그제서야 insert 실행 후 persist

이처럼 비효율적으로 진행되는 것을 살펴 볼 수 있다. 하지만 실무에서는 데이터가 너무 많고 테이블을 분할해야하는 경우에
간혹 `GeneratedValue`가 아닌 임의의 id 를 사용해야 하는 경우가 있다. 이런 경우 `Persistable` 인터페이스를 상속받고
새로운 객체인지를 판단하는 로직인 `isNew()`를 구현해줘야 한다. 가장 간단한 방법은 `createdDate`를 사용하는 것이다

```java
public class Item implements Persistable<String> {

    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdDate;

    public Item(String id) {
        this.id = id;
    }

    @Override
    public boolean isNew() {
        return createdDate == null;
    }
}
```