package workshop.akbolatss.assets.weather

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.rv_item.view.*
import workshop.akbolatss.assets.weather.model.WeatherModel
import workshop.akbolatss.assets.weather.model.prediction.PredictionItem
import workshop.akbolatss.assets.weather.utils.Logger
import java.util.*

/**
 * Custom RecyclerView.Adapter for Weather items
 */
class WeatherAdapter(private val mListener: OnWeatherItemListener) : RecyclerView.Adapter<WeatherAdapter.PredictionViewHolder>() {

    private val mModelList: MutableList<WeatherModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredictionViewHolder {
        val mLayoutInflater = LayoutInflater.from(parent.context)
        val view = mLayoutInflater.inflate(R.layout.rv_item, parent, false)
        return PredictionViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: PredictionViewHolder, position: Int) {
        val predictionsItem = mModelList[position]
        holder.bind(predictionsItem)
    }

    override fun getItemCount(): Int {
        return mModelList.size
    }

    fun onAddItems(predictions: List<WeatherModel>) {
        mModelList.clear()
        mModelList.addAll(predictions)
        notifyDataSetChanged()
        mListener.onWeatherListUpdateListener(predictions)
    }

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

    interface OnWeatherItemListener {
        fun onWeatherListUpdateListener(predictions: List<WeatherModel>)
    }

    /**
     * Custom View Holder where View inits
     */
    inner class PredictionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(weatherModel: WeatherModel) {
            itemView.tvCityName.text = weatherModel.prediction.description
            itemView.tvCountryName.text = weatherModel.prediction.id
            if (weatherModel.weatherResponse == null) {
                itemView.progressBar.visibility = View.VISIBLE
                itemView.tvValue.visibility = View.GONE
                Log.d("TAG", "weatherResponse is null")
            } else if (weatherModel.weatherResponse.cod == 404) {
                itemView.progressBar.visibility = View.GONE
                itemView.tvValue.text = "-"
            } else {
                Log.d("TAG", "weatherResponse is not null")
                itemView.progressBar.visibility = View.GONE
                itemView.tvValue.visibility = View.VISIBLE
                itemView.tvDescription.text = weatherModel.prediction.id
                itemView.tvValue.text = "${weatherModel.weatherResponse.main?.temp}°C"
            }
        }
    }
}
