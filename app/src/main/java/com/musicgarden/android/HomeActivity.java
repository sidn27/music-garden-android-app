package com.musicgarden.android;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicgarden.android.lib.Callback;
import com.musicgarden.android.lib.NetworkLayer;
import com.musicgarden.android.lib.ViewController;
import com.musicgarden.android.models.Album;
import com.musicgarden.android.models.Artist;
import com.musicgarden.android.models.Song;
import com.musicgarden.android.responses.HomeResponse;
import com.musicgarden.android.responses.detail_responses.ArtistDetailResponse;
import com.musicgarden.android.utils.ViewUtil;

import java.io.IOException;
import java.util.HashMap;

public class HomeActivity extends Fragment implements ViewController, View.OnClickListener {

    private HomeFragmentClickListener activityCallback;
    private OnMusicFragmentClickListener activityCallback2;
    private RecyclerView songs;
    private RecyclerView albums;
    private RecyclerView artists;
    private TextView song;
    private TextView album;
    private TextView artist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_home, container, false);

        Bundle bundle = getArguments();
        
        artists = (RecyclerView)view.findViewById(R.id.recyclerView1);
        artist = (TextView)view.findViewById(R.id.button1);
        albums = (RecyclerView)view.findViewById(R.id.recyclerView2);
        album = (TextView)view.findViewById(R.id.button2);
        songs = (RecyclerView)view.findViewById(R.id.recyclerView3);
        song = (TextView)view.findViewById(R.id.button3); 
        
        artist.setOnClickListener(this);
        album.setOnClickListener(this);
        song.setOnClickListener(this);

        loadData();

        return view;
    }

    private void loadData() {
        NetworkLayer<HomeActivity> networkLayer = new NetworkLayer<HomeActivity>(this);
        HashMap<String, String> headers = new HashMap<String, String>();
        HashMap<String, String> query = new HashMap<String, String>();

        Callback callback = new Callback(HomeResponse.class) {
            @Override
            public void call(Object object) {
                showData((HomeResponse) object);
            }
        };

        networkLayer.get(getString(R.string.api_music_get_home), headers, query, callback);

    }

    private void showData(HomeResponse response) {

        artists.setHasFixedSize(true);
        albums.setHasFixedSize(true);
        songs.setHasFixedSize(true);

        LinearLayoutManager llm=new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        LinearLayoutManager llm2=new LinearLayoutManager(getContext());
        llm2.setOrientation(LinearLayoutManager.HORIZONTAL);
        LinearLayoutManager llm3=new LinearLayoutManager(getContext());
        llm3.setOrientation(LinearLayoutManager.HORIZONTAL);

        artists.setLayoutManager(llm);
        artists.setNestedScrollingEnabled(false);

        albums.setLayoutManager(llm2);
        albums.setNestedScrollingEnabled(false);

        songs.setLayoutManager(llm3);
        songs.setNestedScrollingEnabled(false);

        ListGridItemAdapter<Artist> artistListGridItemAdapter = new ListGridItemAdapter<Artist>(getContext(), ArtistDetailActivity.class, activityCallback2);

        artistListGridItemAdapter.addAll(response.getArtists());

        artists.setAdapter(artistListGridItemAdapter);

        ListGridItemAdapter<Album> albumListGridItemAdapter = new ListGridItemAdapter<Album>(getContext(), AlbumDetailActivity.class, activityCallback2);

        albumListGridItemAdapter.addAll(response.getAlbums());

        albums.setAdapter(albumListGridItemAdapter);

        ListGridItemAdapter<Song> songListGridItemAdapter = new ListGridItemAdapter<Song>(getContext(), null, activityCallback2);

        songListGridItemAdapter.addAll(response.getSongs());

        songs.setAdapter(songListGridItemAdapter);


    }

    @Override
    public void onResponse(String response) {

        ObjectMapper mapper = new ObjectMapper();
        try {
            HomeResponse homeResponse = mapper.readValue(response, HomeResponse.class);

            if(homeResponse.getResponse().equals("success")) {
                showData(homeResponse);
            }
        }
        catch (IOException e) {
            ViewUtil.showToast(getContext(), "Mapping error in Artist Detail Activity");
        }
    }

    @Override
    public void onError(Throwable t) {
        ViewUtil.showToast(getContext(), t.toString());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;

            try {
                activityCallback = (HomeFragmentClickListener) activity;
                activityCallback2 = (OnMusicFragmentClickListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnMusicFragmentClickListener");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button1:
                activityCallback.onArtist();
                break;
            case R.id.button2:
                activityCallback.onAlbum();
                break;
            case R.id.button3:
                activityCallback.onSong();
                break;
        }
    }
}
