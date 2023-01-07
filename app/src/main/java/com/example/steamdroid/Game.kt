package com.example.steamdroid

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

data class Game (
    var gameName: String? = null,
    var gameDescription: String? = null,
    var editorName: List<String>? = null,
    var backGroundImg: String? = null, // header_img
    var backGroundImgTitle: String? = null, // background
    /*var icone: String? = null, // A definir*/
    var price: String? = null //price_overview
)

class GameTypeAdapter : TypeAdapter<Game>() {

    override fun read(input: JsonReader): Game {
        val game = Game()
        val token  = input.peek()
        if (token == JsonToken.BEGIN_OBJECT) {
            input.beginObject()
            while (input.peek() != JsonToken.END_OBJECT) {
                if (input.peek() == JsonToken.BEGIN_OBJECT) {
                    input.beginObject()
                    while (input.peek() != JsonToken.END_OBJECT) {
                        if (input.peek() == JsonToken.BEGIN_OBJECT) {
                            input.beginObject()
                            while (input.peek() != JsonToken.END_OBJECT) { // Boucle data
                                if (input.peek() == JsonToken.NAME) {
                                    when (input.nextName()) {
                                        "name" -> {
                                            game.gameName = input.nextString()
                                        }
                                        "detailed_description" -> {
                                            game.gameDescription = input.nextString().take(100)
                                        }
                                        "developers" -> {
                                            if (input.peek() == JsonToken.BEGIN_ARRAY) {
                                                input.beginArray()
                                                val editorList = mutableListOf<String>()
                                                while (input.peek() != JsonToken.END_ARRAY) {
                                                    input.nextString().let { editorList.add(it) }
                                                }
                                                input.endArray()
                                                game.editorName = editorList
                                            }
                                        }
                                        "header_image" -> {
                                            game.backGroundImg = input.nextString()
                                        }
                                        "price_overview" -> {
                                            if (input.peek() == JsonToken.BEGIN_OBJECT) {
                                                input.beginObject()
                                                while (input.peek() != JsonToken.END_OBJECT) {
                                                    if (input.peek() == JsonToken.NAME && input.nextName() == "final_formatted") {
                                                        game.price = input.nextString()
                                                    }else {
                                                        input.skipValue()
                                                    }
                                                }
                                                input.endObject()
                                            }
                                        }
                                        "background" -> {
                                            game.backGroundImgTitle = input.nextString()
                                        }
                                        else -> input.skipValue()
                                    }
                                }else {
                                    input.skipValue()
                                }
                            }
                            input.endObject()
                        } else {
                            input.skipValue()
                        }
                    }
                    input.endObject()
                }
                else {
                    input.skipValue()
                }
            }
            input.endObject()
        }
        return game;
    }

    override fun write(out: JsonWriter?, value: Game?) {
        TODO("Not yet implemented")

    }
}
