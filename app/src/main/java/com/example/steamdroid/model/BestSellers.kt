package com.example.steamdroid.model

data class BestSellersResponse(
    val response: Response
)
data class Response(
    val rollup_date: Int,
    val ranks: List<Rank>
)
data class Rank(
    val rank: Int,
    val appid: Int,
    val last_week_rank: Int,
    val peak_in_game: Int
)

