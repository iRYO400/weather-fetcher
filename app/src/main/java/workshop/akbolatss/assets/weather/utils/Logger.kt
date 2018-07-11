package workshop.akbolatss.assets.weather.utils

import android.util.Log
import workshop.akbolatss.assets.weather.BuildConfig
import workshop.akbolatss.assets.weather.application.MainApplication.Companion.LOG_TAG

/**
 * Logcat helper. Used only in debug mode
 */
class Logger {
    companion object {
        fun i(name: String, msg: String) {
            if (BuildConfig.DEBUG) {
                Log.d(name, msg)
            }
        }

        fun i(s: String) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, s)
            }
        }

        fun e(name: String, msg: String) {
            if (BuildConfig.DEBUG) {
                Log.e(name, msg)
            }
        }

        fun e(s: String) {
            if (BuildConfig.DEBUG) {
                Log.e(LOG_TAG, s)
            }
        }
    }
}