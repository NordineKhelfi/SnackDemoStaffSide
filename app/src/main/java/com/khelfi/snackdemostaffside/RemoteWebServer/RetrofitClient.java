package com.khelfi.snackdemostaffside.RemoteWebServer;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 * Created by norma on 23/01/2018.
 */

public class RetrofitClient {

    public static Retrofit retrofit = null;

    public  static Retrofit getClient(String baseURL){

        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

}
