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
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicgarden.android.lib.NetworkLayer;
import com.musicgarden.android.lib.ViewController;
import com.musicgarden.android.models.Album;
import com.musicgarden.android.models.Song;
import com.musicgarden.android.responses.detail_responses.AlbumDetailResponse;
import com.musicgarden.android.responses.detail_responses.ArtistDetailResponse;
import com.musicgarden.android.responses.list_responses.SongListResponse;
import com.musicgarden.android.utils.Globals;
import com.musicgarden.android.utils.ViewUtil;

import java.io.IOException;
import java.util.HashMap;

public class AlbumDetailActivity extends Fragment implements ViewController {

    private OnMusicFragmentClickListener activityCallback;

    private int id;
    private String name;
    private String image_url;
    private int type;
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.music_detail_activity, container, false);

        mView = view;

        Bundle bundle = getArguments();

        id = 0;
        name = null;
        image_url = null;
        if(bundle != null) {
            id = bundle.getInt("ID", 0);
            name = bundle.getString("Name");
            image_url = bundle.getString("Image URL");
            type = 0;

            loadData(id);
        }
        else {
            type = 1;
            loadData();
        }

        return view;
    }

    private void loadData(int id) {
        NetworkLayer<AlbumDetailActivity> networkLayer = new NetworkLayer<AlbumDetailActivity>(this);
        HashMap<String, String> headers = new HashMap<String, String>();
        HashMap<String, String> query = new HashMap<String, String>();
        if(id != 0) {
            query.put("id", String.valueOf(id));
        }
        headers.put("Authorization", Globals.getToken());
        networkLayer.get(getString(R.string.api_music_get_album), headers, query);

    }

    private void loadData() {
        NetworkLayer<AlbumDetailActivity> networkLayer = new NetworkLayer<AlbumDetailActivity>(this);
        HashMap<String, String> headers = new HashMap<String, String>();
        HashMap<String, String> query = new HashMap<String, String>();

        headers.put("Authorization", Globals.getToken());
        networkLayer.get(getString(R.string.api_music_get_songs), headers, query);

    }

    private void showData(AlbumDetailResponse albumDetailResponse) {

        View mainLayout = mView.findViewById(R.id.parentLayoutMusicDetail);
        mainLayout.setVisibility(View.VISIBLE);

        RecyclerView recyclerView = (RecyclerView)mView.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm=new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);
        recyclerView.setNestedScrollingEnabled(false);

        ListItemAdapter<Song> albumListItemAdapter = new ListItemAdapter<Song>(getContext(), null, activityCallback);

        albumListItemAdapter.setAdapterData(id, name, image_url);

        albumListItemAdapter.addAll(albumDetailResponse.getSong_list());

        recyclerView.setAdapter(albumListItemAdapter);

    }

    @Override
    public void onResponse(String response) {

        ObjectMapper mapper = new ObjectMapper();
        switch (type) {
            case 0:
                try {
                    AlbumDetailResponse albumDetailResponse = mapper.readValue(response, AlbumDetailResponse.class);

                    if(albumDetailResponse.getResponse().equals("success")) {
                        showData(albumDetailResponse);
                    }
                }
                catch (IOException e) {
                    ViewUtil.showToast(getContext(), "Mapping error in Album Detail Activity");
                }
                break;

            case 1:
                try {
                    SongListResponse songListResponse = mapper.readValue(response, SongListResponse.class);

                    if(songListResponse.getResponse().equals("success")) {
                        AlbumDetailResponse albumDetailResponse = new AlbumDetailResponse();
                        albumDetailResponse.setSong_list(songListResponse.getList());
                        showData(albumDetailResponse);
                    }
                }
                catch (IOException e) {
                    ViewUtil.showToast(getContext(), "Mapping error in Album Detail Activity");
                }
                break;

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
                activityCallback = (OnMusicFragmentClickListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnMusicFragmentClickListener");
            }
        }
    }
}
