package com.example.steamdroid

import com.example.steamdroid.model.Game
import com.example.steamdroid.model.GameReviewTypeAdapter
import com.example.steamdroid.model.GameTypeAdapter
import com.example.steamdroid.model.ReviewerNameTypeAdapter
import com.example.steamdroid.search.SearchGameTypeAdapter
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitBuilder {
    private fun getSearchGameRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.steampowered.com")
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .registerTypeAdapter(List::class.java, SearchGameTypeAdapter())
                        .create()
                )
            )
            .addCallAdapterFactory(CoroutineCallAdapterFactory()) // Add this line
            .build()
    }

    private fun getGameDetailsRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://store.steampowered.com")
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .registerTypeAdapter(Game::class.java, GameTypeAdapter())
                        .create()
                )
            )
            .addCallAdapterFactory(CoroutineCallAdapterFactory()) // Add this line
            .build()
    }

    private fun getBestsellersRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.steampowered.com")
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory()) // Add this line
            .build()
    }

    private fun getGameReviewsRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://store.steampowered.com")
            .addConverterFactory(GsonConverterFactory.create(
                GsonBuilder()
                    .registerTypeAdapter(List::class.java, GameReviewTypeAdapter())
                    .create()
            ))
            .addCallAdapterFactory(CoroutineCallAdapterFactory()) // Add this line
            .build()
    }

    private fun getGameReviewsNameRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.steampowered.com")
            .addConverterFactory(GsonConverterFactory.create(
                GsonBuilder()
                    .setLenient()
                    .registerTypeAdapter(String::class.java, ReviewerNameTypeAdapter())
                    .create()
            ))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    val searchGameService: SteamApi = getSearchGameRetrofit().create(SteamApi::class.java)
    val gameDetailsService: SteamApi = getGameDetailsRetrofit().create(SteamApi::class.java)
    val bestsellersService: SteamApi = getBestsellersRetrofit().create(SteamApi::class.java)
    val gameReviewsService: SteamApi = getGameReviewsRetrofit().create(SteamApi::class.java)
    val gameReviewsNameService: SteamApi = getGameReviewsNameRetrofit().create(SteamApi::class.java)
}
