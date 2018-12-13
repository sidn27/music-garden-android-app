package com.musicgarden.android.models;

import com.musicgarden.android.lib.ListItemController;

/**
 * Created by Necra on 13-09-2017.
 */

public class Album implements ListItemController {

    private int id;
    private String name;
    private int artist_id;
    private String image_url;

    @Override
    public String getImage_url() {
        return image_url;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getArtist_id() {
        return artist_id;
    }
}
