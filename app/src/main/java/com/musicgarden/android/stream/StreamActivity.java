package com.musicgarden.android.stream;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.musicgarden.android.GoogleSignInActivity;
import com.musicgarden.android.OnMusicFragmentClickListener;
import com.musicgarden.android.R;
import com.musicgarden.android.models.Song;
import com.musicgarden.android.models.User;
import com.musicgarden.android.utils.Globals;
import com.musicgarden.android.utils.ViewUtil;
import com.musicgarden.android.ws.BroadcastMessage;
import com.musicgarden.android.ws.CreateRoomMessage;
import com.musicgarden.android.ws.DisplayMessage;
import com.musicgarden.android.ws.Message;
import com.musicgarden.android.ws.UserListMessage;
import com.musicgarden.android.ws.UserMessage;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

import static android.media.MediaPlayer.MEDIA_INFO_BUFFERING_START;
import static com.musicgarden.android.stream.WSListener.NORMAL_CLOSURE_STATUS;
import static com.musicgarden.android.ws.Constants.*;

public class StreamActivity extends AppCompatActivity implements OnMusicFragmentClickListener {

    private WebSocket webSocket;
    private OkHttpClient client;
    private FragmentManager fm;
    private StreamHome streamHome;
    private StreamUser streamUser;

    private BroadcastMessage lastBroadcast;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction ft;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    ft = fm.beginTransaction();
                    ft.hide(streamUser);
                    ft.show(streamHome);
                    ft.commit();
                    break;
                case R.id.navigation_users:
                    ft = fm.beginTransaction();
                    ft.hide(streamHome);
                    ft.show(streamUser);
                    ft.commit();
                    break;
            }
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        client = new OkHttpClient();

        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        streamHome = new StreamHome();
        streamUser = new StreamUser();
        ft.add(R.id.stream_content, streamHome);
        ft.hide(streamHome);
        ft.add(R.id.stream_content, streamUser);
        ft.show(streamUser);
        ft.commit();

        Uri uri = getIntent().getData();

        if(uri != null) {
            streamHome.setOwner(false);
            streamUser.setOwner(false);
        }
        else {
            streamHome.setOwner(true);
            streamUser.setOwner(true);
        }

        if(uri == null) {
            createRoom();
        }
        else {

            if(loadSharedPrefs()) {

                String path = uri.getPath();
                String roomID = path.substring(path.lastIndexOf("/") + 1);
                connectToRoom(roomID);
            }
        }

    }

    private void connectToRoom() {
        Uri uri = getIntent().getData();

        String path = uri.getPath();
        String roomID = path.substring(path.lastIndexOf("/") + 1);
        connectToRoom(roomID);
    }

    private boolean loadSharedPrefs() {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.shared_prefs), Context.MODE_PRIVATE);
        String token = prefs.getString("User Token", null);
        if(token == null) {
            Globals.userLoggedIn = false;
            startActivityForResult(new Intent(this, GoogleSignInActivity.class), 0);
            return false;
        }
        else {
            Globals.userLoggedIn = true;
            String name = prefs.getString("User Name", null);
            String image_url = prefs.getString("User Image", null);
            String email = prefs.getString("User Email", null);
            Integer id = prefs.getInt("User ID", 0);

            Globals.setToken(token);
            Globals.setName(name);
            Globals.setEmail(email);
            Globals.setImage_url(image_url);
            Globals.setId(id);
            return true;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode != RESULT_OK) {
            finish();
        }
        else {
            connectToRoom();
        }
    }

    private void createRoom() {
        Request request = new Request.Builder().url("ws://52.66.138.181:8080/musicgarden/").build();
        WSListener listener = new WSListener(this);
        webSocket = client.newWebSocket(request, listener);

        CreateRoomMessage createRoomMessage = new CreateRoomMessage();
        createRoomMessage.setType(CREATE);
        User user = getLoggedInUser();
        createRoomMessage.setUser(user);

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        webSocket.send(gson.toJson(createRoomMessage));

    }

    public void handleMessage(String response) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        Message message = gson.fromJson(response, Message.class);

        switch(message.getType())
        {
            case NOTIFY_ROOM_CREATED:
                DisplayMessage createMessage = gson.fromJson(response, DisplayMessage.class);
                connectToRoom(createMessage.getResponse());
                Globals.roomID = createMessage.getResponse();
                break;
            case BROADCAST:
                BroadcastMessage broadcastMessage = gson.fromJson(response, BroadcastMessage.class);
                handleBroadcast(broadcastMessage);
                break;
            case DISPLAY:
                final DisplayMessage displayMessage = gson.fromJson(response, DisplayMessage.class);
                final Context context = this;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ViewUtil.showToast(context, displayMessage.getResponse());
                    }
                });

                if(displayMessage.getResponse().contains("No such") || displayMessage.getResponse().contains("Owner closed")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            if(Globals.getMediaPlayer() != null && Globals.getMediaPlayer().isPlaying()) {
                                Globals.getMediaPlayer().stop();
                                Globals.getMediaPlayer().reset();
                            }
                        }
                    });
                }

                break;
            case HEARTBEAT:
                break;
            case NOTIFY_USER_LIST:
                UserListMessage userListMessage = gson.fromJson(response, UserListMessage.class);
                updateUserList(userListMessage.getList());
                break;
        }
    }

    private void connectToRoom(String roomID) {
        Request request = new Request.Builder().url("ws://52.66.138.181:8080/musicgarden/" + roomID).build();
        WSListener listener = new WSListener(this);
        webSocket = client.newWebSocket(request, listener);

        UserMessage connectRoomMessage = new UserMessage();
        connectRoomMessage.setType(CONNECT);
        User user = getLoggedInUser();
        connectRoomMessage.setUser(user);

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        webSocket.send(gson.toJson(connectRoomMessage));

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while(true) {
                        sleep(1000);
                        sendHeartbeatMessage();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

    }

    private void sendHeartbeatMessage() {
        Message message = new Message();
        message.setType(HEARTBEAT);
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        webSocket.send(gson.toJson(message));
    }

    private User getLoggedInUser() {
        User user = new User();
        user.setEmail(Globals.getEmail());
        user.setImage_url(Globals.getImage_url());
        user.setName(Globals.getName());
        user.setId(Globals.getId());
        return user;
    }

    private void handleBroadcast(final BroadcastMessage bm) {
        MediaPlayer mp = Globals.getMediaPlayer();
        try {
            if (bm.getPlayer_action().equals("play")) {
                if(mp.isPlaying()) {
                    mp.stop();
                }
                mp.reset();
                mp.setDataSource(bm.getSong_stream_url());
                mp.prepare();
                final Context context = this;
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {

                        streamHome.musicBody.setVisibility(View.VISIBLE);
                        streamHome.songName.setText(bm.getSong_name());
                        streamHome.artistName.setText(bm.getArtist_name());
                        if(!streamHome.isOwner) {
                            streamHome.pause.setVisibility(View.GONE);
                            streamHome.play.setVisibility(View.GONE);
                        }
                        ViewUtil.loadImage(context, bm.getAlbum_image_url(), streamHome.songImage);


                        Date currentTime = Calendar.getInstance().getTime();
                        long diff = currentTime.getTime() - Long.parseLong(bm.getDatetime());

                        //Toast.makeText(context, "Diff : " + diff, Toast.LENGTH_SHORT).show();

                        mp.seekTo((int) diff + bm.getSeek_time());

                        //Toast.makeText(context, "Player Time : " + mp.getCurrentPosition(), Toast.LENGTH_SHORT).show();

                    }
                });

                mp.start();


            } else {
                mp.pause();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUserList(final List<User> userList) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                streamUser.updateUserList(userList);

            }
        });
    }

    private void sendBroadcast(Song song, String albumName, String albumArt) {
        lastBroadcast = new BroadcastMessage();
        lastBroadcast.setType(BROADCAST);
        lastBroadcast.setUser(getLoggedInUser());
        Date currentTime = Calendar.getInstance().getTime();
        lastBroadcast.setDatetime(String.valueOf(currentTime.getTime()));
        lastBroadcast.setSeek_time(0);
        lastBroadcast.setPlayer_action("play");
        lastBroadcast.setSong_name(song.getName());
        lastBroadcast.setArtist_name(albumName);
        lastBroadcast.setAlbum_image_url(albumArt);
        lastBroadcast.setSong_stream_url(song.getStream_url());
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        webSocket.send(gson.toJson(lastBroadcast));
    }


    @Override
    public void onListItemClick(Class<?> detailActivityClass, Bundle bundle) {
        return;
    }

    @Override
    public void onSongClick(Song song, String albumName, String albumArt) {
        streamHome.musicBody.setVisibility(View.VISIBLE);
        streamHome.songName.setText(song.getName());
        streamHome.artistName.setText(song.getArtist_name());
        streamHome.pause.setVisibility(View.VISIBLE);
        streamHome.play.setVisibility(View.GONE);
        ViewUtil.loadImage(this, song.getImage_url(), streamHome.songImage);
        sendBroadcast(song, song.getArtist_name(), song.getImage_url());
    }

    @Override
    public void changeBackground(String st) {
        lastBroadcast.setSeek_time(Globals.getMediaPlayer().getCurrentPosition());
        lastBroadcast.setPlayer_action(st);
        Date currentTime = Calendar.getInstance().getTime();
        lastBroadcast.setDatetime(String.valueOf(currentTime.getTime()));
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        webSocket.send(gson.toJson(lastBroadcast));

    }

    public void sendDestroyMessage() {

        UserMessage userMessage = new UserMessage();
        userMessage.setType(DESTROY);
        userMessage.setUser(getLoggedInUser());
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        webSocket.send(gson.toJson(userMessage));

    }


    @Override
    public void addToLibrary(Song song) {
        webSocket.close(NORMAL_CLOSURE_STATUS, "");
        if(Globals.getMediaPlayer() != null && Globals.getMediaPlayer().isPlaying()) {
            Globals.getMediaPlayer().stop();
            Globals.getMediaPlayer().reset();
        }
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(Globals.getMediaPlayer() != null) {
            if(Globals.getMediaPlayer().isPlaying()) {
                Globals.getMediaPlayer().stop();
            }
            Globals.getMediaPlayer().reset();
        }
    }
}
