package com.musicgarden.android;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.musicgarden.android.lib.ListItemController;
import com.musicgarden.android.models.Song;
import com.musicgarden.android.utils.ViewUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Necra on 14-09-2017.
 */

public class ListItemAdapter<T> extends RecyclerView.Adapter<ListItemAdapter.ListItemViewHolder> {

    private ArrayList<T> list;
    private Context context;
    private Class<?> clazz;
    private OnMusicFragmentClickListener activityCallback;
    private int id;
    private String name;
    private String image_url;

    public ListItemAdapter(Context context, Class<?> clazz, OnMusicFragmentClickListener activityCallback) {
        list = new ArrayList<>();
        this.context = context;
        this.clazz = clazz;
        this.activityCallback = activityCallback;
    }

    public void setAdapterData(int id, String name, String image_url)
    {
        this.id = id;
        this.name = name;
        this.image_url = image_url;
    }

    @Override
    public void onBindViewHolder(ListItemAdapter.ListItemViewHolder holder, int position) {

        ListItemController object = (ListItemController)list.get(position);

        ViewUtil.loadImage(context, object.getImage_url(), holder.vImage);

        holder.vName.setText(object.getName());
    }


    @Override
    public ListItemAdapter.ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.music_list_item, viewGroup, false);
        return new ListItemAdapter.ListItemViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void addAll(List<T> models) {
        for (T model : models) {
            list.add(model);
        }
        notifyDataSetChanged();
    }

    public void newList() {
        list = new ArrayList<>();
    }


    class ListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView vName;
        private ImageView vImage;

        public ListItemViewHolder(View v) {
            super(v);

            vName = (TextView) v.findViewById(R.id.nameList);
            vImage = (ImageView) v.findViewById(R.id.imageList);
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            ListItemController object = (ListItemController)list.get(position);


            if(clazz != null) {

                Bundle bundle = new Bundle();
                bundle.putInt("ID", object.getId());
                bundle.putString("Name", object.getName());
                bundle.putString("Image URL", object.getImage_url());

                activityCallback.onListItemClick(clazz, bundle);
                activityCallback.changeBackground(object.getImage_url());

            }
            else if(activityCallback !=null) {
                activityCallback.onSongClick((Song)object, name, image_url);
            }

        }
    }
}
