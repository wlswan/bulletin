package com.example.board.service;

import com.example.board.domain.Post;
import com.example.board.dto.PostDto;
import com.example.board.exception.PostNotFoundException;
import com.example.board.repository.PostRepository;
import com.example.board.security.User;
import com.example.board.security.auth.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Page<Post> findAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }
    public Post findById(Long id) {
        return postRepository.findById(id).orElseThrow(()->new PostNotFoundException("해당 게시글을 찾을 수 없습니다."));
    }

    public Post findPostWithComments(Long id) {
        return postRepository.findPostWithComments(id).orElseThrow(() -> new PostNotFoundException("해당 게시글을 찾을 수 없습니다."));
    }

    public Post create(@Valid PostDto postdto, User user) {
        Post post = new Post();
        post.setUser(user);
        post.setTitle(postdto.getTitle());
        post.setContent(postdto.getContent());
        return postRepository.save(post);
    }

    public void delete(Long id, User user) {
        Post post = findById(id);

        if(isWriterOrAdmin(user,post)){
            postRepository.deleteById(id);
        }
        else {
            throw new AccessDeniedException("게시글을 삭제할 권한이 없습니다.");
        }
    }


    @Transactional
    public void update(Long id, PostDto postDto, User user) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("게시물이 존재하지 않습니다."));

        if(!isWriterOrAdmin(user,post)) {
            throw new AccessDeniedException("게시글에 수정할 권한이 없습니다.");
        }

        post.setContent(postDto.getContent());
        post.setTitle(postDto.getTitle());

    }


    public Page<Post> search(String type, String keyword, Pageable pageable) {
        switch(type) {
            case "title":
                return postRepository.findByTitleContaining(keyword, pageable);
            case "content":
                return postRepository.findByContentContaining(keyword, pageable);
            case "author":
                return postRepository.findByUser_EmailContaining(keyword, pageable);
            default:
                return postRepository.findAll(pageable);
        }
    }

    @Transactional
    public Post findByIdAndIncreaseViews(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("게시글이 존재하지 않습니다."));
        post.setViews(post.getViews()+1);
        return post;
    }

    public boolean isWriterOrAdmin(User user, Post post) {
        return user.getRole() == Role.ROLE_ADMIN || post.getUser().getId().equals(user.getId());
    }
}
