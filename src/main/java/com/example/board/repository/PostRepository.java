package com.example.board.repository;

import com.example.board.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    Page<Post> findByTitleContaining(String keyword, Pageable pageable);
    Page<Post> findByContentContaining(String keyword, Pageable pageable);
    Page<Post> findByUser_EmailContaining(String keyword, Pageable pageable);
}
