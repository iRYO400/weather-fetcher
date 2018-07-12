package workshop.akbolatss.assets.weather.network

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import workshop.akbolatss.assets.weather.model.weather.WeatherResponse
import workshop.akbolatss.assets.weather.model.prediction.PredictionResponse

/**
 * Author: Akbolat Sadvakassov
 * Date: 10.07.2018
 */
interface ApiInterface {
    @GET
    fun getPredictions(@Url url: String): Observable<PredictionResponse>

    @GET("weather")
    fun getWeather(@Query("q") query: String, @Query("APPID") appKey: String): Observable<WeatherResponse>
}