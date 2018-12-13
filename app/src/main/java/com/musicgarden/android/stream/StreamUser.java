package com.musicgarden.android.stream;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicgarden.android.ArtistDetailActivity;
import com.musicgarden.android.ListItemAdapter;
import com.musicgarden.android.ListItemDecoration;
import com.musicgarden.android.OnMusicFragmentClickListener;
import com.musicgarden.android.R;
import com.musicgarden.android.lib.NetworkLayer;
import com.musicgarden.android.lib.ViewController;
import com.musicgarden.android.models.Song;
import com.musicgarden.android.models.User;
import com.musicgarden.android.responses.list_responses.SongListResponse;
import com.musicgarden.android.responses.list_responses.UserListResponse;
import com.musicgarden.android.utils.Globals;
import com.musicgarden.android.utils.ViewUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class StreamUser extends Fragment {

    private ListItemAdapter<User> userListItemAdapter;
    private TextView inviteButton;
    private TextView quitButton;
    private boolean isOwner;

    public void setOwner(boolean o) {
        isOwner = o;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.stream_user_list, container, false);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm=new LinearLayoutManager(inflater.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.addItemDecoration(new ListItemDecoration((int)getResources().getDimension(R.dimen.music_player_height_collapse)));

        userListItemAdapter = new ListItemAdapter<User>(getContext(), null, null);

        recyclerView.setAdapter(userListItemAdapter);
        quitButton = (TextView) view.findViewById(R.id.quitButton);
        inviteButton = (TextView) view.findViewById(R.id.inviteButton);

        if(isOwner) {

            inviteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String roomLink = "www.musicgarden.com/musicgarden/" + Globals.roomID;
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, roomLink);
                        startActivity(intent);
                    } catch (Exception e) {
                        ViewUtil.showToast(getActivity(), "Can't share\nTry again");
                    }
                }
            });

            quitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((StreamActivity) getActivity()).sendDestroyMessage();
                }
            });
        }
        else {
            inviteButton.setVisibility(View.GONE);
            quitButton.setVisibility(View.VISIBLE);
            quitButton.setText("LEAVE");

            quitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((StreamActivity) getActivity()).addToLibrary(null);
                }
            });
        }

        return view;
    }

    public void updateUserList(List<User> userList) {

        userListItemAdapter.newList();
        userListItemAdapter.addAll(userList);

    }

}
