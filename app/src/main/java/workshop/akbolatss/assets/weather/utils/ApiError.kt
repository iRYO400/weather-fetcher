package workshop.akbolatss.assets.weather.utils

import com.google.gson.JsonParser
import retrofit2.HttpException

/**
 * Проверка статуса из ошибки {@link https://openweathermap.org}
 */
class ApiError constructor(error: Throwable) {

    var statusCode = 102

    init {
        if (error is HttpException) {
            val errorJsonString = error.response().errorBody()?.string()
            this.statusCode = JsonParser().parse(errorJsonString)
                    .asJsonObject["cod"]
                    .asInt
        } else {
            this.statusCode = 500
        }
    }
}