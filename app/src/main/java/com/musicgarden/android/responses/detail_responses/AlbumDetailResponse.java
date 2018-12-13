package com.musicgarden.android.responses.detail_responses;

import com.musicgarden.android.models.Album;
import com.musicgarden.android.models.Song;
import com.musicgarden.android.responses.Response;

import java.util.ArrayList;

/**
 * Created by Necra on 13-09-2017.
 */

public class AlbumDetailResponse extends Response {
    private Album album;
    private ArrayList<Song> song_list;

    public Album getAlbum() {
        return album;
    }

    public ArrayList<Song> getSong_list() {
        return song_list;
    }

    public void setSong_list(ArrayList<Song> song_list) {
        this.song_list = song_list;
    }
}
