package com.example.board.security;

import com.example.board.exception.EmailAlreadyExistsException;
import com.example.board.security.auth.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("userRegisterDto", new UserRegisterDto());
        return "register";
    }


    @PostMapping("/register")
    public String register(@Valid @ModelAttribute UserRegisterDto userRegisterDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            userService.register(userRegisterDto);
        } catch (EmailAlreadyExistsException e) {
            bindingResult.rejectValue("email", "duplicate", e.getMessage());
            return "register";
        }

        return "redirect:/login";
    }

    @GetMapping("/myProfile")
    public String myProfile(@AuthenticationPrincipal PrincipalDetails principalDetails,
                            Model model) {
        // AuthenticationPrincipal 여기에 user객체 자체를 넣으면 안됨 세션유지 용량이 커짐 식별자 정도만 넣어야함
        // 나중에 고쳐보자 user객체를 받아서 최소한의 정보를 필드로 저장하고 사용할수있게
        Long id = principalDetails.getUserId();
        User user = userService.findById(id);
        MyProfileDto myProfileDto = new MyProfileDto(user.getEmail(), user.getRole());
        model.addAttribute("myProfileDto", myProfileDto);
        return "myProfile";
    }
}
