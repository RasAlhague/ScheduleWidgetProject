package com.example.schedulewidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class ScheduleWidget extends AppWidgetProvider
{
    private static final String ACTION_WIDGET_UPDATEBTN_CLICK = "WidgetUpdateButtonClick";
    private static final String ACTION_WIDGET_SETTINGBTN_CLICK = "WidgetSettingButtonClick";

    private static Context context;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        ScheduleWidget.context = context;

        // There may be multiple widgets active, so update all of them
        int N = appWidgetIds.length;
        for (int i = 0; i < N; i++)
        {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);

        final String action = intent.getAction();
        if (ACTION_WIDGET_UPDATEBTN_CLICK.equals(action))
        {
            OnUpdateButtonClick();
        }

        if (ACTION_WIDGET_SETTINGBTN_CLICK.equals(action))
        {
            // TODO
        }
    }

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId)
    {
        // setOnUpdateButtonClickEvent(context);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.schedule_widget);

        // set click event
        views.setOnClickPendingIntent(R.id.button_Update, getPendingSelfIntent(context, ACTION_WIDGET_UPDATEBTN_CLICK));
        views.setOnClickPendingIntent(R.id.button_Setting, getPendingSelfIntent(context, ACTION_WIDGET_SETTINGBTN_CLICK));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void OnUpdateButtonClick()
    {
        HTMLReceiver htmlReceiver = new HTMLReceiver();
        htmlReceiver.getWebPageAsync.execute();

//        ScheduleDisplayManager scheduleDisplayManager = new ScheduleDisplayManager();
//        scheduleDisplayManager.DisplaySchedule();
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action)
    {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public static Context getAppContext()
    {
        return ScheduleWidget.context;
    }
}
