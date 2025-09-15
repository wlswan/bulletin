package com.example.board.post;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileResponseDto {
    private Long id;
    private String fileName;
    private String fileUrl;
}
