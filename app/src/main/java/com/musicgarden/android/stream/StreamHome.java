package com.musicgarden.android.stream;

import android.content.Context;
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
import android.widget.LinearLayout;
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
import com.musicgarden.android.models.Artist;
import com.musicgarden.android.models.Song;
import com.musicgarden.android.responses.list_responses.ArtistListResponse;
import com.musicgarden.android.responses.list_responses.SongListResponse;
import com.musicgarden.android.utils.Globals;
import com.musicgarden.android.utils.ViewUtil;

import java.io.IOException;
import java.util.HashMap;

public class StreamHome extends Fragment implements ViewController, View.OnClickListener {

    private ListItemAdapter<Song> songListItemAdapter;
    private OnMusicFragmentClickListener activityCallback;
    public TextView songName;
    public ImageView songImage;
    public TextView artistName;
    public LinearLayout musicBody;
    public ImageView play;
    public ImageView pause;
    public RecyclerView songRecycler;
    public boolean isOwner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.stream_home, container, false);

        songRecycler = (RecyclerView)view.findViewById(R.id.recyclerView);

        songRecycler.setHasFixedSize(true);

        LinearLayoutManager llm=new LinearLayoutManager(inflater.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        songRecycler.setLayoutManager(llm);
        songRecycler.setNestedScrollingEnabled(false);

        songRecycler.addItemDecoration(new ListItemDecoration((int)getResources().getDimension(R.dimen.music_player_height_collapse)));

        songListItemAdapter = new ListItemAdapter<Song>(getContext(), null, activityCallback);

        songRecycler.setAdapter(songListItemAdapter);
        
        initViews(view);

        if(isOwner) {
            loadNextPage();
        }
        else {
            songRecycler.setVisibility(View.GONE);
        }

        return view;
    }

    public void setOwner(boolean o) {
        isOwner = o;
    }
    
    private void initViews(View view) {
        songImage = (ImageView) view.findViewById(R.id.musicPlayerImageBig);
        artistName = (TextView) view.findViewById(R.id.musicPlayerArtistNameBig);
        songName = (TextView) view.findViewById(R.id.musicPlayerSongNameBig);
        musicBody = (LinearLayout) view.findViewById(R.id.musicPlayerBig);
        play = (ImageView) view.findViewById(R.id.musicPlayerBigPlay);
        pause = (ImageView) view.findViewById(R.id.musicPlayerBigPause);

        if(isOwner) {
            play.setOnClickListener(this);
            pause.setOnClickListener(this);

            if (Globals.getMediaPlayer().isPlaying()) {
                musicBody.setVisibility(View.VISIBLE);
            } else {
                musicBody.setVisibility(View.GONE);
            }
        }
        else {
            play.setVisibility(View.GONE);
            pause.setVisibility(View.GONE);
        }
        

    }

    @Override
    public void onResponse(String response) {

        ObjectMapper mapper = new ObjectMapper();
        try {
            SongListResponse albumListResponse = mapper.readValue(response, SongListResponse.class);

            if(albumListResponse.getResponse().equals("success")) {
                songListItemAdapter.addAll(albumListResponse.getList());
            }
        }
        catch (IOException e) {
            ViewUtil.showToast(getContext(), "Mapping error in Stream Home Activity");
        }

    }

    @Override
    public void onError(Throwable t) {

        ViewUtil.showToast(getContext(), t.toString());

    }

    public void loadNextPage() {

        NetworkLayer<StreamHome> networkLayer = new NetworkLayer<StreamHome>(this);
        HashMap<String, String> headers = new HashMap<String, String>();
        HashMap<String, String> query = new HashMap<String, String>();
        networkLayer.get(getString(R.string.api_music_get_songs), headers, query);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;

            try {
                activityCallback = (OnMusicFragmentClickListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnMusicFragmentClickListener");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.musicPlayerBigPlay:
                activityCallback.changeBackground("play");
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                break;

            case R.id.musicPlayerBigPause:
                activityCallback.changeBackground("pause");
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
                break;
        }
    }
}
