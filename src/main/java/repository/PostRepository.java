package repository;

import model.Post;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class PostRepository {

    private final AtomicInteger countPosts = new AtomicInteger(1);

    private final List<Post> posts = new LinkedList<>();

    public synchronized List<Post> all() {
        return posts;
    }

    public Optional<Post> getById(long id) {
        Post post = null;
        synchronized (posts) {
            for (Post p : posts) {
                if (p.getId() == id) {
                    post = p;
                    break;
                }
            }
        }
        return Optional.ofNullable(post);
    }

    public Post save(Post post) {
        if (post.getId() == 0) {
            synchronized (posts) {
                posts.add(new Post(countPosts.getAndIncrement(), post.getContent()));
            }
            return post;
        }
        Post existingPost;
        if (getById(post.getId()).isPresent()) {
            existingPost = getById(post.getId()).get();
            synchronized (posts) {
                int index = posts.indexOf(existingPost);
                posts.set(index, post);
            }
        } else {
            synchronized (posts) {
                posts.add(post);
            }
        }
        return post;
    }

    public void removeById(long id) {
        if (getById(id).isPresent()) {
            synchronized (posts) {
                posts.remove(getById(id).get());
            }
        }
    }
}
