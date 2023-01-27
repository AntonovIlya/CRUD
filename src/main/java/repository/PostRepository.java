package repository;

import model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class PostRepository {

    private final AtomicLong countPosts;

    private final Map<Long, Post> posts;

    public PostRepository() {
        countPosts = new AtomicLong(0);
        posts = new ConcurrentHashMap<>();
    }

    public List<Post> all() {
        return new ArrayList<>(posts.values());
    }

    public Optional<Post> getById(long id) {
        return Optional.ofNullable(posts.get(id));
    }

    public Post save(Post post) {
        long id = post.getId();
        if (posts.containsKey(id)) {
            posts.put(id, new Post(id, post.getContent()));
        } else {
            countPosts.getAndIncrement();
            posts.put(countPosts.get(), new Post(countPosts.get(), post.getContent()));
        }
        return post;
    }

    public void removeById(long id) {
        posts.remove(id);
    }
}
