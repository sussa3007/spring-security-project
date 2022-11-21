package com.suyoung.springsecurityproject.post;

import com.suyoung.springsecurityproject.exception.UserNotFoundException;
import com.suyoung.springsecurityproject.user.User;
import com.suyoung.springsecurityproject.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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
    public List<Post> findByUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
        if (user.isAdmin()) {
            return postRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        }
        return postRepository.findByUserOrderByIdDesc(user);
    }

    public Post savePost(User user, String title, String content) {
        if (user == null) {
            throw new UserNotFoundException();
        }
        return postRepository.save(new Post(title, content, user));
    }

    public void deletePost(User user, Long postId) {
        if (user == null) {
            throw new UserNotFoundException();
        }
        Post post = postRepository.findByIdAndUser(postId, user);
        postRepository.delete(post);
    }

}
