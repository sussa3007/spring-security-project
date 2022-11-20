package com.suyoung.springsecurityproject.config;

import com.suyoung.springsecurityproject.notice.NoticeService;
import com.suyoung.springsecurityproject.post.PostService;
import com.suyoung.springsecurityproject.user.User;
import com.suyoung.springsecurityproject.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;

@Configuration
@Profile(value = "!test")
public class InitializeConfig {
    private final UserService userService;
    private final PostService postService;
    private final NoticeService noticeService;

    public InitializeConfig(
            @Autowired UserService userService,
            @Autowired PostService postService,
            @Autowired NoticeService noticeService) {
        this.userService = userService;
        this.postService = postService;
        this.noticeService = noticeService;
    }

    @PostConstruct
    public void adminAccount() {
        User user = userService.signup("user", "user");
        userService.signupAdmin("admin", "admin");
        postService.savePost(user, "테스트", "테스트입니다.");
        postService.savePost(user, "테스트2", "테스트2입니다.");
        postService.savePost(user, "테스트3", "테스트3입니다.");
        postService.savePost(user, "여름 여행계획", "여름 여행계획 작성중...");
        noticeService.saveNotice("환영합니다.", "환영합니다 여러분");
        noticeService.saveNotice("게시글 작성 방법 공지", "1. 회원가입\n2. 로그인\n3. 게시글 작성\n4. 저장\n* 본인 외에는 게시글을 볼 수 없습니다.");
    }
}
