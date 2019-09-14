package com.zetwerk.android.qrcodescannerandroid.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri

/**
 * Created by Ashutosh on 04-03-2017.
 */
object NetworkUtils {

    fun getUrl(baseUrl: String, queryParams: Map<String, String>?): String {
        val urlBuilder = Uri.parse(baseUrl).buildUpon()
        if (queryParams != null) {
            for (paramName in queryParams.keys) {
                urlBuilder.appendQueryParameter(paramName, queryParams[paramName])
            }
        }

        return urlBuilder.build().toString()
    }

    fun isNetworkConnected(context: Context): Boolean {
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }


}
