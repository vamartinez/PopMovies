package com.portfolio.vic.popmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.portfolio.vic.popmovies.db.*;
import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLCondition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.Model;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity {


    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private static boolean mTwoPane;
    MovieService movieService;
    FlowContentObserver observer;
    List<Movie> movieList;
    private String prevPreference;
    private Movie movieInDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.movie_list);
        assert recyclerView != null;
        if (movieList == null)
            movieList = new ArrayList<>();


        int countColums = 2;
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            countColums = 3;
        }
        GridLayoutManager lLayout = new GridLayoutManager(this, countColums);
        recyclerView.setLayoutManager(lLayout);
        getMovies(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void getMovies(final Bundle savedInstanceState) {
        if (movieService == null)
            movieService = new MovieService(this);
        if (observer == null)
            observer = new FlowContentObserver();
        observer.registerForContentChanges(getApplicationContext(), Movie.class);
        FlowContentObserver.OnModelStateChangedListener modelChangeListener = new FlowContentObserver.OnModelStateChangedListener() {
            @Override
            public void onModelStateChanged(@Nullable Class<? extends Model> table, BaseModel.Action action, @NonNull SQLCondition[] primaryKeyValues) {
                reloadList(savedInstanceState);
            }
        };
        observer.endTransactionAndNotify();
        observer.addModelChangeListener(modelChangeListener);
    }

    private void reloadList(final Bundle savedInstanceState) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.movie_list);
                setupRecyclerView(recyclerView);
                if ((recyclerView) != null) {
                    recyclerView.getAdapter().notifyDataSetChanged();
                    if (savedInstanceState != null) {
                        long mCurrentMovie = savedInstanceState.getLong(MovieDetailFragment.ARG_ITEM_ID);
                        if (mCurrentMovie > 0L)
                            ((SimpleItemRecyclerViewAdapter) recyclerView.getAdapter()).loadDetail(mCurrentMovie);
                        savedInstanceState.putLong(MovieDetailFragment.ARG_ITEM_ID, -1L);
                    }
                }
            }
        });
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter());
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {


        private FlowCursorList<Movie> mFlowCursorList;

        public SimpleItemRecyclerViewAdapter() {
            if (mFlowCursorList == null) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String genPref = sharedPref.getString(SettingsActivity.KEY_PREF_GENERAL, "");
                if (genPref.equals("2")) {
                    mFlowCursorList = new FlowCursorList<>(true, Movie.class, Movie_Table.id.in(new Select(Favorite_Table.movi_id).from(Favorite.class).where()));
                } else {
                    mFlowCursorList = new FlowCursorList<>(true, Movie.class);
                }
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.movie_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Movie movie = mFlowCursorList.getItem(position);
            Picasso.with(getApplicationContext())
                    .load(movie.getImageFullPath(getApplicationContext()))
                    .placeholder(R.drawable.ic_sync_black_24dp)
                    .error(R.drawable.ic_error)
                    .into(holder.imageIV);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    movieInDetail = movie;
                    loadDetail(movie.getId());
                }
            });
        }

        private void loadDetail(long movieId) {
            if (findViewById(R.id.movie_detail_container) != null) {
                Bundle arguments = new Bundle();
                arguments.putLong(MovieDetailFragment.ARG_ITEM_ID, movieId);
                MovieDetailFragment fragment = new MovieDetailFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment)
                        .commit();
            } else {
                Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class);
                intent.putExtra(MovieDetailFragment.ARG_ITEM_ID, movieId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        }

        @Override
        public int getItemCount() {
            return mFlowCursorList.getCount();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView imageIV;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                imageIV = (ImageView) view.findViewById(R.id.imageIV);
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String genPref = sharedPref.getString(SettingsActivity.KEY_PREF_GENERAL, "");
        if (!genPref.equals(prevPreference)) {
            if (genPref != "" &&  Integer.valueOf(genPref).equals(2)) {
                genPref = (prevPreference.equals("0")) ? "1" : "0";
            } else {
                new Delete().from(Movie.class).where().query();
            }
            movieService.getMovies(observer, genPref);
            this.prevPreference = genPref;
        }
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (movieInDetail != null)
            savedInstanceState.putLong(MovieDetailFragment.ARG_ITEM_ID, movieInDetail.getId());
        super.onSaveInstanceState(savedInstanceState);
    }
}
