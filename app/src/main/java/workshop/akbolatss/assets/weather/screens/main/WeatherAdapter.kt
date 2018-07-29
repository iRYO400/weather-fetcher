package workshop.akbolatss.assets.weather.screens.main

import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rv_item.view.*
import workshop.akbolatss.assets.weather.R
import workshop.akbolatss.assets.weather.model.WeatherModel
import workshop.akbolatss.assets.weather.screens.main.helper.WeatherDiffUtilCallback
import java.util.*
import kotlin.math.roundToInt

class WeatherAdapter(private val mListener: OnWeatherItemListener) : RecyclerView.Adapter<WeatherAdapter.PredictionViewHolder>() {

    private val mModelList: MutableList<WeatherModel> = ArrayList()

    private var mLandscape: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredictionViewHolder {
        val mLayoutInflater = LayoutInflater.from(parent.context)
        val view =
                if (viewType == 0) mLayoutInflater.inflate(R.layout.rv_item, parent, false)
                else mLayoutInflater.inflate(R.layout.rv_item_square, parent, false)
        return PredictionViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: PredictionViewHolder, position: Int) {
        val predictionsItem = mModelList[position]
        holder.bind(predictionsItem)
    }

    override fun getItemCount(): Int {
        return mModelList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (!mLandscape) {
            0
        } else {
            1
        }
    }

    /**
     * Adding items
     */
    fun onAddItems(predictions: List<WeatherModel>) {
        mModelList.clear()
        mModelList.addAll(predictions)
        notifyDataSetChanged()
        mListener.onWeatherListUpdateListener(predictions)//Запросы для получения текущей температуры по загруженным городам
    }

    /**
     * Updating items using DiffUtil
     * @see WeatherDiffUtilCallback
     */
    fun onUpdateItems(predictions: List<WeatherModel>) {
        val diffResult = DiffUtil.calculateDiff(WeatherDiffUtilCallback(mModelList, predictions))
        mModelList.clear()
        mModelList.addAll(predictions)
        diffResult.dispatchUpdatesTo(this)
    }

    /**
     * Clear all items in adapter
     */
    fun onClearItems() {
        mModelList.clear()
        notifyDataSetChanged()
    }

    fun onRefreshView(landscape: Boolean) {
        mLandscape = landscape
        notifyDataSetChanged()
    }

    interface OnWeatherItemListener {
        fun onWeatherListUpdateListener(predictions: List<WeatherModel>)
    }

    inner class PredictionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(weatherModel: WeatherModel) {
            itemView.tvCityName.text = weatherModel.prediction.terms!![0]!!.value
            if (weatherModel.weatherResponse == null) {
                itemView.progressBar.visibility = View.VISIBLE
                itemView.tvValue.visibility = View.INVISIBLE
            } else if (weatherModel.weatherResponse.cod == 403) { // Скорее всего превышено количество запросов в минуту( free plan)
                itemView.progressBar.visibility = View.GONE
                itemView.tvValue.text = "-°"
            } else if (weatherModel.weatherResponse.cod == 404) { // Не существует
                itemView.progressBar.visibility = View.GONE
                itemView.tvValue.text = "-°"
            } else if (weatherModel.weatherResponse.cod == 500) { // Не работает
                itemView.progressBar.visibility = View.GONE
                itemView.tvValue.text = "-°"
            } else {
                itemView.progressBar.visibility = View.GONE
                itemView.tvCountryName.text = weatherModel.weatherResponse.weather!![0]!!.main
                itemView.tvValue.visibility = View.VISIBLE
                itemView.tvValue.text = "${weatherModel.weatherResponse.main?.temp!!.minus(273).roundToInt()}°"

                val iconUrl = "http://openweathermap.org/img/w/${weatherModel.weatherResponse.weather[0]!!.icon}.png"
                Picasso.get()
                        .load(iconUrl)
                        .into(itemView.imgIcon)

                val type = weatherModel.weatherResponse.weather[0]!!.icon
                changeBgState(type!!)
            }
        }

        private fun changeBgState(type: String) {
            if (type == "01d" || type == "01n") {
                itemView.clBackground.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_gradient_clear_sky)
            } else if (type == "02d" || type == "02n") {
                itemView.clBackground.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_gradient_few_clouds)
            } else if (type == "03d" || type == "03n") {
                itemView.clBackground.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_gradient_scattered_clouds)
            } else if (type == "04d" || type == "04n") {
                itemView.clBackground.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_gradient_broken_clouds)
            } else if (type == "09d" || type == "09n") {
                itemView.clBackground.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_gradient_shower_rain)
            } else if (type == "10d" || type == "10n") {
                itemView.clBackground.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_gradient_rain)
            } else if (type == "11d" || type == "11n") {
                itemView.clBackground.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_gradient_thunder)
            } else if (type == "13d" || type == "13n") {
                itemView.clBackground.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_gradient_snow)
            } else if (type == "50d" || type == "50n") {
                itemView.clBackground.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_gradient_mist)
            } else {
                itemView.clBackground.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_gradient_empty)
            }
        }
    }
}
