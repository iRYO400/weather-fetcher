package workshop.akbolatss.assets.weather

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main.*
import workshop.akbolatss.assets.weather.utils.Logger
import workshop.akbolatss.assets.weather.application.MainApplication
import workshop.akbolatss.assets.weather.application.MainApplication.Companion.WEATHER_APP_KEY
import workshop.akbolatss.assets.weather.application.MainApplication.Companion.mApiInterface
import workshop.akbolatss.assets.weather.model.Main
import workshop.akbolatss.assets.weather.model.WeatherModel
import workshop.akbolatss.assets.weather.model.WeatherResponse
import workshop.akbolatss.assets.weather.model.prediction.PredictionItem
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), WeatherAdapter.OnWeatherItemListener {

    private var mSearchSubject: BehaviorSubject<String>? = null

    private var mCompositeDisposable: CompositeDisposable? = null

    private lateinit var mAdapter: WeatherAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mCompositeDisposable = CompositeDisposable()

        initRv()
        initEditText()
    }

    private fun initRv() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = WeatherAdapter(this)
        recyclerView.adapter = mAdapter
    }

    private fun initEditText() {
        mSearchSubject = BehaviorSubject.create()

        etQuery.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, event ->
            if (actionId != 0 || event.action == KeyEvent.ACTION_DOWN) {
//                onSubmitQuery()
                val view = findViewById<View>(android.R.id.content)
                if (view != null) {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
                return@OnEditorActionListener true
            }
            false
        })

        etQuery.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().length > 2) {
                    mSearchSubject?.onNext(s.toString())
                } else {
                    mAdapter.onClearItems()
                }
            }
        })

        //Query for new result after typing within 1 second
        etQuery.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                mSearchSubject!!.debounce(300, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { s ->
                            fetchPredictions(s)
                        }
            }
        }
    }

    override fun onDestroy() {
        mCompositeDisposable?.clear()
        super.onDestroy()
    }

    fun onLoading(isDone: Boolean) {
        if (isDone) {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
    }

    private fun fetchPredictions(query: String) {
        onLoading(false)
        mCompositeDisposable?.add(
                mApiInterface.getPredictions("https://maps.googleapis.com/maps/api/place/autocomplete/json?input=$query&types=(cities)&language=ru_RU&key=${MainApplication.GOOGLE_APP_KEY}")
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
                                    onLoading(true)
                                    mAdapter.onAddItems(result)
                                }, { error ->
                            onLoading(true)
                        })
        )
    }

    override fun onWeatherListUpdateListener(predictions: List<WeatherModel>) {
        mCompositeDisposable?.add(
                Observable.fromIterable(predictions)
                        .flatMap { weatherItem ->
                            Observable.zip(
                                    Observable.just(weatherItem.prediction),
                                    mApiInterface.getWeather(weatherItem.prediction.terms!![0]!!.value!!, WEATHER_APP_KEY)
                                            .onErrorResumeNext { _: Throwable ->
                                                Observable.just(WeatherResponse(404, Main()))
                                            },
                                    BiFunction<PredictionItem, WeatherResponse, WeatherModel> { t1, t2 ->
                                        WeatherModel(t2, t1)
                                    })
                        }
                        .toList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ result ->
                            mAdapter.onUpdateItems(result)
                        }, { error ->
                            Logger.e("Error: $error")
                        }))
    }
}
