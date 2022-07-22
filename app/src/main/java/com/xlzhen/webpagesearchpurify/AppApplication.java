package com.xlzhen.webpagesearchpurify;

import android.app.Application;
import android.content.Context;


import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


public class AppApplication extends Application {
    private static Context mContext;
    private static ImageLoader imageLoader;

    public static ImageLoader getImageLoader() {
        return imageLoader;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        imageLoaderConfig();
    }

    public static Context getContext() {
        return mContext;
    }

    private void imageLoaderConfig() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
                .diskCache(new UnlimitedDiskCache(getExternalCacheDir() == null ? getCacheDir() : getExternalCacheDir()))
                .diskCacheFileCount(10000)
                .diskCacheSize(1024 * 1024 * 1024)
                .threadPoolSize(10)
                .build();
        com.nostra13.universalimageloader.utils.L.writeLogs(false);
        com.nostra13.universalimageloader.utils.L.writeDebugLogs(false);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }
}
