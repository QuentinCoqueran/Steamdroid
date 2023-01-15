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


class SearchGameRequest{

    var gson: Gson = GsonBuilder()
        .registerTypeAdapter(SearchGame::class.java, SearchGameTypeAdapter())
        .create()
    private val retrofitAppId = Retrofit.Builder()
        .baseUrl("https://steamcommunity.com")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val retrofitGameDetail = Retrofit.Builder()
        .baseUrl("https://steamcommunity.com")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun searchGame(search: String) {
        val api = retrofitAppId.create(SteamApi::class.java)
        val call = api.searchGame(search)
        println("after ?")

        println(call.request().url())
        call.enqueue(object : Callback<List<SearchGame>> {

            override fun onResponse(call: Call<List<SearchGame>>, response: Response<List<SearchGame>>) {
                println("TEST onResponse")
                if (response.isSuccessful) {
                    println("RESPONSE SUCCESS:")
                    val list = response.body()
                    println("LIST: $list")

                    /*for (game in list!!) {
                        println("GAME: $game")
                        println("GAME ID: ${game.appid}")
                        getGameDetails(game.appid)
                    }*/


                } else {
                    println("RESPONSE ERROR: $response")
                }
            }
            override fun onFailure(call: Call<List<SearchGame>>, t: Throwable) {
                println("TEST onFailure")
                t.printStackTrace()
                /*println("ERROR: ${t.cause} EROOOOOOOOOOOOR")*/
            }
        })
        // print response
        println(call.request().url())
    }


}

