package com.example.board.comment;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentFormDto {
    @NotBlank(message = "댓글을 입력해주세요")
    private String content;

    private Long postId;

}
