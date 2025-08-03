package com.example.board.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8)
    //특수문자 넣는것도 만들수있나
    private String password;



}
