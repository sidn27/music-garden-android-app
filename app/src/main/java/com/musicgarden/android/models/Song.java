package com.musicgarden.android.models;

import com.musicgarden.android.lib.ListItemController;

/**
 * Created by Necra on 13-09-2017.
 */

public class Song implements ListItemController {

    private int id;
    private String name;
    private String stream_url;
    private int album_id;
    private String image_url;
    private String artist_name;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getStream_url() {
        return stream_url;
    }

    public int getAlbum_id() {
        return album_id;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }
}
