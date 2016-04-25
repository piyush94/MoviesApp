package com.genesis.moviesapp.movielistpopular.remote;

import com.genesis.moviesapp.movielistpopular.data.MoviesPopular;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;


public interface GetMoviesPopular {

    String BASE_URL = "https://api.themoviedb.org/3/";

    @GET("movie/popular?api_key=104822a88d9fa8c31861e18640bb2777")
    Call<MoviesPopular> getResults();

    class Factory {
        public static GetMoviesPopular service;

        public static GetMoviesPopular getInstance() {

            if (service == null) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                service = retrofit.create(GetMoviesPopular.class);
            } else {
                return service;
            }

            return service;

        }
    }

}
