package com.example.board.like;

import com.example.board.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class LikeCountScheduler {
    private final RedisTemplate<String, String> redisTemplate;
    private final PostRepository postRepository;

    @Scheduled(fixedRate = 10*1000)
    public void syncLikes(){
        Set<String> keys = redisTemplate.keys("post:like:*");
        if (keys == null || keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            String postIdStr = key.split(":")[2];
            Long postId = Long.parseLong(postIdStr);
            Long likesCount = redisTemplate.opsForSet().size(key);
            if(likesCount ==null) continue;

            postRepository.updateLikesCount(postId, likesCount);

        }
    }
}
