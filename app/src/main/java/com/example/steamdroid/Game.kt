package com.example.steamdroid

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class Game (
    val gameName: String,
    val editorName: String,
    val backGroundImg: String, // header_img
    val backGroundImgTitle: String, // background
    val icone: String
)

class GameTypeAdapter : TypeAdapter<Game>() {

    override fun read(`in`: JsonReader): Game {
        `in`.beginObject()
        var gameName: String? = null
        var editorName: String? = null
        var backGroundImg: String? = null
        var backGroundImgTitle: String? = null
        var icone: String? = null
        println(`in`)
        while (`in`.hasNext()) {

            when (`in`.nextName()) {
                "" -> gameName = `in`.nextString()
                "editorName" -> editorName = `in`.nextString()
                "header_img" -> backGroundImg = `in`.nextString()
                "background" -> backGroundImgTitle = `in`.nextString()
                "icone" -> icone = `in`.nextString()
                else -> `in`.skipValue()
            }
        }
        `in`.endObject()
        return Game(gameName!!, editorName!!, backGroundImg!!, backGroundImgTitle!!, icone!!)
    }

    override fun write(out: JsonWriter?, value: Game?) {
        TODO("Not yet implemented")

    }
}
