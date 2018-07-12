package workshop.akbolatss.assets.weather.model.weather

data class WeatherResponse(
        val dt: Int? = null,
        val coord: Coord? = null,
        val visibility: Int? = null,
        val weather: List<WeatherItem?>? = null,
        val name: String? = null,
        val cod: Int? = null,
        val main: Main? = null,
        val clouds: Clouds? = null,
        val id: Int? = null,
        val sys: Sys? = null,
        val base: String? = null,
        val wind: Wind? = null
) {
    constructor(cod: Int, main: Main) : this(
            0,
            null,
            0,
            null,
            "",
            cod,
            main,
            null,
            0,
            null,
            "",
            null
    )
}
