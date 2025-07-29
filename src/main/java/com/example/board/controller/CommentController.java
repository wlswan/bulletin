package com.example.board.controller;

import com.example.board.dto.CommentForm;
import com.example.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comments")
    public String saveComment(@ModelAttribute CommentForm commentForm, RedirectAttributes redirectAttributes) {
        commentService.saveComment(commentForm);
        redirectAttributes.addAttribute("id", commentForm.getPostId());
        return  "redirect:/posts/{id}";
    }


}
