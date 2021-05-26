package com.github.airicbear.ethereumprice

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import com.github.airicbear.ethereumprice.model.EtherPriceResult
import com.github.airicbear.ethereumprice.network.EtherApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat

/**
 * Implementation of App Widget functionality.
 */
class EthereumPriceAppWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.ethereum_price_app_widget)
    widgetFetchEtherPrice(context, appWidgetManager, appWidgetId, views)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

internal fun widgetFetchEtherPrice(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, views: RemoteViews) {
    EtherApi.retrofitService.getEtherPrice(BuildConfig.ETHER_SCAN_API_KEY).enqueue(object :
        Callback<EtherPriceResult> {
        override fun onFailure(call: Call<EtherPriceResult>, t: Throwable) {
            views.setTextViewText(R.id.appwidget_text, context.getString(R.string.connection_error))
            appWidgetManager.updateAppWidget(appWidgetId, views)
            Log.d("EtherPrice", call.request().toString())
            Log.d("EtherPrice", t.toString())
        }

        override fun onResponse(call: Call<EtherPriceResult>, response: Response<EtherPriceResult>) {
            val etherResponse = response.body()
            val ethUsdValue = etherResponse?.result?.usdValue?.toDouble()
            val ethValue = NumberFormat.getCurrencyInstance().format(ethUsdValue)
            views.setTextViewText(R.id.appwidget_text, ethValue)
            appWidgetManager.updateAppWidget(appWidgetId, views)
            Log.d("EtherPrice", "ETH Value: $ethValue")
        }
    })
}