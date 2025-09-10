package com.example.board.service;

import com.example.board.alarm.NotificationService;
import com.example.board.alarm.NotificationType;
import com.example.board.domain.Comment;
import com.example.board.domain.Post;
import com.example.board.dto.CommentFormDto;
import com.example.board.exception.CommentNotFoundException;
import com.example.board.exception.PostNotFoundException;
import com.example.board.repository.CommentRepository;
import com.example.board.repository.PostRepository;
import com.example.board.security.User;
import com.example.board.security.UserRepository;
import com.example.board.security.auth.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;


    public Comment findById(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException("해당 댓글이 존재하지 않습니다."));

    }

    public void saveComment(CommentFormDto commentFormDto, Long userId) {
        Post post = postRepository.findById(commentFormDto.getPostId()).orElseThrow(() -> new PostNotFoundException("게시글이 존재하지 않습니다."));
        User writer = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("해당 유저가 존재하지 않습니다."));
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(writer);
        comment.setContent(commentFormDto.getContent());

        commentRepository.save(comment);

        notificationService.sendNotification(
                post.getUser(),
                writer,
                comment.getContent(),
                NotificationType.COMMENT
        );



    }

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }

    public void delete(Long id, Long userId) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new CommentNotFoundException("해당 댓글이 존재하지 않습니다."));

        if(isWriterOrAdmin(userId,comment)) {
            commentRepository.delete(comment);
        }
        else {
            throw new AccessDeniedException("댓글 삭제할 권한이 없습니다.");
        }


    }

    public boolean isWriterOrAdmin(Long userId, Comment comment) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("해당 유저가 존재하지 않습니다."));
        return user.getRole() == Role.ROLE_ADMIN || comment.getUser().getId().equals(user.getId());
    }

}
