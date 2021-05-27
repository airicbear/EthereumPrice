package com.github.airicbear.ethereumprice

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
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

    override fun onReceive(context: Context, intent: Intent) {
        val views = RemoteViews(context.packageName, R.layout.ethereum_price_app_widget)
        views.setTextViewText(R.id.appwidget_text, context.getString(R.string.appwidget_text))
        widgetFetchEtherPrice(context, views)

        // Useful log to track intents for the App Widget
        Log.d("EtherPrice", "App Widget received intent ${intent.action}")

        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {

            // Setup on click
            //
            // For some reason, apparently onReceive() is the only function being called?
            // Which is why we put this here instead of in, onUpdate(), for example
            val intentUpdate = Intent(context, EthereumPriceAppWidget::class.java)
            intentUpdate.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val pendingUpdate = PendingIntent.getBroadcast(context, 0, intentUpdate, PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.appwidget_text, pendingUpdate)
        }
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.ethereum_price_app_widget)

    // Update views
    widgetFetchEtherPrice(context, views)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

internal fun widgetFetchEtherPrice(context: Context, views: RemoteViews) {
    EtherApi.retrofitService.getEtherPrice(BuildConfig.ETHER_SCAN_API_KEY).enqueue(object :
        Callback<EtherPriceResult> {
        override fun onFailure(call: Call<EtherPriceResult>, t: Throwable) {
            views.setTextViewText(R.id.appwidget_text, context.getString(R.string.connection_error))
            AppWidgetManager.getInstance(context).updateAppWidget(ComponentName(context, EthereumPriceAppWidget::class.java), views)
            Log.d("EtherPrice", call.request().toString())
            Log.d("EtherPrice", t.toString())
        }

        override fun onResponse(call: Call<EtherPriceResult>, response: Response<EtherPriceResult>) {
            val etherResponse = response.body()
            val ethUsdValue = etherResponse?.result?.usdValue?.toDouble()
            val ethValue = NumberFormat.getCurrencyInstance().format(ethUsdValue)
            views.setTextViewText(R.id.appwidget_text, ethValue)
            AppWidgetManager.getInstance(context).updateAppWidget(ComponentName(context, EthereumPriceAppWidget::class.java), views)
            Log.d("EtherPrice", "ETH Value: $ethValue")
        }
    })
}