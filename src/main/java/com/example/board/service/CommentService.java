package com.example.board.service;

import com.example.board.domain.Comment;
import com.example.board.domain.Post;
import com.example.board.dto.CommentForm;
import com.example.board.exception.CommentNotFoundException;
import com.example.board.exception.PostNotFoundException;
import com.example.board.repository.CommentRepository;
import com.example.board.repository.PostRepository;
import com.example.board.security.User;
import com.example.board.security.details.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public void saveComment(CommentForm commentForm, User user) {
        Post post = postRepository.findById(commentForm.getPostId()).orElseThrow(() -> new PostNotFoundException("게시글이 존재하지 않습니다."));
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(commentForm.getContent());

        commentRepository.save(comment);

    }

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }

    public void delete(Long id, User user) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new CommentNotFoundException("해당 댓글이 존재하지 않습니다."));

        if(isWriterOrAdmin(user,comment)) {
            commentRepository.delete(comment);
        }
        else {
            throw new AccessDeniedException("댓글 삭제할 권한이 없습니다.");
        }


    }

    public boolean isWriterOrAdmin(User user, Comment comment) {
        return user.getRole() == Role.ROLE_ADMIN || comment.getUser().getId().equals(user.getId());
    }

    public Comment findById(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException("해당 댓글이 존재하지 않습니다."));

    }
}
