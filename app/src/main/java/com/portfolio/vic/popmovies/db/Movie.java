package com.portfolio.vic.popmovies.db;

import android.content.Context;
import android.net.Uri;

import com.portfolio.vic.popmovies.MoviApp;
import com.portfolio.vic.popmovies.R;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.provider.ContentProvider;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;
import java.util.List;

/**
 * Created by vic on 03/04/2016.
 */
@ModelContainer
@Table(database = MoviDatabase.class)
public class Movie extends BaseModel {
    @PrimaryKey
    long id;
    @Column
    String poster_path;
    @Column
    String original_title;
    @Column
    String title;
    @Column
    String release_date;
    @Column
    int vote_average;
    @Column
    String overview;

    public String getPoster_path() {
        return poster_path;
    }

    public String getImageFullPath(Context context){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority(MoviApp.getImage_server())
                .appendEncodedPath(MoviApp.getImage_path())
                .appendEncodedPath(this.poster_path);
        return  builder.toString();
    }

    public long getId() {
        return id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getTitle() {
        return title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public int getVote_average() {
        return vote_average;
    }

    public String getOverview() {
        return overview;
    }

    /*
    List<Reviews> reviewsList;
    List<Trailer> trailerList;
    @OneToMany(methods = {OneToMany.Method.SAVE, OneToMany.Method.DELETE}, variableName = "trailer")
    public List<Trailer> getTrailerList() {
        if (trailerList == null || trailerList.isEmpty()) {
            trailerList = SQLite.select()
                    .from(Trailer.class)
                    .where(Trailer_Table. .eq(id))
                    .queryList();
        }
        return trailerList;
    }
}

    private Boolean adult;
    private String backdrop_path;
    private int popularity;
    private int vote_count;

*/
}