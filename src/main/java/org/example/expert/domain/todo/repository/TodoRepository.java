package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u " + // level 2-7 FETCH 이미 전에 추가했었습니다!
            "WHERE (:weather IS NULL OR t.weather = :weather)" +
            "AND (:startDate IS NULL OR t.modifiedAt >= :startDate ) " +
            "AND (:endDate IS NULL OR t.modifiedAt <= :endDate)" +
            "ORDER BY t.modifiedAt DESC")
    Page<Todo> findTodosByWeatherAndModifiedDate(@Param("weather") String weather,
                                                 @Param("startDate") Date startDate,
                                                 @Param("endDate") Date endDate,
                                                 Pageable pageable);
//신민아 튜터님께서 메소드명 간결하게 쓰는거 추천해주심! ex) getTodoByCondition / native query > jpql > query test
    @Query("SELECT t FROM Todo t " +
            "LEFT JOIN t.user " +
            "WHERE t.id = :todoId")
    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);
}



