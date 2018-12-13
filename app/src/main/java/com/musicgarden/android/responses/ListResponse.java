package com.musicgarden.android.responses;

import com.musicgarden.android.responses.Response;

/**
 * Created by Necra on 13-09-2017.
 */

public abstract class ListResponse extends Response {

    private int page_count;

    public int getPage_count() {
        return page_count;
    }
}
