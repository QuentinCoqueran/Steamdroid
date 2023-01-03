package com.example.steamdroid
import com.google.firebase.firestore.auth.User
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


class GameDetailsRequest{

    var gson: Gson = GsonBuilder()
        .registerTypeAdapter(Game::class.java, GameTypeAdapter())
        .create()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://store.steampowered.com/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun getGame(gameId: Number) {
        val api = retrofit.create(SteamApi::class.java)
        val call = api.getGame(gameId)
        call.enqueue(object : Callback<Game> {
            override fun onResponse(call: Call<Game>, response: Response<Game>) {
                if (response.isSuccessful) {
                    val game = response.body()
                    println("GAME: $game")
                } else {
                    println("RESPONSE ERROR")
                }
            }
            override fun onFailure(call: Call<Game>, t: Throwable) {
                println("ERROR: ${t}")
            }
        })
        // print response
        println(call.request().url())
    }

    fun getGameReviews(gameId: Number, callback: Callback<GameReview>) {
        val api = retrofit.create(SteamApi::class.java)
        val call = api.getGameReviews(gameId)
        call.enqueue(callback)
    }
}

interface SteamApi {
    @GET("/api/appdetails")
    fun getGame(@Query("appids") gameId: Number): Call<Game>

    @GET("/appreviews/{gameId}?json=1")
    fun getGameReviews(@Path("gameId") gameId: Number): Call<GameReview>
}
