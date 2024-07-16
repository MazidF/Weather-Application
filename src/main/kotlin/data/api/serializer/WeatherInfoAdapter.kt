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
            location = this[LOCATION_OBJECT].asJsonObject[NAME_FIELD].asString,
            requestTime = this[LOCATION_OBJECT].asJsonObject[LOCALE_TIME_FIELD].asString,
            condition = with(this[CURRENT_OBJECT].asJsonObject[CONDITION_OBJECT].asJsonObject) {
                WeatherCondition(
                    text = this[TEXT_FIELD].asString,
                    code = this[CODE_FIELD].asInt,
                )
            },
        )
    }

    private companion object {
        const val LOCATION_OBJECT = "location"
        const val CURRENT_OBJECT = "current"
        const val CONDITION_OBJECT = "condition"
        const val NAME_FIELD = "name"
        const val TEXT_FIELD = "text"
        const val CODE_FIELD = "code"
        const val LOCALE_TIME_FIELD = "localtime"
    }
}