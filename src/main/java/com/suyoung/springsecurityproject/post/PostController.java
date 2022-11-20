package com.suyoung.springsecurityproject.post;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;


    @GetMapping
    public String findByPost(Principal principal, Model model) {
        List<Post> posts = postService.findByUserName(principal.getName());
        model.addAttribute("posts", posts);
        return "post/index";
    }

    @PostMapping
    public String savePost(@ModelAttribute PostDto postDto, Principal principal) {
        postService.savePost(principal.getName(), postDto.getTitle(), postDto.getContent());
        return "redirect:post";
    }

    @DeleteMapping
    public String deletePost(@RequestParam Long id, Principal principal) {
        postService.deletePost(principal.getName(), id);
        return "redirect:post";
    }
}
