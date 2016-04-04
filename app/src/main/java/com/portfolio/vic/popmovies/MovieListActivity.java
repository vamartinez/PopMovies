package com.portfolio.vic.popmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.portfolio.vic.popmovies.db.*;
import com.portfolio.vic.popmovies.dummy.DummyContent;
import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.raizlabs.android.dbflow.list.FlowQueryList;
import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.sql.language.SQLCondition;
import com.raizlabs.android.dbflow.sql.language.SQLite;
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
    private boolean mTwoPane;
    MovieService movieService;
    FlowContentObserver observer;
    List<Movie> movieList;
    private String prevPreference;

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
        getMovies();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void getMovies() {
        if (movieService == null)
            movieService = new MovieService(this);
        if (observer == null)
            observer = new FlowContentObserver();
        observer.registerForContentChanges(getApplicationContext(), Movie.class);
        FlowContentObserver.OnModelStateChangedListener modelChangeListener = new FlowContentObserver.OnModelStateChangedListener() {
            @Override
            public void onModelStateChanged(@Nullable Class<? extends Model> table, BaseModel.Action action, @NonNull SQLCondition[] primaryKeyValues) {
                reloadList();
            }
        };
        observer.endTransactionAndNotify();
        observer.addModelChangeListener(modelChangeListener);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String genPref = sharedPref.getString(SettingsActivity.KEY_PREF_GENERAL, "");
        this.prevPreference = genPref;
        movieService.getMovies(observer, genPref);
    }

    private void reloadList() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.movie_list);
                setupRecyclerView(recyclerView);
                if ((recyclerView) != null) {
                    recyclerView.getAdapter().notifyDataSetChanged();
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
            mFlowCursorList = new FlowCursorList<>(true, SQLite.select().from(Movie.class));
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
            Picasso.with(getApplicationContext()).load(movie.getImageFullPath(getApplicationContext())).into(holder.imageIV);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putLong(MovieDetailFragment.ARG_ITEM_ID, movie.getId());
                        MovieDetailFragment fragment = new MovieDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movie_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MovieDetailActivity.class);
                        intent.putExtra(MovieDetailFragment.ARG_ITEM_ID, movie.getId());
                        context.startActivity(intent);
                    }
                }
            });
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
}
