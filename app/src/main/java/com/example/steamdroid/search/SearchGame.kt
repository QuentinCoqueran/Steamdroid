package com.example.steamdroid.search


import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

data class SearchGame(
    var appId: Number? = null,
    var appName: String? = null
)

class SearchGameTypeAdapter : JsonDeserializer<List<SearchGame>> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): List<SearchGame> {
        println("TEST deserialize")
        val searchGameList = ArrayList<SearchGame>()
        val jsonObject = json?.asJsonObject
        val apps = jsonObject?.get("response")?.asJsonObject?.get("apps")?.asJsonArray
        apps?.forEach {
            val app = it.asJsonObject
            val searchGame = SearchGame()
            searchGame.appId = app.get("appid").asInt
            searchGame.appName = app.get("name").asString
            searchGameList.add(searchGame)
        }
        return searchGameList
    }
}