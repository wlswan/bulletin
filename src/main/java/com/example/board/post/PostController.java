package com.example.board.post;

import com.example.board.comment.CommentFormDto;
import com.example.board.security.auth.PrincipalDetails;
import com.example.board.like.PostLikeService;
import com.example.board.view.PostViewService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostViewService postViewService;
    private final PostLikeService postLikeService;

@GetMapping
public String list(
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
        Pageable pageable
        , @RequestParam(required = false) String type
        , @RequestParam(required = false) String keyword
        , @RequestParam(defaultValue = "all") String mode
        , @AuthenticationPrincipal PrincipalDetails principalDetails
        ,Model model) {
    Page<Post> postPage;

    if(type != null && keyword !=null && !keyword.isEmpty()) {
        postPage = postService.search(type,keyword,pageable);
    }
    else {
        if("hot".equals(mode)) {
            postPage = postService.findHots(pageable);
        }else {
            postPage = postService.findAll(pageable);
        }
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
    model.addAttribute("mode", mode);

    return "list";
}



    @GetMapping("/{id}")
    public String detail(@PathVariable Long id ,
                         @AuthenticationPrincipal PrincipalDetails principalDetails,
                         HttpServletRequest request,
                         HttpServletResponse response,
                         Model model) {
        Post post = postService.findPostWithCommentsAndFiles(id);

        String userKey;
        if (principalDetails != null) {
            userKey = "user:" + principalDetails.getUserId();
        } else {
            userKey = "guest:" + getOrCreateGuestKey(request, response);
        }

        postViewService.increaseViewCount(id,userKey);
        boolean hasLiked = false;
        long likeCount = postLikeService.getCachedLikeCount(id);
        hasLiked = postLikeService.hasUserLiked(id, principalDetails.getUserId().toString());

        int cachedCount = postViewService.getCachedCount(id);
        post.setViews(post.getViews()+cachedCount);


        model.addAttribute("post",post);
        model.addAttribute("commentForm", new CommentFormDto());
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("hasLiked", hasLiked);
        return "detail";
    }

    private String getOrCreateGuestKey(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if("guestKey".equals(c.getName())){
                    return c.getValue();
                }
            }
        }
        String guestKey = UUID.randomUUID().toString();
        Cookie cookie = new Cookie("guestKey",guestKey);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 2);
        response.addCookie(cookie);
        return guestKey;
    }

    @GetMapping("/new")
    public String createForm(Model model){
        model.addAttribute("postDto",new PostDto());
        return "new";
    }

    @PostMapping()
    public String create(@Valid @ModelAttribute PostDto postdto,
                         BindingResult result,
                         @AuthenticationPrincipal PrincipalDetails PrincipalDetails){
        if(result.hasErrors()) {
            return "new";
        }
        postService.create(postdto,PrincipalDetails.getUserId());
        return "redirect:/posts";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal PrincipalDetails PrincipalDetails){
        postService.delete(id,PrincipalDetails.getUserId());
        return "redirect:/posts";
    }

    @GetMapping("/{id}/edit")
    public String updateForm(@PathVariable Long id,
                             @AuthenticationPrincipal PrincipalDetails PrincipalDetails ,
                             Model model) {
        Post post = postService.findById(id);
        Long userId = PrincipalDetails.getUserId();
        if(!postService.isWriterOrAdmin(userId,post)){
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
                         @AuthenticationPrincipal PrincipalDetails PrincipalDetails,
                         Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("postDto", postDto);
            model.addAttribute("postId", id);
            return "edit";
        }
        postService.update(id,postDto,PrincipalDetails.getUserId());
        return "redirect:/posts/{id}";
    }

    @PostMapping("/{id}/like")
    public String toggleLike(@PathVariable Long id,
                             @AuthenticationPrincipal PrincipalDetails principalDetails) {

        postLikeService.toggleLike(id, principalDetails.getUserId().toString());
        return "redirect:/posts/" + id;
    }

}
