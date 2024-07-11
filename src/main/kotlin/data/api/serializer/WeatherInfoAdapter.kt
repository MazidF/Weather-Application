package org.example.data.api.serializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import org.example.data.model.WeatherCondition
import org.example.data.model.WeatherInfo

class WeatherInfoAdapter : JsonDeserializer<WeatherInfo> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): WeatherInfo? = json?.asJsonObject?.run {
       return WeatherInfo(
           location = this["location"].asJsonObject["name"].asString,
           requestTime = this["location"].asJsonObject["localtime"].asString,
           condition = with(this["current"].asJsonObject["condition"].asJsonObject) {
               WeatherCondition(
                   condition = this["text"].asString,
                   code = this["code"].asInt,
               )
           },
       )
    }
}