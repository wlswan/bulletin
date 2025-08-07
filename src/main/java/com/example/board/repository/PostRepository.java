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
            "JOIN FETCH p.comments c " +
            "JOIN FETCH c.user " +
            "WHERE p.id = :id")
    Optional<Post> findPostWithComments(@Param("id") Long id);

}
