package com.example.baz.studentorganizer.network;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

import com.example.baz.studentorganizer.models.Response;
import com.example.baz.studentorganizer.models.User;

/**
 * Created by Baz on 23/11/2017.
 */

public interface Retrofit_Interface {

    @POST("authenticate")
    Observable<Response> login();

    @POST("users")
    Observable<Response> register(@Body User user);

    @GET("users/{email}")
    Observable<User> getProfile(@Path("email") String email);


}
