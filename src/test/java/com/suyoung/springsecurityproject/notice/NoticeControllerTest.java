package com.suyoung.springsecurityproject.notice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class NoticeControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private NoticeRepository noticeRepository;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(@Autowired WebApplicationContext applicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
    }

    @Test
    void getNotice_인증없음() throws Exception {
        mockMvc.perform(get("/notice"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    void getNotice_인증있음() throws Exception {
        mockMvc.perform(get("/notice"))
                .andExpect(status().isOk());
    }

    @Test
    void postNotice_인증없음() throws Exception {
        String content = objectMapper.writeValueAsString(new Notice("제목", "내용"));
        mockMvc.perform(
                post("/notice").content(content)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "admin", password = "admin")
    void postNotice_유저인증있음() throws Exception {
        String content = objectMapper.writeValueAsString(new Notice("제목", "내용"));
        mockMvc.perform(
                post("/notice").with(csrf()).content(content)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin", password = "admin")
    void postNotice_어드민인증있음() throws Exception {
        String content = objectMapper.writeValueAsString(new Notice("제목", "내용"));
        mockMvc.perform(
                post("/notice").with(csrf()).content(content)
        ).andExpect(redirectedUrl("notice")).andExpect(status().is3xxRedirection());
    }

    @Test
    void deleteNotice_인증없음() throws Exception {
        Notice notice = noticeRepository.save(new Notice("제목", "내용"));
        mockMvc.perform(
                delete("/notice/" + notice.getId())
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = {"USER"}, username = "admin", password = "admin")
    void deleteNotice_유저인증있음() throws Exception {
        Notice notice = noticeRepository.save(new Notice("제목", "내용"));
        mockMvc.perform(
                delete("/notice/" + notice.getId()).with(csrf())
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin", password = "admin")
    void deleteNotice_어드민인증있음() throws Exception {
        Notice notice = noticeRepository.save(new Notice("제목", "내용"));
        mockMvc.perform(
                delete("/notice/" + notice.getId()).with(csrf())
        ).andExpect(status().is4xxClientError());
    }
}