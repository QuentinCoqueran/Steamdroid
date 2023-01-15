package com.example.steamdroid

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SteamApi {
    @GET("/api/appdetails")
    fun getGame(@Query("appids") gameId: Number): Call<Game>

    @GET("/appreviews/{gameId}?json=1")
    fun getGameReviews(@Path("gameId") gameId: Number): Call<GameReview>

    @GET("/actions/SearchApps/{search}")
    fun searchGame(@Path("search") search: String): Call<List<SearchGame>>
}