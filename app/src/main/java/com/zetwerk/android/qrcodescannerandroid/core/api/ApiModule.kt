package com.zetwerk.android.qrcodescannerandroid.core.api

import com.zetwerk.android.qrcodescannerandroid.core.api.Endpoints.API_DRAWING_DETAILS
import com.zetwerk.android.qrcodescannerandroid.models.drawings.Drawing
import com.zetwerk.android.qrcodescannerandroid.models.drawings.DrawingResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiModule {
    @GET(API_DRAWING_DETAILS)
    abstract fun getDrawingDetails(
        @Path("id") id: String?
    ): Call<DrawingResponse>
}
