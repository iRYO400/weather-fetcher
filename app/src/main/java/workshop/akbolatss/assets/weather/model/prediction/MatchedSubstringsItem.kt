package workshop.akbolatss.assets.weather.model.prediction

import com.google.gson.annotations.SerializedName

data class MatchedSubstringsItem(

	@field:SerializedName("offset")
	val offset: Int? = null,

	@field:SerializedName("length")
	val length: Int? = null
)