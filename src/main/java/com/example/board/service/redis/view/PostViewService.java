package com.example.board.service.redis.view;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostViewService {
    private final RedisTemplate<String, String> redisTemplate;

    public void increaseViewCount(Long postId){
        String key = "post:view:" + postId;
        redisTemplate.opsForValue().increment(key);
    }

    public Integer getCachedCount(Long postId) {
        String key = "post:view:" + postId;
        String count = redisTemplate.opsForValue().get(key);
        return (count != null) ? Integer.parseInt(count) : 0;
    }
}
