package workshop.akbolatss.assets.weather.model

import workshop.akbolatss.assets.weather.model.prediction.PredictionItem
import workshop.akbolatss.assets.weather.model.weather.WeatherResponse

/**
 * Main Model
 */
data class WeatherModel(
        val weatherResponse: WeatherResponse? = null,
        val prediction: PredictionItem
)