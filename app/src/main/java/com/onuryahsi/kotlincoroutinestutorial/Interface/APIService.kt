package com.onuryahsi.kotlincoroutinestutorial.Interface

import com.onuryahsi.kotlincoroutinestutorial.Model.Post
import com.onuryahsi.kotlincoroutinestutorial.Model.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface APIService {

    @GET("/posts/{id}")
    fun getPost(@Path("id") id: Int): Call<Post>

    @GET("/users/2")
    fun getUser() : Call<User>
}
