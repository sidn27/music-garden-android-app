package com.musicgarden.android.models;

import com.musicgarden.android.lib.ListItemController;

/**
 * Created by Necra on 13-09-2017.
 */

public class User implements ListItemController{
    private String name;
    private String image_url;
    private String email;
    private int id;

    public void setName(String name) {
        this.name = name;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }
}
