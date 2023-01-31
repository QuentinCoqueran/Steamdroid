package com.example.steamdroid.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

data class GameReview (
    val author: String,
    val vote: Boolean,
    val content: String
)

class GameReviewTypeAdapter : JsonDeserializer<List<GameReview>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): List<GameReview> {
        val reviews = ArrayList<GameReview>()
        val jsonapp = json?.asJsonObject
        val tot = jsonapp?.get("reviews")?.asJsonArray

        tot?.forEach {
            val review = it.asJsonObject
            val gameReview = GameReview(
                review.get("author").asJsonObject.get("steamid").asString,
                review.get("voted_up").asBoolean,
                review.get("review").asString
            )
            reviews.add(gameReview)
        }
        return reviews
    }
}

class ReviewerNameTypeAdapter : JsonDeserializer<String> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): String {
        val jsonapp = json?.asJsonObject
        val response = jsonapp?.get("response")?.asJsonObject
        val players = response?.get("players")?.asJsonArray
        val user = players?.get(0)?.asJsonObject
        val userName = user?.get("personaname")?.asString
        return userName?: ""
    }
}