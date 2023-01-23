package repository;

import model.Post;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class PostRepository {

    private int countPosts = 1;

    private final List<Post> posts = new CopyOnWriteArrayList<>();

    public List<Post> all() {
        return posts;
    }

    public Optional<Post> getById(long id) {
        Post post = null;
        for (Post p : posts) {
            if (p.getId() == id) {
                post = p;
                break;
            }
        }
        return Optional.ofNullable(post);
    }

    public Post save(Post post) {
        if (post.getId() == 0) {
            posts.add(new Post(countPosts++, post.getContent()));
            return post;
        }
        Post existingPost;
        if (getById(post.getId()).isPresent()) {
            existingPost = getById(post.getId()).get();
            int index = posts.indexOf(existingPost);
            posts.set(index, post);
        } else {
            posts.add(post);
        }
        return post;
    }

    public void removeById(long id) {
        if (getById(id).isPresent()) {
            posts.remove(getById(id).get());
        }
    }
}
