package com.example.steamdroid

import com.google.firebase.firestore.auth.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.converter.moshi.MoshiConverterFactory

class BestsellersApiSteam {

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.steampowered.com")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val service = retrofit.create(BestSellersService::class.java)

    val call = service.getResponse()


    fun getResponse() {
        return call.enqueue(object : Callback<BestSellersResponse> {
            override fun onResponse(
                call: Call<BestSellersResponse>,
                response: Response<BestSellersResponse>
            ) {
                if (response.isSuccessful) {
                    val bestSellersResponse = response.body()
                    //add in list all appid
                    val listAppid = mutableListOf<Int>()
                    for (i in bestSellersResponse!!.response.ranks) {
                        listAppid.add(i.appid)
                    }
                    //UserApiSteam().getResponse(listAppid)
                    println("Response successful : $listAppid")
                } else {
                    println("Response not successful")
                }
            }

            override fun onFailure(call: Call<BestSellersResponse>, t: Throwable) {
                // Handle error
            }
        })
    }
}


interface BestSellersService {
    @GET("/ISteamChartsService/GetMostPlayedGames/v1/?")
    fun getResponse(): Call<BestSellersResponse>
}


