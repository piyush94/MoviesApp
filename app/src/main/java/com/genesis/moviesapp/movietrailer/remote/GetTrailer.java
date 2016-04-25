package com.genesis.moviesapp.movietrailer.remote;

import com.genesis.moviesapp.movietrailer.data.MovieTrailer;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public interface GetTrailer {

    @GET("videos?api_key=104822a88d9fa8c31861e18640bb2777")
    Call<MovieTrailer> getResults();

    class Factory {
        public static GetTrailer service;

        public static GetTrailer getInstance(String url) {

            String BASE_URL = url;
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            service = retrofit.create(GetTrailer.class);

            return service;

        }
    }
}
