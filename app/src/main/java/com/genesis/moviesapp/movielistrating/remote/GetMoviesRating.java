package com.genesis.moviesapp.movielistrating.remote;

import com.genesis.moviesapp.movielistrating.data.MoviesRating;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;


public interface GetMoviesRating {

    String BASE_URL = "https://api.themoviedb.org/3/";

    @GET("movie/top_rated?api_key=104822a88d9fa8c31861e18640bb2777")
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
