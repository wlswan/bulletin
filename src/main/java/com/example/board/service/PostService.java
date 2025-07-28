package com.example.board.service;

import com.example.board.domain.Post;
import com.example.board.exception.PostNotFoundException;
import com.example.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;


    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Post findById(Long id) {
        return postRepository.findById(id).orElseThrow(()->new PostNotFoundException("해당 게시글을 찾을 수 없습니다."));
    }

    public Post create(Post post) {
        return postRepository.save(post);
    }

    public void delete(Long id) {
        postRepository.deleteById(id);
    }


    @Transactional
    public void update(Long id, Post updatedPost) {
        postRepository.findById(id).ifPresent(post -> {
            post.setTitle(updatedPost.getTitle());
            post.setContent(updatedPost.getContent());
            post.setAuthor(updatedPost.getAuthor());
        });

    }


}
