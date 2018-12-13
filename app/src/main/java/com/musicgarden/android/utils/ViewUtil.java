package com.musicgarden.android.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * Created by Necra on 14-09-2017.
 */

public class ViewUtil {
    public static void showToast(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void loadImage(Context context, String image_url, ImageView imageView)
    {
        Picasso picasso = Picasso.with(context);
        picasso.load(image_url)
                .fit()
                .centerCrop()
                .into(imageView);
    }
}
