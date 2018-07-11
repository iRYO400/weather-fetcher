package workshop.akbolatss.assets.weather

import android.support.v7.util.DiffUtil
import workshop.akbolatss.assets.weather.model.WeatherModel
import workshop.akbolatss.assets.weather.utils.Logger

/**
 * Author: Akbolat Sadvakassov
 * Date: 10.07.2018
 */
class WeatherDiffUtilCallback(private val oldList: List<WeatherModel> = ArrayList(),
                              private val newList: List<WeatherModel> = ArrayList()) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldWeatherModel = oldList[oldItemPosition]
        val newWeatherModel = newList[newItemPosition]
        return oldWeatherModel.prediction.id == newWeatherModel.prediction.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldWeatherModel = oldList[oldItemPosition]
        val newWeatherModel = newList[newItemPosition]
        return oldWeatherModel.weatherResponse?.main?.temp == newWeatherModel.weatherResponse?.main?.temp
    }
}