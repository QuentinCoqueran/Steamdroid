package com.example.steamdroid.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

data class GameReview (
    val author: String,
    val vote: Boolean,
    val review: String
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