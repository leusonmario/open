package com.mapzen;

import android.app.Application;

public class MapzenApplication extends Application {

    // default to Reykjavik
    public static double[] DEFAULT_COORDINATES =  {
            Double.parseDouble("64.133333") * 1E6,
            Double.parseDouble("-21.933333") * 1E6
    };

    public static String LOG_TAG = "Mapzen: ";

    public static int getZoomLevel() {
        return zoomLevel;
    }

    public static void setZoomLevel(int zoomLevel) {
        MapzenApplication.zoomLevel = zoomLevel;
    }

    public static int zoomLevel = 15;


}