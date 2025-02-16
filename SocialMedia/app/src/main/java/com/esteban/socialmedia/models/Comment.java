package com.esteban.socialmedia.models;

public class Comment {
    private String id;
    private String comment;
    private String idUser;
    private String idPost;
    Long timestamp;

    public Comment(){}

    public Comment(String id, String comment, String idUser, String idPost, Long timestamp) {
        this.id = id;
        this.comment = comment;
        this.idUser = idUser;
        this.idPost = idPost;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
