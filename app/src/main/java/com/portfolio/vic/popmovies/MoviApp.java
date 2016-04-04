package com.portfolio.vic.popmovies;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by vic on 03/04/2016.
 */
public class MoviApp extends Application {
    private final static String MOVIE_POPULAR = "3/movie/popular";
    private final static String MOVIE_TOP_RATED = "3/movie/top_rated";
    private final static String server = "api.themoviedb.org";
    private final static String image_server = "image.tmdb.org";
    private final static String image_path = "t/p/w185";

    public static String getMovieTopRated() {
        return MOVIE_TOP_RATED;
    }

    public static String getMoviePopular() {
        return MOVIE_POPULAR;
    }

    public static String getServer() {
        return server;
    }

    public static String getImage_server() {
        return image_server;
    }

    public static String getImage_path() {
        return image_path;
    }

    private static RequestQueue queue;

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
    }

    public static RequestQueue getQueue(Context context) {
        if (queue == null) queue = Volley.newRequestQueue(context);
        return queue;
    }
}
