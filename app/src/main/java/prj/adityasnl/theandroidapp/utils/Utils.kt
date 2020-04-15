package prj.adityasnl.theandroidapp.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.DisplayMetrics
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange

class Utils {
    companion object {
        fun isNetworkAvailable(context: Context): Boolean? {
            var isConnected: Boolean? = false // Initial Value
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            if (activeNetwork != null && activeNetwork.isConnected)
                isConnected = true
            return isConnected
        }

        @CheckResult
        @ColorInt
        fun modifyAlpha(@ColorInt color: Int, @androidx.annotation.IntRange(from = 0, to = 255) alpha: Int): Int {
            return color and 0x00ffffff or (alpha shl 24)
        }

        /**
         * Set the alpha component of `color` to be `alpha`.
         */
        @CheckResult
        @ColorInt
        fun modifyAlpha(@ColorInt color: Int, @FloatRange(from = 0.0, to = 1.0) alpha: Float): Int {
            return modifyAlpha(color, (255f * alpha).toInt())
        }

        fun isNavBarOnBottom(context: Context): Boolean {
            val res: Resources = context.resources
            val cfg: Configuration = context.resources.configuration
            val dm: DisplayMetrics = res.displayMetrics
            val canMove = dm.widthPixels != dm.heightPixels &&
                    cfg.smallestScreenWidthDp < 600
            return !canMove || dm.widthPixels < dm.heightPixels
        }
    }
}