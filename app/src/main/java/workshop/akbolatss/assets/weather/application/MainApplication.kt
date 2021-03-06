package workshop.akbolatss.assets.weather.application

import android.app.Application
import io.rx_cache2.internal.RxCache
import io.victoralbertos.jolyglot.GsonSpeaker
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import workshop.akbolatss.assets.weather.BuildConfig
import workshop.akbolatss.assets.weather.model.CacheProvider
import workshop.akbolatss.assets.weather.network.ApiInterface
import java.util.concurrent.TimeUnit

class MainApplication : Application() {

    companion object {
        const val WEATHER_URL = "http://api.openweathermap.org/data/2.5/"
        const val WEATHER_APP_KEY = "e6fbcad26d2bb10b76f1226374c86e5f"
        const val GOOGLE_APP_KEY = "AIzaSyAhcRK2eK7JbNY1EyG9dJrzz5ebOVe_1lI"

        const val PREFS_LAST_QUERY = "SharedLastQuery"

        lateinit var mApiInterface: ApiInterface
        lateinit var mCacheProvider: CacheProvider
    }

    private lateinit var mRetrofit: Retrofit

    override fun onCreate() {
        super.onCreate()

        val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(getLoggingInterceptor())
                .build()


        mRetrofit = Retrofit.Builder()
                .baseUrl(WEATHER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()

        mApiInterface = mRetrofit.create(ApiInterface::class.java)

        mCacheProvider = RxCache.Builder()
                .setMaxMBPersistenceCache(10)
                .persistence(this.cacheDir, GsonSpeaker())
                .using(CacheProvider::class.java)
    }


    private fun getLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        return if (BuildConfig.DEBUG) {
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            loggingInterceptor
        } else {
            loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
            loggingInterceptor
        }
    }
}
