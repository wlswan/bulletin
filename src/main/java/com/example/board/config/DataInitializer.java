package com.example.board.config;

import com.example.board.domain.Post;
import com.example.board.repository.PostRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

//    @Bean
//    public CommandLineRunner init(PostRepository postRepository) {
//        return args -> {
//            for (int i = 1; i <= 100; i++) {
//                Post post = new Post();
//                post.setTitle("제목 " + i);
//                post.setContent("내용 " + i);
//                post.setAuthor("작성자 " + i);
//                post.setCreatedAt(LocalDateTime.now());
//                postRepository.save(post);
//            }
//        };
//    }
}
