package com.musicgarden.android.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.musicgarden.android.lib.NetworkService;

/**
 * Created by Necra on 14-09-2017.
 */

public class Globals {
    private static NetworkService networkService;

    private static MediaPlayer mediaPlayer;


    private static String token;

    private static String name;
    private static String image_url;
    private static String email;

    public static boolean userLoggedIn;

    public static String roomID;

    public static Integer getId() {
        return id;
    }

    public static void setId(Integer id) {
        Globals.id = id;
    }

    private static Integer id;

    public static void reset() {
        userLoggedIn = false;
        name = null;
        image_url = null;
        email = null;
        mediaPlayer = null;
        token = null;
        id = null;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Globals.token = token;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Globals.name = name;
    }

    public static String getImage_url() {
        return image_url;
    }

    public static void setImage_url(String image_url) {
        Globals.image_url = image_url;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        Globals.email = email;
    }

    public static MediaPlayer getMediaPlayer()
    {
        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        return mediaPlayer;
    }

    public static void setMediaPlayer(MediaPlayer mediaPlayer) {
        Globals.mediaPlayer = mediaPlayer;
    }


    public static NetworkService getNetworkService() {
        if(networkService == null) {
            networkService = new NetworkService("http://52.66.138.181:5020/");
        }
        return networkService;
    }

    public static void setNetworkService(NetworkService networkService) {
        Globals.networkService = networkService;
    }
}
