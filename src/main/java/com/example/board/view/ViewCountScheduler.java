package com.example.board.view;

import com.example.board.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ViewCountScheduler {
    private final RedisTemplate<String, String> redisTemplate;
    private final PostRepository postRepository;

    @Scheduled(fixedRate = 10*1000) //멑티 서버에서 실행될때는 문제가 있을수도 있음
    public void syncView(){
        Set<String> keys = redisTemplate.keys("post:view:*");
        if(keys == null || keys.isEmpty())
            return;

        for (String key : keys) {
            String postIdStr = key.split(":")[2];
            Long postId = Long.parseLong(postIdStr);

            String countStr = redisTemplate.opsForValue().get(key);
            if(countStr == null) continue;

            int count = Integer.parseInt(countStr);
            postRepository.increaseViewCount(postId, count);
            redisTemplate.delete(key);
        }
    }
}
