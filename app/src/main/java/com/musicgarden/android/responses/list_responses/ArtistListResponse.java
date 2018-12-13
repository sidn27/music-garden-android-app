package com.musicgarden.android.responses.list_responses;

import com.musicgarden.android.models.Artist;
import com.musicgarden.android.responses.ListResponse;

import java.util.ArrayList;

public class ArtistListResponse extends ListResponse {

    private ArrayList<Artist> list;

    public ArrayList<Artist> getList() {
        return list;
    }
}
