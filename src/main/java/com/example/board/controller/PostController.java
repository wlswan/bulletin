package com.example.board.controller;

import com.example.board.domain.Post;
import com.example.board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("posts",postService.findAll());
        return "list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id , Model model) {
        postService.findById(id).ifPresent(post -> model.addAttribute("post",post));
        return "detail";
    }

    @GetMapping("/new")
    public String createForm(Model model){
//        model.addAttribute("post",new Post());
        return "new";
    }

    @PostMapping()
    public String create(Post post){
        postService.create(post);
        return "redirect:/posts";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id){
        postService.delete(id);
        return "redirect:/posts";
    }

    @GetMapping("/{id}/edit")
    public String updateForm(@PathVariable Long id, Model model) {
        postService.findById(id)
                .ifPresent(post -> model.addAttribute("post", post));
        return "edit";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Post updatedPost) {
        postService.update(id,updatedPost);
        return "redirect:/posts/{id}";
    }
}
