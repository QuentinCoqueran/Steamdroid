package com.example.steamdroid

import com.google.firebase.firestore.auth.User
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.intellij.lang.annotations.Language
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

class GameDetailsRequest {

    var gson: Gson = GsonBuilder()
        .registerTypeAdapter(Game::class.java, GameTypeAdapter())
        .create()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://store.steampowered.com")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun getGame(gameId: Number, lang: String, callback: (Game?) -> Unit) {
        val api = retrofit.create(SteamApi::class.java)
        val call = api.getGame(gameId, lang)
        call.enqueue(object : Callback<Game> {
            override fun onResponse(call: Call<Game>, response: Response<Game>) {
                if (response.isSuccessful) {
                    println("RESPONSE:" + response.body())
                    val game: Game? = response.body()
                    callback(game)
                } else {
                    callback(null)
                }
            }
            override fun onFailure(call: Call<Game>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun getGameReviews(gameId: Number, callback: Callback<GameReview>) {
        val api = retrofit.create(SteamApi::class.java)
        val call = api.getGameReviews(gameId)
        call.enqueue(callback)
    }
}


