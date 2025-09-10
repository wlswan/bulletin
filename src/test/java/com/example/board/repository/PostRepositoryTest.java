package com.example.board.repository;

import com.example.board.post.Post;
import com.example.board.post.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    void savaAndFind() {

        Post post = new Post();
        post.setAuthor("홍길동");
        post.setTitle("자기소개");
        post.setContent("안녕하세요");
        post.setCreatedAt(LocalDateTime.now());

        Post post1 = postRepository.save(post);
        Optional<Post> byId = postRepository.findById(post1.getId());

        assertThat(byId.get().getTitle()).isEqualTo("자기소개");
        assertThat(byId.get().getAuthor()).isEqualTo("홍길동");
    }

}