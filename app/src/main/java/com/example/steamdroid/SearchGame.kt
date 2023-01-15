package com.example.steamdroid


import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

data class SearchGame (
    var appId: Number? = null
)

class SearchGameTypeAdapter : TypeAdapter<List<SearchGame>>() {

    override fun read(input: JsonReader): List<SearchGame> {
        println("read")
        val searchGameList = mutableListOf<SearchGame>()
        val searchGame = SearchGame()
        val token  = input.peek()
        println("token $token")

        /*if (input.peek() == JsonToken.BEGIN_ARRAY) {
            input.beginArray()
            println("begin array")*/
            if (input.peek() == JsonToken.BEGIN_OBJECT) {
                println("begin object")
                input.beginObject()
                while (input.peek() == JsonToken.NAME) {
                    println("name")
                    val nextName = input.nextName()
                    if (nextName == "appid") {
                        println("appid")
                        searchGame.appId = input.nextLong()
                        searchGameList.add(searchGame)
                    } else {
                        println("SKIP nextName : $nextName")
                        input.skipValue()
                    }
                }
                input.endObject()
            }
           /* input.endArray()
        }*/
        println("return")
        return searchGameList
    }

    override fun write(out: JsonWriter?, value: List<SearchGame>) {
        TODO("Not yet implemented")

    }
}
