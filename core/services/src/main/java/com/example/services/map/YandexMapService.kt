package com.example.services.map

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.FrameLayout
import com.example.services.BuildConfig
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YandexMapService @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) : MapService {
    private val office = Point(55.352016, 86.091037)
    private var mapView: MapView? = null

    override val isConfigured: Boolean
        get() = BuildConfig.YANDEX_MAPKIT_API_KEY.isNotBlank()

    override fun initialize() {
        if (!isConfigured) return
        MapKitFactory.setApiKey(BuildConfig.YANDEX_MAPKIT_API_KEY)
        MapKitFactory.initialize(applicationContext)
    }

    override fun createOfficeMapView(context: Context): View {
        if (!isConfigured) return FrameLayout(context)
        return MapView(context).also { view ->
            mapView = view
            view.mapWindow.map.mapObjects.addPlacemark(office)
            view.mapWindow.map.move(
                CameraPosition(office, 15f, 0f, 0f),
                Animation(Animation.Type.SMOOTH, 0f),
                null
            )
        }
    }

    override fun onStart() {
        if (!isConfigured) return
        MapKitFactory.getInstance().onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        if (!isConfigured) return
        mapView?.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun openRouteToOffice(context: Context): Boolean {
        val mapsIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("yandexmaps://maps.yandex.ru/?rtext=~${office.latitude},${office.longitude}&rtt=auto")
        ).setPackage(YANDEX_MAPS_PACKAGE)

        val navigatorIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("yandexnavi://build_route_on_map?lat_to=${office.latitude}&lon_to=${office.longitude}")
        ).setPackage(YANDEX_NAVIGATOR_PACKAGE)

        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://yandex.ru/maps/?rtext=~${office.latitude},${office.longitude}&rtt=auto")
        )

        return listOf(mapsIntent, navigatorIntent, browserIntent).any { intent ->
            runCatching {
                context.startActivity(intent)
                true
            }.getOrDefault(false)
        }
    }

    private companion object {
        const val YANDEX_MAPS_PACKAGE = "ru.yandex.yandexmaps"
        const val YANDEX_NAVIGATOR_PACKAGE = "ru.yandex.yandexnavi"
    }
}
