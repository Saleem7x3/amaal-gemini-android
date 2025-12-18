package com.example.amaal.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;
import com.example.amaal.R;
import com.example.amaal.db.AppDatabase;
import com.example.amaal.model.Deed;
import java.util.Collections;
import java.util.List;

public class FocusWidget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        AppDatabase db = AppDatabase.getDatabase(context);
        List<Deed> deeds = db.deedDao().getIncompleteDeeds();
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.activity_main); // Simplified for placeholder
        
        if (!deeds.isEmpty()) {
            Collections.sort(deeds);
            Deed focus = deeds.get(0);
            // In a real widget, we'd use a specific widget layout
        }
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}