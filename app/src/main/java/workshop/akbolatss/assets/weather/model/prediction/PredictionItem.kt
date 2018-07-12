package workshop.akbolatss.assets.weather.model.prediction

import com.google.gson.annotations.SerializedName

/**
 * Response object for https://maps.googleapis.com/maps/api/place/autocomplete/
 */
data class PredictionItem(

	@field:SerializedName("reference")
	val reference: String? = null,

	@field:SerializedName("types")
	val types: List<String?>? = null,

	@field:SerializedName("matched_substrings")
	val matchedSubstrings: List<MatchedSubstringsItem?>? = null,

	@field:SerializedName("terms")
	val terms: List<TermsItem?>? = null,

	@field:SerializedName("structured_formatting")
	val structuredFormatting: StructuredFormatting? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("place_id")
	val placeId: String? = null
)