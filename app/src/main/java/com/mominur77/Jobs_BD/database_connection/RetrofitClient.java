package com.mominur77.Jobs_BD.database_connection;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;
    private static final String baseUrl="http://ip-api.com/";

    public static synchronized Retrofit getApiClient(){
        if (retrofit==null){
            retrofit= new Retrofit
                    .Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
