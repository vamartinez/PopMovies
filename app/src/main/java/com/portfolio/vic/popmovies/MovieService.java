package com.portfolio.vic.popmovies;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.portfolio.vic.popmovies.db.Movie;
import com.raizlabs.android.dbflow.list.FlowQueryList;
import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.container.JSONArrayModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vic on 03/04/2016.
 */
public class MovieService {

    Context context;

    public MovieService(Context context) {
        this.context = context;
    }

    public void getMovies(final FlowContentObserver observer, String genPref) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(MoviApp.getServer());
        if (genPref.equals("1"))
            builder.appendEncodedPath(MoviApp.getMovieTopRated());
        else
            builder.appendEncodedPath(MoviApp.getMoviePopular());
        builder.appendQueryParameter("api_key", context.getResources().getString(R.string.movie_key));
        String myUrl = builder.build().toString();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, myUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject json = null;
                        JSONArray result = null;
                        try {
                            json = new JSONObject(response);
                            result = new JSONArray(json.getString("results"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JSONArrayModel<Movie> jsonArrayModel = new JSONArrayModel<Movie>(result, Movie.class);
                        observer.beginTransaction();
                        jsonArrayModel.save();
                        observer.endTransactionAndNotify();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
// Add the request to the RequestQueue.
        MoviApp.getQueue(context).add(stringRequest);
    }

}
