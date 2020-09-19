package com.mominur77.Jobs_BD.database_connection;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("json")
    Call<JsonElement> getIpInfo();

}
