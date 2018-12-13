package com.musicgarden.android.responses.list_responses;

import com.musicgarden.android.models.User;
import com.musicgarden.android.responses.ListResponse;

import java.util.List;

/**
 * Created by Necra on 19-11-2017.
 */

public class UserListResponse extends ListResponse {

    private List<User> list;

    public List<User> getList() {
        return list;
    }

    public void setList(List<User> list) {
        this.list = list;
    }
}
