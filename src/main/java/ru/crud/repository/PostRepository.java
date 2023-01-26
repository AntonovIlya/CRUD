package ru.crud.repository;

import org.springframework.stereotype.Repository;
import ru.crud.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepository {

    AtomicLong countPosts =  new AtomicLong(0);

    private final Map<Long, Post> posts = new ConcurrentHashMap<>();

    public synchronized List<Post> all() {
        return new ArrayList<>(posts.values());
    }

    public Optional<Post> getById(long id) {
        return Optional.ofNullable(posts.get(id));
    }

    public Post save(Post post) {
        synchronized (posts) {
            long id = post.getId();
            if (posts.containsKey(id)) {
                posts.put(id, new Post(id, post.getContent()));
            } else {
                countPosts.getAndIncrement();
                posts.put(countPosts.get(), new Post(countPosts.get(), post.getContent()));
            }
            return post;
        }
    }

    public void removeById(long id) {
        if (getById(id).isPresent()) {
            posts.remove(id);
        }
    }
}
