package com.musicgarden.android.models;

import com.musicgarden.android.lib.ListItemController;

public class Artist implements ListItemController {

    private int id;
    private String name;
    private String image_url;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage_url() {
        return image_url;
    }
}
