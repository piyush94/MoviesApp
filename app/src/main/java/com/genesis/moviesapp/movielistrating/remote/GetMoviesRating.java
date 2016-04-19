package com.genesis.moviesapp.movielistrating.remote;

import com.genesis.moviesapp.movielistrating.data.MoviesRating;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;


public interface GetMoviesRating {

    String BASE_URL = "https://api.themoviedb.org/3/discover/";

    @GET("movie?api_key=YOUR_API_KEY&sort_by=vote_count.desc")
    Call<MoviesRating> getResults();

    class Factory {
        public static GetMoviesRating service;

        public static GetMoviesRating getInstance() {

            if (service == null) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                service = retrofit.create(GetMoviesRating.class);
            } else {
                return service;
            }

            return service;

        }
    }
}
