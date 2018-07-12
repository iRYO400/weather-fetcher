package workshop.akbolatss.assets.weather.screens.main

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.rx_cache2.DynamicKey
import io.rx_cache2.EvictDynamicKey
import workshop.akbolatss.assets.weather.application.MainApplication.Companion.GOOGLE_APP_KEY
import workshop.akbolatss.assets.weather.application.MainApplication.Companion.WEATHER_APP_KEY
import workshop.akbolatss.assets.weather.application.MainApplication.Companion.mApiInterface
import workshop.akbolatss.assets.weather.application.MainApplication.Companion.mCacheProvider
import workshop.akbolatss.assets.weather.model.weather.Main
import workshop.akbolatss.assets.weather.model.WeatherModel
import workshop.akbolatss.assets.weather.model.weather.WeatherResponse
import workshop.akbolatss.assets.weather.model.prediction.PredictionItem
import workshop.akbolatss.assets.weather.utils.ApiError


class MainPresenter : MainContract.Presenter {

    private var mView: MainContract.View? = null

    private var mCompositeDisposable: CompositeDisposable? = null

    override fun fetchPredictions(query: String, isNetworkAvailable: Boolean) {
        mView?.onLoading(false)
        mCompositeDisposable?.add(
                mCacheProvider.getCachedPredictions(
                        mApiInterface.getPredictions("https://maps.googleapis.com/maps/api/place/autocomplete/json?input=$query&types=(cities)&language=ru_RU&key=$GOOGLE_APP_KEY"),
                        DynamicKey(query),
                        EvictDynamicKey(isNetworkAvailable))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMapIterable {
                            it.predictions
                        }
                        .map { item ->
                            WeatherModel(null, item)
                        }
                        .toList()
                        .subscribe(
                                { result ->
                                    mView?.onLoading(true)
                                    mView?.onLoadItems(result)
                                }, { error ->
                            mView?.onLoading(true)
                            mView?.onErrorPrediction()
                        })
        )
    }

    override fun fetchWeather(predictions: List<WeatherModel>, isNetworkAvailable: Boolean) {
        mCompositeDisposable?.add(
                Observable.fromIterable(predictions)
                        .flatMap { weatherItem ->
                            mCacheProvider.getCachedWeather(Observable.zip(
                                    Observable.just(weatherItem.prediction),
                                    mApiInterface.getWeather(weatherItem.prediction.terms!![0]!!.value!!, WEATHER_APP_KEY)
                                            .onErrorResumeNext { t: Throwable ->
                                                val statusCode = ApiError(t).statusCode
                                                Observable.just(WeatherResponse(statusCode, Main()))
                                            },
                                    BiFunction<PredictionItem, WeatherResponse, WeatherModel> { t1, t2 ->
                                        WeatherModel(t2, t1)
                                    }), DynamicKey(weatherItem.prediction.id), EvictDynamicKey(isNetworkAvailable))
                        }
                        .toList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ result ->
                            mView?.onUpdateItems(result)
                        }, { error ->
                            mView?.onErrorWeather()
                        }))
    }

    override fun attachView(view: MainContract.View) {
        mView = view

        mCompositeDisposable = CompositeDisposable()
    }

    override fun detachView() {
        mCompositeDisposable?.clear()
        this.mView = null
    }
}