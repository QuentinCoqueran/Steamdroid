package com.example.steamdroid.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type


data class Game (
    var gameName: String? = null,
    var gameDescription: String? = null,
    var editorName: List<String>? = null,
    var backGroundImg: String? = null,
    var backGroundImgTitle: String? = null,
    var icone: String? = null,
    var price: String? = null
)

class GameTypeAdapter : JsonDeserializer<Game> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Game {
        val game = Game()
        val jsonapp = json?.asJsonObject
        val jsonString = jsonapp?.toString()
        val appId = jsonString?.let { jsonString.substring(2, it.indexOf("\"", 2)) }
        val data = jsonapp?.get("$appId")?.asJsonObject?.get("data")?.asJsonObject

        game.gameName = data?.get("name")?.asString
        game.gameDescription = data?.get("detailed_description")?.asString
        game.gameDescription = game.gameDescription?.take(1200)
        game.gameDescription = game.gameDescription?.replace("<br />", "")
        game.gameDescription = game.gameDescription?.replace("<br>", "\n")
        game.gameDescription = game.gameDescription?.replace(Regex("<img.*/>") , "")
        game.gameDescription = game.gameDescription?.replace("&quot;", "")
        if (game.gameDescription?.startsWith("\n") == true){
            game.gameDescription = game.gameDescription?.substring(1)
        }
        val editorList = mutableListOf<String>()
        data?.get("developers")?.asJsonArray?.forEach {
            editorList.add(it.asString)
        }
        game.editorName = editorList
        game.backGroundImg = data?.get("header_image")?.asString
        game.backGroundImgTitle = data?.get("background")?.asString
        game.icone = data?.get("header_image")?.asString
        game.price = data?.get("price_overview")?.asJsonObject?.get("final_formatted")?.asString
        return game
    }
}
