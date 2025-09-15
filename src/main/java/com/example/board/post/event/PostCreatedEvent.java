package com.example.board.post.event;

import com.example.board.post.Post;

import java.nio.file.Path;
import java.util.List;

public class PostCreatedEvent {
    private final Post post;
    private final List<Path> tempFilePaths;

    public PostCreatedEvent(Post post, List<Path> tempFilePaths) {
        this.post = post;
        this.tempFilePaths = tempFilePaths;
    }

    public Post getPost() {
        return post;
    }

    public List<Path> getTempFilePaths() {
        return tempFilePaths;
    }
}
