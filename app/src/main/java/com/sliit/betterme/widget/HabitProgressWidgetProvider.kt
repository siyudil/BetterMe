package com.sliit.betterme.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.sliit.betterme.R
import com.sliit.betterme.data.Prefs
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HabitProgressWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        private fun todayKey(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        fun updateAllWidgets(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, HabitProgressWidgetProvider::class.java))
            for (id in ids) updateAppWidget(context, manager, id)
        }

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_habits)
            val prefs = Prefs(context)
            val arr: JSONArray = prefs.getJsonArray(Prefs.HABITS)
            var total = 0
            var done = 0
            val key = todayKey()
            for (i in 0 until arr.length()) {
                val obj: JSONObject = arr.optJSONObject(i) ?: continue
                total++
                val doneArr = obj.optJSONArray("doneDates") ?: JSONArray()
                var has = false
                for (j in 0 until doneArr.length()) if (doneArr.optString(j) == key) { has = true; break }
                if (has) done++
            }
            val pct = if (total == 0) 0 else done * 100 / total
            views.setTextViewText(R.id.tvWidgetText, "Today's habits: $done/$total ($pct%)")
            views.setProgressBar(R.id.widgetProgress, 100, pct, false)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
