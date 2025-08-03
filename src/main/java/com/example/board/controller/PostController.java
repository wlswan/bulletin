package com.example.board.controller;

import com.example.board.domain.Comment;
import com.example.board.domain.Post;
import com.example.board.dto.CommentForm;
import com.example.board.service.CommentService;
import com.example.board.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final CommentService commentService;

//    @GetMapping
//    public String list(Model model) {
//        model.addAttribute("posts",postService.findAll());
//        return "list";
//    }
@GetMapping
public String list(
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
        Pageable pageable
        , @RequestParam(required = false) String type
        , @RequestParam(required = false) String keyword
        ,Model model) {
    Page<Post> postPage;

    if(type != null && keyword !=null && !keyword.isEmpty()) {
        postPage = postService.search(type,keyword,pageable);
    }
    else {
        postPage = postService.findAll(pageable);
    }

    int nowPage = postPage.getNumber() + 1; //현재 페이지
    int totalPages = postPage.getTotalPages();
    int pageSize = 9; // 밑에 보이는 최대 페이지 수

    int startPage = ((nowPage - 1) / pageSize) * pageSize + 1; // 내가 속해있는 페이지 9개 그룹 중 제일 처음
    int endPage = Math.min(startPage + pageSize - 1, totalPages); //마지막 페이지

    model.addAttribute("page", postPage);
    model.addAttribute("nowPage", nowPage);
    model.addAttribute("startPage", startPage);
    model.addAttribute("endPage", endPage);
    model.addAttribute("type", type);
    model.addAttribute("keyword", keyword);

    return "list";
}



    @GetMapping("/{id}")
    //쿼리 3번 나감 post 조회 , 조회수 업데이트  , comment 조회 나중에 바꿔보자
    public String detail(@PathVariable Long id , Model model) {
        Post post = postService.findByIdAndIncreaseViews(id);
        List<Comment> comments = commentService.getCommentsByPostId(id);
        model.addAttribute("post",post);
        model.addAttribute("comments", comments);
        model.addAttribute("commentForm", new CommentForm());
        return "detail";
    }

    @GetMapping("/new")
    public String createForm(Model model){
        model.addAttribute("post",new Post());
        return "new";
    }

    @PostMapping()
    public String create(@Valid @ModelAttribute Post post, BindingResult result){
        if(result.hasErrors()) {
            return "new";
        }
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
        Post post = postService.findById(id);
        model.addAttribute("post",post);
        return "edit";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Post updatedPost) {
        postService.update(id,updatedPost);
        return "redirect:/posts/{id}";
    }
}
