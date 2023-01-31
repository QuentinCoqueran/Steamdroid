package com.example.steamdroid.game_details

import com.example.steamdroid.SteamApi
import com.example.steamdroid.model.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GameDetailsRequest {

    fun getGame(gameId: Number, lang: String, currency: String, callback: (Game?) -> Unit) {
        val gson: Gson = GsonBuilder()
            .registerTypeAdapter(Game::class.java, GameTypeAdapter())
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://store.steampowered.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val api = retrofit.create(SteamApi::class.java)
        val call = api.getGame(gameId, lang, currency)
        call.enqueue(object : Callback<Game> {
            override fun onResponse(call: Call<Game>, response: Response<Game>) {
                if (response.isSuccessful) {
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

    fun getGameReviews(gameId: Number, lang: String, json: Number, callback: (List<GameReview>?) -> Unit) {
        val gsonReview: Gson = GsonBuilder()
            .registerTypeAdapter(List::class.java, GameReviewTypeAdapter())
            .create()
        val retrofitReview = Retrofit.Builder()
            .baseUrl("https://store.steampowered.com")
            .addConverterFactory(GsonConverterFactory.create(gsonReview))
            .build()
        val api = retrofitReview.create(SteamApi::class.java)
        val call = api.getGameReviews(gameId, lang, json)
        call.enqueue(object : Callback<List<GameReview>> {
            override fun onResponse(call: Call<List<GameReview>>, response: Response<List<GameReview>>) {
                if (response.isSuccessful) {
                    val reviews: List<GameReview>? = response.body()
                    println("REVIEWS: $reviews")
                    if (reviews != null) {
                        callback(reviews)
                    }
                } else {
                    callback(null)
                }
            }
            override fun onFailure(call: Call<List<GameReview>>, t: Throwable) {
                println("ERROR: $t")
                callback(null)
            }
        })
    }

    fun getReviewerName(steamids: String, format: String, callback: (String?) -> Unit) {
        val gsonReview: Gson = GsonBuilder()
            .setLenient()
            .registerTypeAdapter(List::class.java, ReviewerNameTypeAdapter())
            .create()
        val retrofitReview = Retrofit.Builder()
            .baseUrl("https://api.steampowered.com")
            .addConverterFactory(GsonConverterFactory.create(gsonReview))
            .build()
        val api = retrofitReview.create(SteamApi::class.java)
        val key = "03B490F87625680CB895CD79AB9F59F2"
        val call = api.getReviewerName(key,"json", steamids)
        println("CALL: $steamids")
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val userName: String? = response.body()
                    println("USERNAME: $userName")
                    if (userName != null) {
                        callback(userName)
                    }
                } else {
                    println("Response not successful")
                    callback(null)
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                println("ERROR USERNAME: $t")
                callback(null)
            }
        })
    }
}
