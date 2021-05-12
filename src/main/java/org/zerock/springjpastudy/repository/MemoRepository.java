package org.zerock.springjpastudy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.springjpastudy.entity.Memo;

import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {

    List<Memo> findByMnoBetweenOrderByMnoDesc(Long from, Long to);

    Page<Memo> findByMnoBetween(Long from, Long to, Pageable pageable);

    void deleteMemosByMnoLessThan(Long num);

    /**
     * @Query 어노테이션 사용이유
     *
     * 필요한 데이터만 선별적으로 추출하는 기능이 있음
     * 데이터베이스에 맞는 순수한 SQL을 사용하는 기능
     * insert, update, delete와 같은 select가 아닌 DML등을 처리하는 기능 (@Modifying과 함께 사용)
     */

    @Transactional
    @Modifying
    @Query("update Memo m set m.memoText = :memoText where m.mno = :mno ")
    int updateMemoText(@Param("mno") Long mno, @Param("memoText") String memoText);

    // @Query와 페이징 처리
    @Query(value = "select m from Memo m where m.mno > :mno",
            countQuery = "select count(m) from Memo m where m.mno > :mno")
    Page<Memo> getListWithQuery(Long mno, Pageable pageable);

    //Object[] 리턴
    @Query(value = "select m.mno, m.memoText, CURRENT_TIME from Memo m where m.mno > :mno",
            countQuery = "select count(m) from Memo m where m.mno > : mno")
    Page<Object[]> getListWithQueryObject(Long mno, Pageable pageable);

    // Native SQL 처리 : JPA 기능을 상실하기 때문에 부득이한 상황에 복잡한 쿼리 경우 가끔 사용
    @Query(value = "select * from memo where mno > 0", nativeQuery = true)
    List<Object[]> getNativeresult();
}
