package com.example.bansach.model;

public class Review {
    private String id;
    private int book_id;
    private String username;
    private String content;
    private float rating;
    private String time;
    private boolean visible;

    public Review() {
    }

    public Review(String id, int book_id, String username, String content, float rating, String time, boolean visible) {
        this.id = id;
        this.book_id = book_id;
        this.username = username;
        this.content = content;
        this.rating = rating;
        this.time = time;
        this.visible = visible;
    }
    //getter
    public String getId() { return id; }

    public int getBook_id() { return book_id; }

    public String getUsername() { return username;}

    public String getContent() { return content; }

    public float getRating() { return rating; }

    public String getTime() { return time; }

    public boolean isVisible() { return visible; }

    //setter
    public void setId(String id) { this.id = id; }

    public void setBook_id(int book_id) { this.book_id = book_id; }

    public void setUsername(String username) { this.username = username; }

    public void setContent(String content) { this.content = content; }

    public void setRating(float rating) { this.rating = rating; }

    public void setTime(String time) { this.time = time; }

    public void setVisible(boolean visible) { this.visible = visible; }

}