package com.musicgarden.android.responses.list_responses;

import com.musicgarden.android.models.Song;
import com.musicgarden.android.responses.ListResponse;

import java.util.ArrayList;

/**
 * Created by Necra on 13-09-2017.
 */

public class SongListResponse extends ListResponse {

    private ArrayList<Song> list;

    public ArrayList<Song> getList() {
        return list;
    }
}
