package workshop.akbolatss.assets.weather.screens.main

import workshop.akbolatss.assets.weather.model.WeatherModel

interface MainContract {

    interface View {

        fun onErrorPrediction()

        fun onErrorWeather()

        fun onLoading(isDone: Boolean)

        fun onLoadItems(result: List<WeatherModel>)

        fun onUpdateItems(result: List<WeatherModel>)
    }

    interface Presenter {
        fun fetchPredictions(query: String, isNetworkAvailable: Boolean)

        fun fetchWeather(predictions: List<WeatherModel>, isNetworkAvailable: Boolean)

        fun attachView(view: View)

        fun detachView()
    }
}