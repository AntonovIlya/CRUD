package ru.crud.repository;

import org.springframework.stereotype.Repository;
import ru.crud.exception.NotFoundException;
import ru.crud.model.AdapterPost;
import ru.crud.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepository {

    private final AtomicLong countPosts;

    private final Map<Long, AdapterPost> posts;

    public PostRepository() {
        countPosts = new AtomicLong(0);
        posts = new ConcurrentHashMap<>();
    }

    public List<Post> all() {
        return posts.values().stream()
                .filter(adapterPost -> !adapterPost.isRemoved())
                .map(AdapterPost::getPost)
                .toList();
    }

    public Optional<Post> getById(long id) {
        if (posts.containsKey(id)) {
            Post post = !posts.get(id).isRemoved() ? posts.get(id).getPost() : null;
            return Optional.ofNullable(post);
        }
        return Optional.empty();
    }

    public Post save(Post post) {
        long id = post.getId();
        if (posts.containsKey(id)) {
            if (posts.get(id).isRemoved()) throw new NotFoundException();
            posts.put(id, new AdapterPost(new Post(id, post.getContent())));
            return post;
        } else {
            countPosts.getAndIncrement();
            posts.put(countPosts.get(), new AdapterPost(new Post(countPosts.get(), post.getContent())));
            return posts.get(countPosts.get()).getPost();
        }
    }

    public void removeById(long id) {
        if (posts.containsKey(id)) {
            posts.get(id).setRemoved(true);
        }
    }
}
