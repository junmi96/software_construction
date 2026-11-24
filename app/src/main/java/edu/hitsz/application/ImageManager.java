package edu.hitsz.application;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import edu.hitsz.aircraftwar2026.R;

public final class ImageManager {

    public static Bitmap HERO_IMAGE;
    public static Bitmap BACKGROUND_IMAGE;

    private ImageManager() {
    }

    public static void init(Resources resources) {
        if (HERO_IMAGE != null && BACKGROUND_IMAGE != null) {
            return;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        HERO_IMAGE = BitmapFactory.decodeResource(
                resources, R.drawable.hero, options);
        BACKGROUND_IMAGE = BitmapFactory.decodeResource(
                resources, R.drawable.bg, options);
    }
}