package com.musicgarden.android.responses.detail_responses;

import com.musicgarden.android.responses.Response;

/**
 * Created by Necra on 13-09-2017.
 */

public class UserDetailResponse extends Response {
    private String email;
    private String name;
    private String image_url;
    private int id;

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getImage_url() {
        return image_url;
    }

    public int getId() {
        return id;
    }
}
