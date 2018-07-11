package workshop.akbolatss.assets.weather.model.prediction

import com.google.gson.annotations.SerializedName

data class PredictionResponse(

        @SerializedName("predictions")
        val predictions: List<PredictionItem>,

        @SerializedName("status")
        val status: String
)