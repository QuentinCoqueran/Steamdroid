package com.example.steamdroid
import com.example.steamdroid.home.HomeActivity.Companion.inProgress
import com.example.steamdroid.model.Product
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

        var isFinished = false

        call.enqueue(object : Callback<MutableList<SearchGame>> {

            override fun onResponse(call: Call<MutableList<SearchGame>>, response: Response<MutableList<SearchGame>>) {
                println("TEST onResponse")
                if (response.isSuccessful) {
                    println("RESPONSE SUCCESS:")
                    val list = response.body()
                    println("LIST: $list")
                    /*
                    val products = mutableListOf<Product>()
                    var cpt = 0

                    for (game in list!!) {

                        GameDetailsRequest().getGame(game.appId!!) { gameDetails ->

                            if (gameDetails != null) {

                                products.add(
                                    Product(
                                        gameDetails.gameName.orEmpty(),
                                        gameDetails.price.orEmpty(),
                                        gameDetails.backGroundImg.orEmpty(),
                                        gameDetails.editorName.orEmpty(),
                                        gameDetails.backGroundImgTitle.orEmpty(),
                                    )
                                )
                            }else{
                                println("NULL")
                            }
                            cpt++
                            if (cpt == list.size) {
                                callback(products)
                            }
                        }

                    }*/

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
                /*println("ERROR: ${t.cause} EROOOOOOOOOOOOR")*/
            }
        })

    }


}

