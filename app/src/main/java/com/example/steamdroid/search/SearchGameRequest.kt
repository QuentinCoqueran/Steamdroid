package com.example.steamdroid.search
import com.example.steamdroid.SteamApi
import com.example.steamdroid.home.HomeActivity.Companion.inProgress
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchGameRequest{

    private var gson: Gson = GsonBuilder()
        .registerTypeAdapter(List::class.java, SearchGameTypeAdapter())
        .create()
    private val retrofitAppId = Retrofit.Builder()
        .baseUrl("https://api.steampowered.com")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    //fun searchGame(callback: (MutableList<Product>?) -> Unit) {
    fun searchGame(callback: (MutableList<SearchGame>?) -> Unit) {

        println("searchGame")
        val api = retrofitAppId.create(SteamApi::class.java)
        println("api")
        val call = api.searchGame()
        println("after ?")

        println(call.request().url())


        call.enqueue(object : Callback<MutableList<SearchGame>> {

            override fun onResponse(call: Call<MutableList<SearchGame>>, response: Response<MutableList<SearchGame>>) {
                println("TEST onResponse")
                if (response.isSuccessful) {
                    println("RESPONSE SUCCESS:")
                    val list = response.body()
                    println("LIST: $list")

                    callback(list)

                } else {
                    println("RESPONSE ERROR: $response")
                    callback(null)
                }
            }
            override fun onFailure(call: Call<MutableList<SearchGame>>, t: Throwable) {
                println("TEST onFailure")
                t.printStackTrace()
                inProgress = false
            }
        })

    }


}

