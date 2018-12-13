package com.musicgarden.android;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicgarden.android.lib.NetworkLayer;
import com.musicgarden.android.lib.ViewController;
import com.musicgarden.android.models.Album;
import com.musicgarden.android.models.Artist;
import com.musicgarden.android.responses.detail_responses.ArtistDetailResponse;
import com.musicgarden.android.responses.list_responses.AlbumListResponse;
import com.musicgarden.android.utils.ViewUtil;
import java.io.IOException;
import java.util.HashMap;

public class ArtistDetailActivity extends Fragment implements ViewController {

    private OnMusicFragmentClickListener activityCallback;
    private int type;
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.music_detail_activity, container, false);

        mView = view;

        Bundle bundle = getArguments();

        int id = 0;
        if(bundle != null) {
            id = bundle.getInt("ID", 0);
            loadData(id);
            type = 0;
        }
        else {
            loadData();
            type = 1;
        }

        return view;
    }

    private void loadData(int id) {
        NetworkLayer<ArtistDetailActivity> networkLayer = new NetworkLayer<ArtistDetailActivity>(this);
        HashMap<String, String> headers = new HashMap<String, String>();
        HashMap<String, String> query = new HashMap<String, String>();
        if(id != 0) {
            query.put("id", String.valueOf(id));
        }
        networkLayer.get(getString(R.string.api_music_get_artist), headers, query);

    }

    private void loadData() {
        NetworkLayer<ArtistDetailActivity> networkLayer = new NetworkLayer<ArtistDetailActivity>(this);
        HashMap<String, String> headers = new HashMap<String, String>();
        HashMap<String, String> query = new HashMap<String, String>();
        networkLayer.get(getString(R.string.api_music_get_albums), headers, query);

    }

    private void showData(ArtistDetailResponse artistDetailResponse) {

        View mainLayout = mView.findViewById(R.id.parentLayoutMusicDetail);
        mainLayout.setVisibility(View.VISIBLE);

        RecyclerView recyclerView = (RecyclerView)mView.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm=new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);
        recyclerView.setNestedScrollingEnabled(false);

        ListItemAdapter<Album> albumListItemAdapter = new ListItemAdapter<Album>(getContext(), AlbumDetailActivity.class, activityCallback);

        albumListItemAdapter.addAll(artistDetailResponse.getAlbum_list());

        recyclerView.setAdapter(albumListItemAdapter);

    }

    @Override
    public void onResponse(String response) {

        ObjectMapper mapper = new ObjectMapper();
        switch(type) {
            case 0:
                try {
                    ArtistDetailResponse artistDetailResponse = mapper.readValue(response, ArtistDetailResponse.class);

                    if(artistDetailResponse.getResponse().equals("success")) {
                        showData(artistDetailResponse);
                    }
                }
                catch (IOException e) {
                    ViewUtil.showToast(getContext(), "Mapping error in Artist Detail Activity");
                }
                break;

            case 1:
                try {
                    AlbumListResponse albumListResponse = mapper.readValue(response, AlbumListResponse.class);

                    if(albumListResponse.getResponse().equals("success")) {
                        ArtistDetailResponse artistDetailResponse = new ArtistDetailResponse();
                        artistDetailResponse.setAlbum_list(albumListResponse.getList());
                        showData(artistDetailResponse);
                    }
                }
                catch (IOException e) {
                    ViewUtil.showToast(getContext(), "Mapping error in Artist Detail Activity");
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
