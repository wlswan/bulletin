package com.example.board.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다. ")
    private String content;

    @NotBlank(message = "작성자는 필수입니다.")
    private String author;

    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL,orphanRemoval = true)
    List<Comment> comments = new ArrayList<>();

    private LocalDateTime createdAt;

    @Column(nullable = false) // 데이터베이스 계층 DB에 업데이트할 때 유효성 검증
    @NotNull //애플리케이션 계층 dto에서 유효성 검증
    private int views = 0;

    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
    }

    //대안으로 @CreatedDate가 있음


}
