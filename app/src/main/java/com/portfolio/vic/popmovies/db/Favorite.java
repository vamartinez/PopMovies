package com.portfolio.vic.popmovies.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by vic on 03/04/2016.
 */
@ModelContainer
@Table(database = MoviDatabase.class)
public class Favorite extends BaseModel {
    @PrimaryKey(autoincrement = true)
    long id;
    @Column
    long movi_id;

    public void setMovi_id(long movi_id) {
        this.movi_id = movi_id;
    }
}
