package com.example.steamdroid

import com.example.steamdroid.home.BestSellersResponse
import com.example.steamdroid.model.GameReview
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface SteamApi {
    @GET("/api/appdetails")
    fun getGame(
        @Query("appids") gameId: Number,
        @Query("l") lang: String,
        @Query("cc") currency: String 
    ): Call<Game>

    @GET("/appreviews/{gameId}")
    fun getGameReviews(
        @Path("gameId") gameId: Number,
        @Query("l") lang: String,
        @Query("json") json: Number
    ): Call<List<GameReview>>

    @GET("/actions/SearchApps/{search}")
    fun searchGame(@Path("search") search: String): Call<List<SearchGame>>

    @GET("/ISteamChartsService/GetMostPlayedGames/v1/?")
    fun getResponse(): Call<BestSellersResponse>
}