package com.hrawat.opencv;

import android.app.Application;

import com.hrawat.opencv.utils.ImageLoaderUtils;

/**
 * Created by hrawat on 5/15/2017.
 */
public class OpenApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoaderUtils.initImageLoader(this);
    }
}
