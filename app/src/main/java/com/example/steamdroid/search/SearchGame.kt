package com.example.steamdroid


import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
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


/*
class SearchGameTypeAdapter : TypeAdapter<MutableList<SearchGame>>() {

    override fun read(input: JsonReader): MutableList<SearchGame> {
        println("read")
        val searchGameList = mutableListOf<SearchGame>()
        val searchGame = SearchGame()
        val token = input.peek()
        println("token $token")
        /*
        if (input.peek() == JsonToken.BEGIN_OBJECT) {
            input.beginObject()

            while (input.peek() == JsonToken.NAME) {

                println("name")
                val appListTxt = input.nextName()
                if (appListTxt == "applist") {
                    println("applist")

                    if (input.peek() == JsonToken.BEGIN_OBJECT) {
                        input.beginObject()

                        while (input.peek() == JsonToken.NAME) {

                            println("name")
                            val appTxt = input.nextName()
                            if (appTxt == "apps") {
                                println("apps")

                                if (input.peek() == JsonToken.BEGIN_ARRAY) {
                                    input.beginArray()

                                    while (input.peek() == JsonToken.BEGIN_OBJECT) {
                                        input.beginObject()

                                        while (input.peek() == JsonToken.NAME) {
                                            println("name")
                                            val nextName = input.nextName()
                                            if (nextName == "appid") {
                                                println("appid")
                                                searchGame.appId = input.nextLong()
                                                searchGameList.add(searchGame)
                                                //searchGameList.add(searchGame)
                                            } else {
                                                println("SKIP nextName : $nextName")
                                                input.skipValue()
                                            }
                                        }

                                        input.endObject()
                                    }
                                }
                            } else {
                                println("SKIP nextName : $appTxt")
                                input.skipValue()
                            }
                        }
                        input.endObject()

                    }

                } else {
                    println("SKIP nextName : $appListTxt")
                    input.skipValue()
                }


            }
            input.endObject()
            /* input.endArray()
         }*/

        }
        */
        println("return")
        //println(searchGameList)
        return searchGameList
    }

    override fun write(out: JsonWriter?, value: MutableList<SearchGame>) {
        TODO("Not yet implemented")

    }

}*/