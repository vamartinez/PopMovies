package com.portfolio.vic.popmovies.db;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by vic on 03/04/2016.
 */

@Database(name = MoviDatabase.NAME, version = MoviDatabase.VERSION)
public final  class MoviDatabase {
    public static final String NAME = "Movies";
    public static final int VERSION = 1;
//    @Table(Movie2.class) public static final String LISTS = "lists";
}
