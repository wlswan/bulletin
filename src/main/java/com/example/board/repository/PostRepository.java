package com.example.board.repository;

import com.example.board.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    Page<Post> findByTitleContaining(String keyword, Pageable pageable);
    Page<Post> findByContentContaining(String keyword, Pageable pageable);
    Page<Post> findByUser_EmailContaining(String keyword, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.views = p.views + :count WHERE p.id = :postId")
    void increaseViewCount(@Param("postId")Long postId, @Param("count") int count);


    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN FETCH p.comments c " +
            "LEFT JOIN FETCH c.user " +
            "WHERE p.id = :id")
        //join fetch는 inner join으로 작동해서 댓글 없으면 안 보일수있음
    Optional<Post> findPostWithComments(@Param("id") Long id);

    //서버 하나일떄는 오버라이트 괜찮지만 분산 서버면 문제됨 동시성 문제
    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.likes = :likesCount WHERE p.ID = :postId")
    void updateLikesCount(@Param("postId") Long postId, @Param("likesCount") Long likesCount);

    @Query("SELECT p FROM Post p WHERE p.likes>= 10 or p.views >=30 ORDER BY p.createdAt DESC")
    Page<Post> findHots(Pageable pageable);
}
