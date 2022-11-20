package com.suyoung.springsecurityproject.post;

import com.suyoung.springsecurityproject.user.User;
import com.suyoung.springsecurityproject.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public List<Post> findByUserName(String userName) {
        User user = userRepository.findByUsername(userName);
        if (user == null) {
            throw new RuntimeException("유저가 없습니다.");
        }
        return postRepository.findByUserAndStatus(user, PostStatus.Y);
    }

    public Post savePost(String userName, String title, String content) {
        User user = userRepository.findByUsername(userName);
        if (user == null) {
            throw new RuntimeException("유저가 없습니다.");
        }
        return postRepository.save(new Post(title, content, user));
    }

    public void deletePost(String userName, Long id) {
        User user = userRepository.findByUsername(userName);
        if (user == null) {
            throw new RuntimeException("유저가 없습니다.");
        }
        Post post = postRepository.findByIdAndUser(id, user);
        postRepository.delete(post);
    }

}
