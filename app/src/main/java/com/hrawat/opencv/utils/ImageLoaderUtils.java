package com.hrawat.opencv.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.compat.BuildConfig;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.hrawat.opencv.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public final class ImageLoaderUtils {

    private ImageLoaderUtils() {
    }

    private static final String TAG = ImageLoaderUtils.class.getSimpleName();

    public static void loadImage(Context context, String url, ImageView imageView) {
        loadImage(context, url, imageView, R.drawable.ic_user_place_holder);
    }

    public static void loadImage(Context context, String url, ImageView imageView, final int resId) {
        DisplayImageOptions.Builder displayOptions = new DisplayImageOptions.Builder();
        if (resId > 0) {
            displayOptions.showImageForEmptyUri(resId);
            displayOptions.showImageOnLoading(resId);
            displayOptions.showImageOnFail(resId);
        }
        displayOptions.cacheOnDisk(true);
        displayOptions.cacheInMemory(true);
        displayOptions.imageScaleType(ImageScaleType.EXACTLY);
        displayOptions.bitmapConfig(Bitmap.Config.RGB_565);
        displayOptions.considerExifParams(true);
        displayOptions.displayer(new FadeInBitmapDisplayer(1000));
        if (TextUtils.isEmpty(url)) {
            if (resId > 0)
                imageView.setImageResource(resId);
            else
                imageView.setImageResource(R.drawable.ic_user_place_holder);
        } else {
            ImageLoader.getInstance().displayImage(url, imageView, displayOptions.build(), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    super.onLoadingFailed(imageUri, view, failReason);
                    LogUtils.e(TAG, failReason.toString());
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    LogUtils.e(TAG, "Image loaded successfully");
                }
            });
        }
    }

    public static void loadImage(Context context, String url, ImageView imageView, final Drawable res) {
        DisplayImageOptions.Builder displayOptions = new DisplayImageOptions.Builder();
        if (res != null) {
            displayOptions.showImageForEmptyUri(res);
            displayOptions.showImageOnLoading(res);
            displayOptions.showImageOnFail(res);
        }
        displayOptions.displayer(new FadeInBitmapDisplayer(1000));
        displayOptions.cacheOnDisk(true);
        displayOptions.cacheInMemory(true);
        displayOptions.imageScaleType(ImageScaleType.EXACTLY);
        displayOptions.bitmapConfig(Bitmap.Config.RGB_565);
        displayOptions.considerExifParams(true);
        if (TextUtils.isEmpty(url)) {
            if (res != null)
                imageView.setImageDrawable(res);
            else
                imageView.setImageResource(R.drawable.ic_user_place_holder);
        } else {
            ImageLoader.getInstance().displayImage(url, imageView, displayOptions.build(), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    super.onLoadingFailed(imageUri, view, failReason);
                    LogUtils.e(TAG, failReason.toString());
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    LogUtils.e(TAG, "Image loaded successfully");
                }
            });
        }
    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        if (BuildConfig.DEBUG)
            config.writeDebugLogs();
        ImageLoader.getInstance().init(config.build());
    }
}
