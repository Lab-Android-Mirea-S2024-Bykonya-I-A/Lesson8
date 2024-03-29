package com.mirea.bykonyaia.yandexmapintegration;

import android.app.Application;

import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;

public class YandexMapApplication extends Application {
    private final static String YANDEX_MAP_API_KEY = "e0e1acd5-5852-4e0a-807f-7a8a331c98aa";

    @Override
    public void onCreate() {
        super.onCreate();
        MapKitFactory.setApiKey(YANDEX_MAP_API_KEY);
    }
}
