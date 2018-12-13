package com.musicgarden.android;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicgarden.android.lib.NetworkLayer;
import com.musicgarden.android.lib.ViewController;
import com.musicgarden.android.models.Artist;
import com.musicgarden.android.responses.list_responses.ArtistListResponse;
import com.musicgarden.android.utils.ViewUtil;

import java.io.IOException;
import java.util.HashMap;

public class ArtistListActivity extends Fragment implements ViewController {

    private ListItemAdapter<Artist> artistListItemAdapter;
    private int page_number;
    private OnMusicFragmentClickListener activityCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.music_detail_activity, container, false);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm=new LinearLayoutManager(inflater.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.addItemDecoration(new ListItemDecoration((int)getResources().getDimension(R.dimen.music_player_height_collapse)));

        artistListItemAdapter = new ListItemAdapter<Artist>(getContext(), ArtistDetailActivity.class, activityCallback);

        recyclerView.setAdapter(artistListItemAdapter);

        page_number = 1;

        loadNextPage();

        return view;
    }


    @Override
    public void onResponse(String response) {

        ObjectMapper mapper = new ObjectMapper();
        try {
            ArtistListResponse artistListResponse = mapper.readValue(response, ArtistListResponse.class);

            if(artistListResponse.getResponse().equals("success")) {
                artistListItemAdapter.addAll(artistListResponse.getList());
                page_number++;
            }
        }
        catch (IOException e) {
            ViewUtil.showToast(getContext(), "Mapping error in Artist List Activity");
        }

    }

    @Override
    public void onError(Throwable t) {

        ViewUtil.showToast(getContext(), t.toString());

    }

    public void loadNextPage() {

        NetworkLayer<ArtistListActivity> networkLayer = new NetworkLayer<ArtistListActivity>(this);
        HashMap<String, String> headers = new HashMap<String, String>();
        HashMap<String, String> query = new HashMap<String, String>();
        query.put("page_number", String.valueOf(page_number));
        networkLayer.get(getString(R.string.api_music_get_artists), headers, query);

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
