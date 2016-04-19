package com.genesis.moviesapp;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.genesis.moviesapp.movietrailer.remote.GetTrailer;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ItemDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    public MovieListParcel movieDetails;
    @Bind(R.id.backdrop)
    ImageView backdrop;
    @Bind(R.id.poster)
    ImageView poster;
    @Bind(R.id.movie_title)
    TextView title;
    @Bind(R.id.movie_overview)
    TextView overview;
    @Bind(R.id.details)
    TextView details;
    @Bind(R.id.play_trailer)
    ImageButton playTrailer;
    private String movieID;
    @Bind(R.id.fav)
    ImageButton favBtn;
    @Bind(R.id.review_btn)
    Button reviewBtn;
    FavDB dbHandler;

    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHandler = ItemListActivity.dbHandler;

        if (getArguments().containsKey(ItemDetailFragment.ARG_ITEM_ID)) {

            movieDetails = getArguments().getParcelable(ItemDetailFragment.ARG_ITEM_ID);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);

        ButterKnife.bind(this, rootView);

        playTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (movieDetails != null) {
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                movieDetails.setTrailerUrl("https://www.youtube.com/watch?v=" +
                                        GetTrailer.Factory.getInstance("http://api.themoviedb.org/3/movie/" + movieDetails.getMovieId() + "/")
                                                .getResults().execute().body().getResults().get(0).getKey());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(movieDetails.getTrailerUrl())));
                        }
                    }.execute();

                }
            }
        });

        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dbHandler.getMovie(movieID)) {

                    dbHandler.delMovie(movieID);
                    movieDetails.setSAVED(0);
                    Toast.makeText(getActivity(), "Movie Deleted from Favorites", Toast.LENGTH_SHORT).show();
                    favBtn.setImageResource(android.R.drawable.star_big_off);

                } else {

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap bitmap = ((BitmapDrawable) poster.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                    movieDetails.setPoster(stream.toByteArray());
                    stream = new ByteArrayOutputStream();
                    bitmap = ((BitmapDrawable) backdrop.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                    movieDetails.setBackdrop(stream.toByteArray());

                    if (stream != null) {
                        movieDetails.setSAVED(1);
                        dbHandler.addMovie(movieDetails);
                        Toast.makeText(getActivity(), "Movie Added to Favorites", Toast.LENGTH_SHORT).show();
                        favBtn.setImageResource(android.R.drawable.star_big_on);
                        try {
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("mid", movieID);
                ItemReviewFragment fragment = new ItemReviewFragment();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.add(R.id.item_detail_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        try {
            title.setText(movieDetails.getTitle());
            if (movieDetails.getSAVED() == 1) {
                poster.setImageBitmap(BitmapFactory.decodeByteArray(movieDetails.getPoster(), 0, movieDetails.getPoster().length));
                backdrop.setImageBitmap(BitmapFactory.decodeByteArray(movieDetails.getBackdrop(), 0, movieDetails.getBackdrop().length));
            } else {
                Picasso.with(getActivity()).load(movieDetails.getPosterPath()).into(poster);
                Picasso.with(getActivity()).load(movieDetails.getBackdropPath()).into(backdrop);
            }
            overview.setText(movieDetails.getOverview());
            details.setText(movieDetails.getReleaseDate());
            details.append("\n" + "Rating : " + movieDetails.getRating() + "/10");
            movieID = movieDetails.getMovieId();
            if (dbHandler.getMovie(movieID))
                favBtn.setImageResource(android.R.drawable.star_big_on);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return rootView;
    }

}
