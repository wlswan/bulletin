package com.example.board.view;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PostViewService {
    private final RedisTemplate<String, String> redisTemplate;

    public void increaseViewCount(Long postId, String userKey){
        String key = "post:view:" + postId + ":" + userKey;
        Boolean isNew = redisTemplate.opsForValue().setIfAbsent(key, "1", 24, TimeUnit.HOURS);
        if (Boolean.TRUE.equals(isNew)) {
            String totalKey = "post:view:" + postId + ":total";
            redisTemplate.opsForValue().increment(totalKey);
        }
    }

    public Integer getCachedCount(Long postId) {
        String totalKey = "post:view:" + postId + ":total";
        String count = redisTemplate.opsForValue().get(totalKey);
        return (count != null) ? Integer.parseInt(count) : 0;
    }
}
