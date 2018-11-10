package com.example.c02hp1dtdv35.healthapplication.retrofit;


import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

import com.example.c02hp1dtdv35.healthapplication.Login.Login;
import com.example.c02hp1dtdv35.healthapplication.Login.Signup;

/**
 * Created by ADMIN on 15/06/2017.
 */

public interface ApiInterface {
    @FormUrlEncoded
    @POST("signup")
    Call<Signup> signup(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("signin")
    Call<Login> login(@Field("username") String username, @Field("password") String password);

}
