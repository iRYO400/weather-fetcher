package workshop.akbolatss.assets.weather.model

import io.reactivex.Observable
import io.rx_cache2.DynamicKey
import io.rx_cache2.EvictDynamicKey
import io.rx_cache2.LifeCache
import workshop.akbolatss.assets.weather.model.prediction.PredictionResponse
import java.util.concurrent.TimeUnit

interface CacheProvider {

    companion object {
        const val LIFESPAN = 1L
    }
    @LifeCache(duration = LIFESPAN, timeUnit = TimeUnit.HOURS)
    fun getCachedWeather(getZipWeatherModel: Observable<WeatherModel>, ket: DynamicKey, evictKey: EvictDynamicKey): Observable<WeatherModel>

    @LifeCache(duration = LIFESPAN, timeUnit = TimeUnit.HOURS)
    fun getCachedPredictions(getPredictionResponse: Observable<PredictionResponse>, ket: DynamicKey, evictKey: EvictDynamicKey): Observable<PredictionResponse>
}