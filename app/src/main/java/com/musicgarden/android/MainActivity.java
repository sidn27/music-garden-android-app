package com.musicgarden.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicgarden.android.lib.NetworkLayer;
import com.musicgarden.android.lib.ViewController;
import com.musicgarden.android.models.Song;
import com.musicgarden.android.responses.Response;
import com.musicgarden.android.responses.detail_responses.ArtistDetailResponse;
import com.musicgarden.android.stream.StreamActivity;
import com.musicgarden.android.utils.Globals;
import com.musicgarden.android.utils.ViewUtil;

import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, OnMusicFragmentClickListener, ViewController, HomeFragmentClickListener {

    private View musicPlayer;
    private ImageView bgImage;
    private ImageView musicPlayerMiniPlay;
    private ImageView musicPlayerMiniPause;


    private TextView userName;
    private TextView userEmail;
    private ImageView userImage;

    private Fragment currentFragment;

    private HomeActivity homeActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*ArtistListActivity artistFragment = new ArtistListActivity();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.bodyMusicMainActivity, artistFragment)
                .commit();*/

        homeActivity = new HomeActivity();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.bodyMusicMainActivity, homeActivity)
                .commit();

        currentFragment = homeActivity;

        musicPlayer = findViewById(R.id.musicPlayer);
        bgImage = (ImageView)findViewById(R.id.backgroundImage);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(musicPlayer);


        musicPlayerMiniPause = (ImageView)musicPlayer.findViewById(R.id.musicPlayerMiniPause);
        musicPlayerMiniPlay = (ImageView)musicPlayer.findViewById(R.id.musicPlayerMiniPlay);

        userName = (TextView)navigationView.getHeaderView(0).findViewById(R.id.nameUser);
        userEmail = (TextView)navigationView.getHeaderView(0).findViewById(R.id.emailUser);
        userImage = (ImageView)navigationView.getHeaderView(0).findViewById(R.id.imageUser);


        musicPlayerMiniPause.setOnClickListener(this);
        musicPlayerMiniPlay.setOnClickListener(this);

        musicPlayer.setVisibility(View.GONE);

        loadSharedPrefs();

        if(!Globals.userLoggedIn) {
            startActivityForResult(new Intent(this, GoogleSignInActivity.class), 0);
        }
        else {
            ViewUtil.loadImage(this, Globals.getImage_url(), userImage);
            userName.setText(Globals.getName());
            userEmail.setText(Globals.getEmail());
        }
    }

    private void loadSharedPrefs() {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.shared_prefs), Context.MODE_PRIVATE);
        String token = prefs.getString("User Token", null);
        if(token == null) {
            Globals.userLoggedIn = false;
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

        }
    }

    private void clearSharedPrefs() {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.shared_prefs), MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("User Name");
        editor.remove("User Image");
        editor.remove("User Email");
        editor.remove("User Token");
        editor.remove("User ID");
        editor.apply();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode != RESULT_OK) {
            finish();
        }
        else {
            ViewUtil.loadImage(this, Globals.getImage_url(), userImage);
            userName.setText(Globals.getName());
            userEmail.setText(Globals.getEmail());
        }
    }

    @Override
    public void onListItemClick(Class<?> detailActivityClass, Bundle bundle) {
        Fragment fragment = Fragment.instantiate(this, detailActivityClass.getName(), bundle);

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.entry_from_right, R.anim.exit_to_left, R.anim.entry_from_left, R.anim.exit_to_right)
                .replace(R.id.bodyMusicMainActivity, fragment)
                .addToBackStack(detailActivityClass.getName())
                .commit();

    }

    @Override
    public void onSongClick(Song song, String albumName, String albumArt) {

        MediaPlayer mediaPlayer = Globals.getMediaPlayer();

        // play audio through music player
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(song.getStream_url());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            ViewUtil.showToast(this, "IOException MediaPlayer");
        }

        musicPlayer.setVisibility(View.VISIBLE);
        musicPlayerMiniPlay.setVisibility(View.GONE);

        TextView songName = (TextView)musicPlayer.findViewById(R.id.musicPlayerSongName);
        TextView artistName = (TextView)musicPlayer.findViewById(R.id.musicPlayerArtistName);
        ImageView imageView = (ImageView)musicPlayer.findViewById(R.id.musicPlayerImage);

        songName.setText(song.getName());
        artistName.setText(albumName);
        ViewUtil.loadImage(this, albumArt, imageView);

    }

    @Override
    public void addToLibrary(Song song) {
        NetworkLayer<MainActivity> networkLayer = new NetworkLayer<MainActivity>(this);
        HashMap<String, String> headers = new HashMap<String, String>();
        HashMap<String, String> query = new HashMap<String, String>();
        query.put("song_id", String.valueOf(song.getId()));
        headers.put("Authorization", Globals.getToken());
        networkLayer.get(getString(R.string.api_user_add_song), headers, query);
    }

    @Override
    public void onResponse(String response) {

        ObjectMapper mapper = new ObjectMapper();
        try {
            Response addToLibrary = mapper.readValue(response, Response.class);

            if(addToLibrary.getResponse().equals("success")) {
                ViewUtil.showToast(this, "Successfully added");
            }
            else {
                ViewUtil.showToast(this, addToLibrary.getError());
            }
        }
        catch (IOException e) {
            ViewUtil.showToast(this, "Mapping error in Main Activity");
        }
    }

    @Override
    public void onError(Throwable t) {
        ViewUtil.showToast(this, t.toString());
    }


    @Override
    public void changeBackground(String url) {

        if(url != null) {
            ViewUtil.loadImage(this, url, bgImage);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.libraryButton) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.entry_from_right, R.anim.exit_to_left, R.anim.entry_from_left, R.anim.exit_to_right)
                    .replace(R.id.bodyMusicMainActivity, homeActivity)
                    .commit();

        } else if (id == R.id.streamButton) {
            startActivity(new Intent(this, StreamActivity.class));
        } else if (id == R.id.logoutButton) {
            clearSharedPrefs();
            Globals.reset();
            startActivityForResult(new Intent(this, GoogleSignInActivity.class), 0);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.musicPlayerMiniPause:
                musicPlayerMiniPause.setVisibility(View.GONE);
                musicPlayerMiniPlay.setVisibility(View.VISIBLE);
                Globals.getMediaPlayer().pause();
                break;

            case R.id.musicPlayerMiniPlay:
                musicPlayerMiniPause.setVisibility(View.VISIBLE);
                musicPlayerMiniPlay.setVisibility(View.GONE);
                Globals.getMediaPlayer().start();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Globals.getMediaPlayer().isPlaying()) {
            musicPlayer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onArtist() {

        ArtistListActivity artistFragment = new ArtistListActivity();

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.entry_from_right, R.anim.exit_to_left, R.anim.entry_from_left, R.anim.exit_to_right)
                .replace(R.id.bodyMusicMainActivity, artistFragment)
                .addToBackStack(artistFragment.getClass().getName())
                .commit();

        currentFragment = artistFragment;
    }

    @Override
    public void onAlbum() {

        ArtistDetailActivity artistFragment = new ArtistDetailActivity();

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.entry_from_right, R.anim.exit_to_left, R.anim.entry_from_left, R.anim.exit_to_right)
                .replace(R.id.bodyMusicMainActivity, artistFragment)
                .addToBackStack(artistFragment.getClass().getName())
                .commit();

        currentFragment = artistFragment;
    }

    @Override
    public void onSong() {

        AlbumDetailActivity artistFragment = new AlbumDetailActivity();


        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.entry_from_right, R.anim.exit_to_left, R.anim.entry_from_left, R.anim.exit_to_right)
                .replace(R.id.bodyMusicMainActivity, artistFragment)
                .addToBackStack(artistFragment.getClass().getName())
                .commit();


        currentFragment = artistFragment;
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
