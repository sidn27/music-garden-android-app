package com.musicgarden.android.responses;

import com.musicgarden.android.models.User;

/**
 * Created by Necra on 13-09-2017.
 */

public class SignInResponse extends Response {
    private String token;
    private User user_detail;

    public String getToken() {
        return token;
    }

    public User getUser_detail() {
        return user_detail;
    }
}
