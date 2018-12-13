package com.musicgarden.android.lib;

public interface ViewController {
    public void onResponse(String response);
    public void onError(Throwable t);
}
