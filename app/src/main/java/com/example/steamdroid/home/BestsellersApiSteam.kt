package com.example.steamdroid

import com.example.steamdroid.home.BestSellersResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

class BestsellersApiSteam {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.steampowered.com")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()


    fun getResponse(callback: (BestSellersResponse?) -> Unit) {
        val service: SteamApi = retrofit.create(SteamApi::class.java)
        val call = service.getResponse()
        call.enqueue(object : Callback<BestSellersResponse> {
            override fun onResponse(
                call: Call<BestSellersResponse>,
                response: Response<BestSellersResponse>
            ) {
                if (response.isSuccessful) {
                    val bestSellersResponse = response.body()
                    callback(bestSellersResponse)
                } else {
                    println("Response not successful")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<BestSellersResponse>, t: Throwable) {
                // Handle error
                callback(null)
            }
        })
    }
}



