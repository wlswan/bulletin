package com.example.board.security.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class KakaoLoginController {

    @GetMapping("/login/oauth2/kakao")
    public String getKakaoCode(@RequestParam(required = false) String code,
                             @RequestParam(required = false) String error,
                             @RequestParam(required = false) String error_description,
                             @RequestParam(required = false) String state) {
        System.out.println("code = " + code);
        System.out.println("error = " + error);
        System.out.println("error_description = " + error_description);
        System.out.println("state = " + state);

        return "redirect:/posts";

    }
}
