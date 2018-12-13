package com.musicgarden.android.responses;

import com.musicgarden.android.models.Album;
import com.musicgarden.android.models.Artist;
import com.musicgarden.android.models.Song;

import java.util.List;

/**
 * Created by Necra on 20-11-2017.
 */

public class HomeResponse extends Response {
    private List<Artist> artists;
    private List<Album> albums;
    private List<Song> songs;

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
}
