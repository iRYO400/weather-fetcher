package workshop.akbolatss.assets.weather.screens.main

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
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main.*
import workshop.akbolatss.assets.weather.R
import workshop.akbolatss.assets.weather.application.MainApplication.Companion.PREFS_LAST_QUERY
import workshop.akbolatss.assets.weather.model.WeatherModel
import workshop.akbolatss.assets.weather.utils.PreferenceHelper
import workshop.akbolatss.assets.weather.utils.UtilMethods.isNetworkAvailable
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), WeatherAdapter.OnWeatherItemListener, MainContract.View {

    private var mPresenter: MainContract.Presenter? = null

    private var mSearchSubject: BehaviorSubject<String>? = null

    private lateinit var mAdapter: WeatherAdapter
    /**
     * Checker for Cold-Start
     */
    private var isColdStart = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        attachPresenter()
        initRv()
        initEditText()
    }

    private fun attachPresenter() {
        if (lastNonConfigurationInstance != null) {
            mPresenter = lastCustomNonConfigurationInstance as MainContract.Presenter
        } else {
            mPresenter = MainPresenter()
            isColdStart = true
        }
        mPresenter!!.attachView(this)
    }

    override fun onRetainCustomNonConfigurationInstance(): Any {
        return mPresenter!!
    }

    /**
     * Initializing RecyclerView
     */
    private fun initRv() {
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = WeatherAdapter(this)
        recyclerView.adapter = mAdapter
    }

    /**
     * Initializing EditText
     */
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
                if (s.toString().trim().isNotEmpty()) {
                    mSearchSubject?.onNext(s.toString())
                }
            }
        })

        //Query for new result after typing within 300 millis
        etQuery.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val prefsHelper = PreferenceHelper.defaultPrefs(this)
                mSearchSubject!!.debounce(300, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext { s ->
                            prefsHelper.edit().putString(PREFS_LAST_QUERY, s).apply()
                        }
                        .subscribe { s ->
                            if (s.length > 2) {
                                mPresenter!!.fetchPredictions(s, isNetworkAvailable(this))
                            } else {
                                mAdapter.onClearItems()
                            }
                        }
            }
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        if (isColdStart) {
            val prefsHelper = PreferenceHelper.defaultPrefs(this)
            if (prefsHelper.contains(PREFS_LAST_QUERY)) {
                val query = prefsHelper.getString(PREFS_LAST_QUERY, "")
                etQuery.setText(query)
                mPresenter!!.fetchPredictions(query, false)//False = Triggers to load from Cache
            }
        }
    }

    override fun onDestroy() {
        mPresenter?.detachView()
        super.onDestroy()
    }

    /**
     * Loading State manager
     */
    override fun onLoading(isDone: Boolean) {
        if (isDone) {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
    }

    /**
     * Error on fetching autocomplete prediction
     */
    override fun onErrorPrediction() {
        Toast.makeText(this, getString(R.string.error_api_prediction), Toast.LENGTH_SHORT).show()
    }

    /**
     * Error on fetching Weather info
     */
    override fun onErrorWeather() {
        Toast.makeText(this, getString(R.string.error_api_weather), Toast.LENGTH_SHORT).show()
    }

    override fun onLoadItems(result: List<WeatherModel>) {
        mAdapter.onAddItems(result)
    }

    override fun onUpdateItems(result: List<WeatherModel>) {
        mAdapter.onUpdateItems(result)
    }
    override fun onWeatherListUpdateListener(predictions: List<WeatherModel>) {
        mPresenter!!.fetchWeather(predictions, isNetworkAvailable(this))
    }
}
