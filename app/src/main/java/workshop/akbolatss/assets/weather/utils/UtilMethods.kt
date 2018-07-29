package workshop.akbolatss.assets.weather.utils

import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager

/**
 * Author: Akbolat Sadvakassov
 * Date: 11.07.2018
 */
object UtilMethods {

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun isLandscapeOrientation(newConfig: Configuration): Boolean {
        return newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
}