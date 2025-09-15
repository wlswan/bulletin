package com.example.board.post.event;

import org.springframework.context.ApplicationEvent;

import java.util.List;

public class PostDeletedEvent {
    private final List<String> s3Keys;

    public PostDeletedEvent(List<String> s3Keys) {
        this.s3Keys = s3Keys;
    }

    public List<String> getS3Keys() {
        return s3Keys;
    }
}
