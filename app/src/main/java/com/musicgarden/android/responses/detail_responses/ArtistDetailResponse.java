package com.musicgarden.android.responses.detail_responses;


import com.musicgarden.android.models.Album;
import com.musicgarden.android.models.Artist;
import com.musicgarden.android.responses.Response;

import java.util.ArrayList;

/**
 * Created by Necra on 13-09-2017.
 */

public class ArtistDetailResponse extends Response {
    private Artist artist;
    private ArrayList<Album> album_list;

    public Artist getArtist() {
        return artist;
    }

    public ArrayList<Album> getAlbum_list() {
        return album_list;
    }

    public void setAlbum_list(ArrayList<Album> album_list) {
        this.album_list = album_list;
    }
}
