package ru.crud.model;

public class AdapterPost {

    private final Post post;

    private boolean removed;

    public AdapterPost(Post post) {
        this.post = post;
    }

    public Post getPost() {
        return post;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }
}
