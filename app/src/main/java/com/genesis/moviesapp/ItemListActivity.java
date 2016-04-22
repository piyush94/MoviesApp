package com.genesis.moviesapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.genesis.moviesapp.movielistpopular.data.MoviesPopular;
import com.genesis.moviesapp.movielistpopular.data.Result;
import com.genesis.moviesapp.movielistpopular.remote.GetMoviesPopular;
import com.genesis.moviesapp.movielistrating.data.MoviesRating;
import com.genesis.moviesapp.movielistrating.remote.GetMoviesRating;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ItemListActivity extends AppCompatActivity {

    private boolean mTwoPane;
    private ArrayList<MovieListParcel> movieList;
    private View recyclerView;
    private static int displayWidth;
    private static int displayHeight;
    private boolean[] selItem = {true, false, false};
    public static FavDB dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        ((RecyclerView) recyclerView).setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        displayWidth = point.x;
        displayHeight = point.y;
        if (displayHeight < displayWidth) {
            int x = displayHeight;
            displayHeight = displayWidth;
            displayWidth = displayHeight;
            ((RecyclerView) recyclerView).setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        }

        dbHandler = new FavDB(ItemListActivity.this);


        GetPopularMovies();

        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }
    }



    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(movieList));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ArrayList<MovieListParcel> mValues;

        public SimpleItemRecyclerViewAdapter(ArrayList<MovieListParcel> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            if (holder.mItem.getSAVED() == 1) {
                holder.poster.setImageBitmap(BitmapFactory.decodeByteArray(holder.mItem.getPoster(), 0, holder.mItem.getPoster().length));
            } else
                Picasso.with(getApplicationContext()).load(holder.mItem.getPosterPath()).into(holder.poster);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putParcelable(ItemDetailFragment.ARG_ITEM_ID, holder.mItem);
                        ItemDetailFragment fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.mItem);
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView poster;
            public MovieListParcel mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                poster = (ImageView) view.findViewById(R.id.posterHolder);
                poster.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                ((int) Math.round(0.31 * displayHeight)), (int) Math.round(0.65 * displayWidth)));
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (selItem[0])
            menu.findItem(R.id.choice1).setChecked(true);
        else if (selItem[1])
            menu.findItem(R.id.choice2).setChecked(true);
        else if (selItem[2]) {
            menu.findItem(R.id.choice3).setChecked(true);
            movieList = dbHandler.getFavMovies();
            setupRecyclerView((RecyclerView) recyclerView);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.choice1) {

            item.setChecked(true);
            selItem[0] = true;
            selItem[1] = selItem[2] = false;
            GetPopularMovies();
            return true;

        } else if (item.getItemId() == R.id.choice2) {

            item.setChecked(true);
            selItem[1] = true;
            selItem[0] = selItem[2] = false;
            GetRatingMovies();
            return true;

        } else if (item.getItemId() == R.id.choice3) {

            ArrayList<MovieListParcel> tempList = dbHandler.getFavMovies();
            if (tempList.size() > 0) {
                movieList = tempList;
                item.setChecked(true);
                selItem[2] = true;
                selItem[0] = selItem[1] = false;
                setupRecyclerView((RecyclerView) recyclerView);
                return true;
            } else {
                Toast.makeText(getApplicationContext(), "No Movie in Favourites", Toast.LENGTH_SHORT).show();
                return true;
            }

        }

        return super.onOptionsItemSelected(item);
    }

    private void GetPopularMovies() {

        final ProgressDialog dialog = new ProgressDialog(ItemListActivity.this);
        dialog.setMessage("Loading");
        dialog.show();
        movieList = new ArrayList<>();

        GetMoviesPopular.Factory.getInstance().getResults().enqueue(new Callback<MoviesPopular>() {
            @Override
            public void onResponse(Call<MoviesPopular> call, Response<MoviesPopular> response) {

                final int resLen = response.body().getResults().size();

                for (int i = 0; i < resLen; i++) {

                    final String[] movieDetails = new String[8];
                    Result result = response.body().getResults().get(i);
                    movieDetails[0] = String.valueOf(result.getId());
                    movieDetails[1] = "http://image.tmdb.org/t/p/w300" + result.getPosterPath();
                    movieDetails[2] = "http://image.tmdb.org/t/p/w500" + result.getBackdropPath();
                    movieDetails[3] = result.getTitle();
                    movieDetails[4] = result.getReleaseDate();
                    movieDetails[5] = String.valueOf(result.getVoteAverage());
                    movieDetails[6] = result.getOverview();
                    movieDetails[7] = "https://www.youtube.com";

                    movieList.add(new MovieListParcel(movieDetails, 0, new byte[0], new byte[0]));

                }
                setupRecyclerView((RecyclerView) recyclerView);
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<MoviesPopular> call, Throwable t) {
                if (dialog.isShowing())
                    dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Network Not Available.", Toast.LENGTH_SHORT).show();
                selItem[2] = true;
                selItem[0] = selItem[1] = false;
                movieList = dbHandler.getFavMovies();
                setupRecyclerView((RecyclerView) recyclerView);

            }
        });
    }

    private void GetRatingMovies() {

        final ProgressDialog dialog = new ProgressDialog(ItemListActivity.this);
        dialog.setMessage("Loading");
        dialog.show();
        movieList = new ArrayList<>();

        GetMoviesRating.Factory.getInstance().getResults().enqueue(new Callback<MoviesRating>() {
            @Override
            public void onResponse(Call<MoviesRating> call, Response<MoviesRating> response) {

                final int resLen = response.body().getResults().size();

                for (int i = 0; i < resLen; i++) {

                    final String[] movieDetails = new String[8];
                    com.genesis.moviesapp.movielistrating.data.Result result = response.body().getResults().get(i);
                    movieDetails[0] = String.valueOf(result.getId());
                    movieDetails[1] = "http://image.tmdb.org/t/p/w300" + result.getPosterPath();
                    movieDetails[2] = "http://image.tmdb.org/t/p/w500" + result.getBackdropPath();
                    movieDetails[3] = result.getTitle();
                    movieDetails[4] = result.getReleaseDate();
                    movieDetails[5] = String.valueOf(result.getVoteAverage());
                    movieDetails[6] = result.getOverview();
                    movieDetails[7] = "https://www.youtube.com";

                    movieList.add(new MovieListParcel(movieDetails, 0, new byte[0], new byte[0]));
                }
                setupRecyclerView((RecyclerView) recyclerView);
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<MoviesRating> call, Throwable t) {
                if (dialog.isShowing())
                    dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Network Not Available.", Toast.LENGTH_SHORT).show();
                selItem[2] = true;
                selItem[0] = selItem[1] = false;
                movieList = dbHandler.getFavMovies();
                setupRecyclerView((RecyclerView) recyclerView);
            }
        });

    }

    @Override
    public void onBackPressed() {

        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }
}
