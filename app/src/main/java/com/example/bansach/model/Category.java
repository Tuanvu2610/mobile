package com.example.bansach.model;

public class Category {

    private int category_id;
    private String name_cate;
    private int parent_id;
    private String link;
    private boolean expanded = false;
    public Category() {
    }

    public Category(int category_id,
                    String name_cate,
                    int  parent_id,
                    String link,
                    boolean expanded) {
        this.category_id = category_id;
        this.name_cate = name_cate;
        this.parent_id = parent_id;
        this.link = link;
        this.expanded = expanded;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getName_cate() {
        return name_cate;
    }

    public void setName_cate(String name_cate) {
        this.name_cate = name_cate;
    }

    public int  getParent_id() {
        return parent_id;
    }

    public void setParent_id(int  parent_id) {
        this.parent_id = parent_id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
