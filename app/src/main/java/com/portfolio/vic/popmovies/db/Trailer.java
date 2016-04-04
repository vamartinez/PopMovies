package com.portfolio.vic.popmovies.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.container.ForeignKeyContainer;

/**
 * Created by vic on 03/04/2016.
 */
@Table(database = MoviDatabase.class)
public class Trailer extends BaseModel {
    @PrimaryKey
    String id;
    @Column
    String site;
    @ForeignKey(saveForeignKeyModel = false)
    ForeignKeyContainer<Movie> movieForeignKeyContainer;
    public void associateMovie(Movie movie) {
        movieForeignKeyContainer = FlowManager.getContainerAdapter(Movie.class).toForeignKeyContainer(movie);
    }
}
