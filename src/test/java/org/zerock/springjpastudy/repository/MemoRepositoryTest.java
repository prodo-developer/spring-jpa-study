package org.zerock.springjpastudy.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.springjpastudy.entity.Memo;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MemoRepositoryTest {

    @Autowired
    MemoRepository memoRepository;

    @Test
    @DisplayName("객체 가져오기")
    public void testClass() {
        System.out.println(memoRepository.getClass().getName());
    }

    @Test
    @DisplayName("자바8을 이용해 100건 데이터 넣기")
    public void testInsertDummies() {
        IntStream.rangeClosed(1, 100).forEach(i -> {
            Memo memo = Memo.builder()
                    .memoText("Sample..." +i)
                    .build();
            memoRepository.save(memo);
        });
    }

    @Test
    @DisplayName("Select FindByid으로 가져오기")
    public void testFindByid() {
        Long mno = 100L;

        Optional<Memo> result = memoRepository.findById(mno);

        System.out.println("=========================");

        if(result.isPresent()) {
            Memo memo = result.get();
            System.out.println(memo);
        }
    }

    @Test
    @Transactional
    @DisplayName("Select GetOne으로 가져오기")
    public void testGetOne() {
        Long mno = 100L;

        Memo memo = memoRepository.getOne(mno);

        System.out.println("=========================");

        System.out.println(memo);
    }

    @Test
    @DisplayName("수정 작업 테스트")
    public void testUpdate() {
        Memo memo = Memo.builder()
                .mno(100L)
                .memoText("Update Text")
                .build();

        System.out.println(memoRepository.save(memo));
    }

    @Test
    @DisplayName("삭제 작업 테스트")
    public void testDelete() {
        Long mno = 100L;

        memoRepository.deleteById(mno);
    }

    @Test
    @DisplayName("페이징 처리 : 1페이지 10개")
    public void testPageDefault() {
        Pageable pageable = PageRequest.of(0,10);
        Page<Memo> result = memoRepository.findAll(pageable);

        System.out.println(result);

        System.out.println("-----------------------");

        System.out.println("Total Pages : " + result.getTotalPages());      // 총 몇페이지
        System.out.println("Total Count : " + result.getTotalElements());   // 전체 개수
        System.out.println("Page Number : " + result.getNumber());          // 현재 페이지 번호 0부터 시작
        System.out.println("Page Size: " + result.getSize());               // 페이지당 데이터 개수
        System.out.println("has next page?: " + result.hasNext());          // 다음 페이지 존재 여부
        System.out.println("first page : " +result.isFirst());              // 시작페이지(0) 여부

        System.out.println("-----------------------");

        for (Memo memo : result.getContent()) {
            System.out.println(memo);
        }
    }

    @Test
    @DisplayName("정렬 조건 추가하기")
    public void testSort() {
        Sort sort1 = Sort.by("mno").descending();
        Sort sort2 = Sort.by("memoText").ascending();
        Sort sortAll = sort1.and(sort2); // and를 이용한 연결
        Pageable pageable = PageRequest.of(0, 10, sortAll);

        Page<Memo> result = memoRepository.findAll(pageable);

        result.get().forEach(memo -> {
            System.out.println(memo);
        });
    }

    @Test
    @DisplayName("쿼리 메서드 테스트")
    public void testQueryMethods() {
        List<Memo> list = memoRepository.findByMnoBetweenOrderByMnoDesc(70L, 80L);

        for (Memo memo : list) {
            System.out.println(memo);
        }
    }

    @Test
    @DisplayName("쿼리메서드와 Pageable의 결합")
    public void testQueryMethodwithPageble() {

        Pageable pageable = PageRequest.of(0, 10, Sort.by("mno").descending());
        Page<Memo> result =memoRepository.findByMnoBetween(10L, 50L, pageable);
        result.get().forEach(memo -> System.out.println(memo));
    }

    @Test
    @DisplayName("deleteBy로 시작하는 삭제 처리 : 10보다 작은 데이터 삭제")
    @Commit
    @Transactional
    public void testDeleteQueryMethods() {

        /**
         * deleteBy는 실제 개발에 많이 사용되지 않습니다.
         * SQL을 이용하듯이 한번에 삭제가 이루어지는 것이 아니라
         * 아래와 같이 각 엔티티 객체를 하나씩 삭제하기 때문 입니다.
         */

        memoRepository.deleteMemosByMnoLessThan(10L);
    }
}