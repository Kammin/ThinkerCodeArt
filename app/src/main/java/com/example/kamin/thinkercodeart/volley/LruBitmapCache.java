package com.example.kamin.thinkercodeart.volley;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader;
import com.example.kamin.thinkercodeart.activity.MainActivity;

public class LruBitmapCache extends LruCache<String, Bitmap> implements
        ImageLoader.ImageCache {
    public static final String TAG = MainActivity.class.getSimpleName();

    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        Log.d("cacheSize"," "+cacheSize);
        return cacheSize;
    }

    public LruBitmapCache() {
        this(getDefaultLruCacheSize());
    }

    public LruBitmapCache(int sizeInKiloBytes) {
        super(sizeInKiloBytes);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }

    @Override
    public Bitmap getBitmap(String url) {
        Log.d(TAG," "+url+"  =   "+get(url));
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        Log.d(TAG," "+url+"  =   "+bitmap.getByteCount());
        put(url, bitmap);
    }
}