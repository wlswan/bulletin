package com.example.board.like;

import com.example.board.alarm.NotificationService;
import com.example.board.alarm.NotificationType;
import com.example.board.post.Post;
import com.example.board.exception.PostNotFoundException;
import com.example.board.post.PostRepository;
import com.example.board.security.User;
import com.example.board.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final RedisTemplate<String, String> redisTemplate;
    private final NotificationService notificationService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private String getKey(Long postId) {
        return "post:like:" + postId;
    }

    public long getCachedLikeCount(Long postId) {
        String key = getKey(postId);
        return redisTemplate.opsForSet().size(key);
    }

    public boolean toggleLike(Long postId, String userId) {
        String key = getKey(postId);
        Boolean alreadyLike = redisTemplate.opsForSet().isMember(key, userId);

        if (Boolean.TRUE.equals(alreadyLike)) {
            redisTemplate.opsForSet().remove(key, userId);
            return false;
        }
        else {
            redisTemplate.opsForSet().add(key, userId);
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new PostNotFoundException("게시글 없음"));

            User sender = userRepository.findById(Long.valueOf(userId))
                    .orElseThrow(() -> new UsernameNotFoundException("유저 없음"));


            notificationService.sendNotification(
                    post.getUser(),
                    sender,
                    null,
                    NotificationType.LIKE
            );
            return true;
        }
    }
    public boolean hasUserLiked (Long postId, String userId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(getKey(postId), userId));
    }
}
