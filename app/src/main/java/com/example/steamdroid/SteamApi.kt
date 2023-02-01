package com.example.steamdroid

import com.example.steamdroid.model.Game
import com.example.steamdroid.model.GameReview
import com.example.steamdroid.search.SearchGame
//import com.example.steamdroid.search.WrappedResponse
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SteamApi {
    @GET("/api/appdetails")
    fun getGame(@Query("appids") gameId: Number): Deferred<Game>

    @GET("/appreviews/{gameId}?json=1")
    fun getGameReviews(@Path("gameId") gameId: Number): Call<GameReview>

    @GET("/IStoreService/GetAppList/v1/?access_token=bb0a74dd61eb8c96a4391bd12402a3c8")
    fun searchGame(): Deferred<MutableList<SearchGame>>
}