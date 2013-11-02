package com.example.schedulewidget;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class ScheduleWidget extends AppWidgetProvider
{
    private static final String ACTION_WIDGET_UPDATEBTN_CLICK = "WidgetUpdateButtonClick";
    private static final String ACTION_WIDGET_SETTINGBTN_CLICK = "WidgetSettingButtonClick";
    private static final String ACTION_WIDGET_ALLSCHEDULEGBTN_CLICK = "WidgetAllScheduleButtonClick";

    public static Context context;

    public static Context getAppContext()
    {
        return ScheduleWidget.context;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);

        final String action = intent.getAction();
        if ( ACTION_WIDGET_UPDATEBTN_CLICK.equals(action) )
        {
            OnUpdateButtonClick();
        }

        if ( ACTION_WIDGET_SETTINGBTN_CLICK.equals(action) )
        {
            Intent settingActivityIntent = new Intent(ScheduleWidget.context, SettingActivity.class);
            PendingIntent configPendingIntent = PendingIntent.getActivity(ScheduleWidget.context, 0, settingActivityIntent, 0);
            try
            {
                configPendingIntent.send();
            }
            catch (CanceledException e)
            {
                e.printStackTrace();
            }
        }

        if ( ACTION_WIDGET_ALLSCHEDULEGBTN_CLICK.equals(action) )
        {
            Intent allScheduleActivityIntent = new Intent(ScheduleWidget.context, WholeScheduleActivity.class);
            PendingIntent allSchedulePendingIntent = PendingIntent.getActivity(ScheduleWidget.context, 0, allScheduleActivityIntent, 0);
            try
            {
                allSchedulePendingIntent.send();
            }
            catch (CanceledException e)
            {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        ScheduleWidget.context = context;

        // There may be multiple widgets active, so update all of them
        int N = appWidgetIds.length;
        for ( int i = 0; i < N; i++ )
        {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId)
    {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.schedule_widget);

        // set click event
        views.setOnClickPendingIntent(R.id.button_Update, getPendingSelfIntent(context, ACTION_WIDGET_UPDATEBTN_CLICK));
        views.setOnClickPendingIntent(R.id.button_Setting, getPendingSelfIntent(context, ACTION_WIDGET_SETTINGBTN_CLICK));
        views.setOnClickPendingIntent(R.id.button_all_schedule, getPendingSelfIntent(context, ACTION_WIDGET_ALLSCHEDULEGBTN_CLICK));

        OnUpdateButtonClick();

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void OnUpdateButtonClick()
    {
        // final Dialog dialog = new Dialog(ScheduleWidget.context);
        // dialog.setContentView(R.layout.calendarview);
        // dialog.setTitle("Title...");
        //
        // dialog.show();

        // // trololo
        // Integer a = 127;
        // Integer b = 127;
        // Integer c = 128;
        // Integer d = 128;
        // boolean bAB = a == b;
        // boolean bCD = c == d;
        // System.out.println(a);
        // System.out.println(b);
        // System.out.println(bAB); // true
        // System.out.println(bCD); // false
        // System.out.println(a == b); // true
        // System.out.println(c == d); // false
        //
        // int x=0;
        // System.out.println(++x==++x);
        // int xx=0;
        // System.out.println(++xx==xx);
        // int xxx=0;
        // System.out.println(xxx==xxx++);
        // int xxxx=0;
        // System.out.println(xxxx++==xxxx++);
        // int xxxxx=0;
        // System.out.println(xxxxx++==++xxxxx);
        //
        // // trololo end ---
        //

        Utility.SetTextToTextView(R.id.textView_Schedule, "");
        Utility.TougleProgressBar(true);

        ScheduleDisplayManager scheduleDisplayManager = new ScheduleDisplayManager();

        // Toast
        Utility.Toasts.ShowToastMsg(R.string.xml_file_available_checking);

        if ( Utility.FileExistance(GlobalVariables.SCHEDULE_FILE_NAME) && scheduleDisplayManager.CheckXMLFileActuality() )
        {
            Utility.Toasts.ShowToastMsg(R.string.xml_available_and_actual_showing);

            scheduleDisplayManager.DisplaySchedule();
        }
        else
        {
            // Toast
            Utility.Toasts.ShowToastMsg(R.string.xml_not_found_localy);

            if ( Utility.IsWifiConnected() /* || IsMobileConnected() */)
            {
                if( Utility.CheckUserLoginPassExistence() ) 
                {
                    Receiver htmlReceiver = new Receiver();
                    htmlReceiver.StartExecSequence();
                }
                else
                {
                    // Toast
                    Utility.Toasts.ShowToastMsg(R.string.check_user_data_exist);
                    Utility.SetTextToTextView(R.id.textView_Schedule, getAppContext().getString(R.string.check_user_data_exist));
                    Utility.TougleProgressBar(false);
                }
                
            }
            else
            {
                // Toast
                Utility.Toasts.ShowToastMsg(R.string.check_connection);
                Utility.SetTextToTextView(R.id.textView_Schedule, getAppContext().getString(R.string.check_connection));
                Utility.TougleProgressBar(false);
            }
        }
        
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action)
    {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
