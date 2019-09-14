package com.zetwerk.android.qrcodescannerandroid.core.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder

import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Ashutosh on 03-03-2017.
 */

object ApiServiceGenerator {

    private val gsonBuilder = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
    private val gson = gsonBuilder.create()
    private val gsonConverterFactory = GsonConverterFactory.create(gson)

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val builder = Retrofit.Builder()
        .baseUrl(Endpoints.API_BASE_URL)
        .addConverterFactory(gsonConverterFactory)

    fun <S> createService(serviceClass: Class<S>): S {
        val retrofit = builder.client(httpClient).build()
        return retrofit.create(serviceClass)
    }
}