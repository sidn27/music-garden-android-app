package com.musicgarden.android.stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by Necra on 19-11-2017.
 */

public class WSListener extends WebSocketListener {
    public static final int NORMAL_CLOSURE_STATUS = 1000;
    private StreamActivity streamActivity;

    public WSListener(StreamActivity streamActivity) {
        this.streamActivity = streamActivity;
    }
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        /*webSocket.send("Hello, it's SSaurel !");
        webSocket.send("What's up ?");*/
    }
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        streamActivity.handleMessage(text);
    }
    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        streamActivity.handleMessage(bytes.hex());
    }
    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
    }
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        t.printStackTrace();
    }
}