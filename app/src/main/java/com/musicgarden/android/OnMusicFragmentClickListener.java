package com.musicgarden.android;

import android.os.Bundle;

import com.musicgarden.android.models.Song;

/**
 * Created by Necra on 15-09-2017.
 */

public interface OnMusicFragmentClickListener {
    public void onListItemClick(Class<?> detailActivityClass, Bundle bundle);
    public void onSongClick(Song song, String albumName, String albumArt);
    public void changeBackground(String url);
    public void addToLibrary(Song song);
}
