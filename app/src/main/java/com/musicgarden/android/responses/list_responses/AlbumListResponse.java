package com.musicgarden.android.responses.list_responses;

import com.musicgarden.android.models.Album;
import com.musicgarden.android.responses.ListResponse;

import java.util.ArrayList;

/**
 * Created by Necra on 13-09-2017.
 */

public class AlbumListResponse extends ListResponse {

    private ArrayList<Album> list;

    public ArrayList<Album> getList() {
        return list;
    }
}
