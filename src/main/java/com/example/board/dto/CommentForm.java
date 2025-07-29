package com.example.board.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {
    @NotBlank(message = "댓글을 입력해주세요")
    private String content;

    private Long postId;

}
