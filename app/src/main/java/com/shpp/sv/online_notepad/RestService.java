package com.shpp.sv.online_notepad;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface RestService {

    @GET("notes?transform=1")
    Call<JsonElement> getNotesList();

    @GET("notes/{id}")
    Call<Note> getNote(@Path("id") int noteId);

    @DELETE("notes/{id}")
    Call<Object> delNote(@Path("id") int noteId);

    @FormUrlEncoded
    @POST("notes")
    Call<Object> addNote(@Field("text") String text);

    @FormUrlEncoded
    @PUT("notes/{id}")
    Call<Object> editNote(@Path("id") int id, @Field("text") String text);
}
