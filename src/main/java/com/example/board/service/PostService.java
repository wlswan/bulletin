package com.example.board.service;

import com.example.board.domain.Post;
import com.example.board.exception.PostNotFoundException;
import com.example.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;


//    public List<Post> findAll() {
//        return postRepository.findAll();
//    }

    public Page<Post> findAll(Pageable pageable) {
        return postRepository.findAll(pageable);
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


    public Page<Post> search(String type, String keyword, Pageable pageable) {
        switch(type) {
            case "title":
                return postRepository.findByTitleContaining(keyword, pageable);
            case "content":
                return postRepository.findByContentContaining(keyword, pageable);
            case "author":
                return postRepository.findByAuthorContaining(keyword, pageable);
            default:
                return postRepository.findAll(pageable);
        }
    }
}
