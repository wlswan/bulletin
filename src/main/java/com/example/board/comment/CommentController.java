package com.example.board.comment;

import com.example.board.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comments")
    public String saveComment(@ModelAttribute CommentFormDto commentFormDto,
                              RedirectAttributes redirectAttributes,
                              @AuthenticationPrincipal PrincipalDetails PrincipalDetails) {
        Long userId = PrincipalDetails.getUserId();
        commentService.saveComment(commentFormDto,userId);
        redirectAttributes.addAttribute("id", commentFormDto.getPostId());
        return  "redirect:/posts/{id}";
    }

    @DeleteMapping("/comments/{id}")
    public String deleteComment(@PathVariable Long id,
                                @AuthenticationPrincipal PrincipalDetails PrincipalDetails,
                                RedirectAttributes redirectAttributes) {
        Comment comment = commentService.findById(id);
        commentService.delete(id, PrincipalDetails.getUserId());
        redirectAttributes.addAttribute("id",comment.getPost().getId());

        return "redirect:/posts/{id}";
    }

}
