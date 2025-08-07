package com.example.board.controller;

import com.example.board.domain.Comment;
import com.example.board.domain.Post;
import com.example.board.dto.CommentForm;
import com.example.board.dto.PostDto;
import com.example.board.security.User;
import com.example.board.security.details.CustomUserDetails;
import com.example.board.service.CommentService;
import com.example.board.service.PostService;
import com.example.board.service.PostViewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final CommentService commentService;
    private final PostViewService postViewService;

@GetMapping
public String list(
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
        Pageable pageable
        , @RequestParam(required = false) String type
        , @RequestParam(required = false) String keyword
        ,Model model) {
    Page<Post> postPage;

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    System.out.println("Authentication: " + auth);
    System.out.println("Authenticated? " + (auth != null && auth.isAuthenticated()));

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
    public String detail(@PathVariable Long id , Model model) {
        Post post = postService.findPostWithComments(id);
        postViewService.increaseViewCount(id);

        int cachedCount = postViewService.getCachedCount(id);
        post.setViews(post.getViews()+cachedCount);
        model.addAttribute("post",post);
        model.addAttribute("commentForm", new CommentForm());
        return "detail";
    }

    @GetMapping("/new")
    public String createForm(Model model){
        model.addAttribute("postDto",new PostDto());
        return "new";
    }

    @PostMapping()
    public String create(@Valid @ModelAttribute PostDto postdto,
                         BindingResult result,
                         @AuthenticationPrincipal CustomUserDetails customUserDetails){
        if(result.hasErrors()) {
            return "new";
        }
        postService.create(postdto,customUserDetails.getUser());
        return "redirect:/posts";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal CustomUserDetails customUserDetails){
        postService.delete(id,customUserDetails.getUser());
        return "redirect:/posts";
    }

    @GetMapping("/{id}/edit")
    public String updateForm(@PathVariable Long id,
                             @AuthenticationPrincipal CustomUserDetails customUserDetails ,
                             Model model) {
        Post post = postService.findById(id);
        User user = customUserDetails.getUser();
        if(!postService.isWriterOrAdmin(user,post)){
            return "redirect:/access-denied";
        }
        PostDto postDto = new PostDto();
        postDto.setContent(post.getContent());
        postDto.setTitle(post.getTitle());
        model.addAttribute("postDto",postDto);
        model.addAttribute("postId", id);
        return "edit";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute PostDto postDto,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal CustomUserDetails customUserDetails,
                         Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("postDto", postDto);
            model.addAttribute("postId", id);
            return "edit";
        }
        postService.update(id,postDto,customUserDetails.getUser());
        return "redirect:/posts/{id}";
    }
}
