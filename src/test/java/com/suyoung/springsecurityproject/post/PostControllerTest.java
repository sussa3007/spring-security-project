package com.suyoung.springsecurityproject.post;

import com.suyoung.springsecurityproject.user.User;
import com.suyoung.springsecurityproject.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles(profiles = "test")
@Transactional
class PostControllerTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    private MockMvc mockMvc;
    private User user;
    private User admin;

    @BeforeEach
    public void setUp(@Autowired WebApplicationContext applicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
        user = userRepository.save(new User("user123", "user", "ROLE_USER"));
        admin = userRepository.save(new User("admin123", "admin", "ROLE_ADMIN"));
    }

    @Test
    void getPost_인증없음() throws Exception {
        mockMvc.perform(get("/post"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    // WithUserDetails 로 테스트 하는 방법
    @WithUserDetails(
            value = "user123", // userDetailsService를 통해 가져올 수 있는 유저
            userDetailsServiceBeanName = "userDetailsServiceImpl", // UserDetailsService 구현체의 Bean
            setupBefore = TestExecutionEvent.TEST_EXECUTION // 테스트 실행 직전에 유저를 가져온다.
    )
    void getPost_인증있음() throws Exception {
        mockMvc.perform(
                        get("/post")
                ).andExpect(status().isOk())
                .andExpect(view().name("post/index"))
                .andDo(print());
    }

    @Test
    void postPost_인증없음() throws Exception {
        mockMvc.perform(
                post("/post").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "제목")
                        .param("content", "내용")
        ).andExpect(status().is3xxRedirection());
    }

    @Test
    @WithUserDetails(
            value = "admin123",
            userDetailsServiceBeanName = "userDetailsServiceImpl",
            setupBefore = TestExecutionEvent.TEST_EXECUTION
    )
    void postPost_어드민인증있음() throws Exception {
        mockMvc.perform(
                post("/post").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "제목")
                        .param("content", "내용")
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(
            value = "user123",
            userDetailsServiceBeanName = "userDetailsServiceImpl",
            setupBefore = TestExecutionEvent.TEST_EXECUTION
    )
    void postPost_유저인증있음() throws Exception {
        mockMvc.perform(
                post("/post").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "제목")
                        .param("content", "내용")
        ).andExpect(redirectedUrl("post")).andExpect(status().is3xxRedirection());
    }

    @Test
    void deletePost_인증없음() throws Exception {
        Post post = postRepository.save(new Post("제목", "내용", user));
        mockMvc.perform(
                delete("/post?id=" + post.getId()).with(csrf())
        ).andExpect(status().is3xxRedirection());
    }

    @Test
    @WithUserDetails(
            value = "user123",
            userDetailsServiceBeanName = "userDetailsServiceImpl",
            setupBefore = TestExecutionEvent.TEST_EXECUTION
    )
    void deletePost_유저인증있음() throws Exception {
        Post post = postRepository.save(new Post("제목", "내용", user));
        mockMvc.perform(
                delete("/post?id=" + post.getId()).with(csrf())
        ).andExpect(redirectedUrl("post")).andExpect(status().is3xxRedirection());
    }

    @Test
    @WithUserDetails(
            value = "admin123",
            userDetailsServiceBeanName = "userDetailsServiceImpl",
            setupBefore = TestExecutionEvent.TEST_EXECUTION
    )
    void deletePost_어드민인증있음() throws Exception {
        Post post = postRepository.save(new Post("제목", "내용", user));
        mockMvc.perform(
                delete("/post?id=" + post.getId()).with(csrf())
        ).andExpect(status().is4xxClientError());
    }
}