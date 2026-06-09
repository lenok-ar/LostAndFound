package com.example.services.map

import android.content.Context
import android.view.View

interface MapService {
    val isConfigured: Boolean
    fun initialize()
    fun createOfficeMapView(context: Context): View
    fun onStart()
    fun onStop()
    fun openRouteToOffice(context: Context): Boolean
}
