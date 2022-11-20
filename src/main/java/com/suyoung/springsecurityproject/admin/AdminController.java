package com.suyoung.springsecurityproject.admin;


import com.suyoung.springsecurityproject.post.Post;
import com.suyoung.springsecurityproject.post.PostService;
import com.suyoung.springsecurityproject.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final PostService postService;

    @GetMapping
    public String getPostForAdmin(Authentication authentication, Model model) {
        User user = (User) authentication.getPrincipal();
        List<Post> posts = postService.findByUser(user);
        model.addAttribute("posts", posts);
        return "admin/index";
    }
}