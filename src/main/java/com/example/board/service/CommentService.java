package com.example.board.service;

import com.example.board.domain.Comment;
import com.example.board.domain.Post;
import com.example.board.dto.CommentForm;
import com.example.board.exception.PostNotFoundException;
import com.example.board.repository.CommentRepository;
import com.example.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public void saveComment(CommentForm commentForm) {
        Post post = postRepository.findById(commentForm.getPostId()).orElseThrow(() -> new PostNotFoundException("게시글이 존재하지 않습니다."));
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setContent(commentForm.getContent());

        commentRepository.save(comment);

    }

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }
}
