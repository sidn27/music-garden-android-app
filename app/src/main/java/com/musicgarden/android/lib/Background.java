package com.musicgarden.android.lib;

import android.os.Handler;

import java.util.LinkedList;

/**
 * Created by Necra on 24-12-2017.
 */

public class Background {

    private static Handler handler;
    private static Background background;
    private static LinkedList<Runnable> threads;

    private Background() {
        handler = new Handler();
        threads = new LinkedList<Runnable>();
    }

    public static Background getInstance() {
        if(background == null) {
            background = new Background();
        }
        return background;
    }

    public Handler getHandler() {
        return handler;
    }

    public void registerRunnable(Runnable r, long startTime) {
        threads.add(r);
        handler.postDelayed(r, startTime);
    }

    public void unregisterRunnable(Runnable r) {
        if(threads.contains(r)) {
            threads.remove(r);
            handler.removeCallbacks(r);
        }
    }
}
