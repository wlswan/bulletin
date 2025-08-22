package com.example.board.service.like;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final RedisTemplate<String, String> redisTemplate;

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
            return true;
        }
    }
    public boolean hasUserLiked (Long postId, String userId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(getKey(postId), userId));
    }
}
