package com.example.steamdroid
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

class SteamApiClient<T> {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://steamcommunity.com/actions/SearchApps/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getGame(gameName: String, callback: Callback<Game>) {
        val api = retrofit.create(SteamApi::class.java)
        val call = api.getGame(gameName)
        call.enqueue(callback)
    }
}

interface SteamApi {
    @GET("SearchApps/{gameName}")
    fun getGame(@Path("gameName") gameName: String): Call<Game>
}
