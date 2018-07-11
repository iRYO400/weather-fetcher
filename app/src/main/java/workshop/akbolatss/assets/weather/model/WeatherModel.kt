package workshop.akbolatss.assets.weather.model

import workshop.akbolatss.assets.weather.model.prediction.PredictionItem


data class WeatherModel(
        val weatherResponse: WeatherResponse? = null,
        val prediction: PredictionItem
)