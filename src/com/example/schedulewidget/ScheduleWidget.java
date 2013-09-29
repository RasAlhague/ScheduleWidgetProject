package com.example.schedulewidget;

import java.io.File;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class ScheduleWidget extends AppWidgetProvider
{
    private static final String ACTION_WIDGET_UPDATEBTN_CLICK = "WidgetUpdateButtonClick";
    private static final String ACTION_WIDGET_SETTINGBTN_CLICK = "WidgetSettingButtonClick";

    public static Context context;

    public static Context getAppContext()
    {
        return ScheduleWidget.context;
    }

    public boolean FileExistance(String fname)
    {
        File file = ScheduleWidget.context.getFileStreamPath(fname);
        if ( file.exists() )
        {
            return true;
        }
        else
        {
            return false;
        }
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
            Intent settingActivityIntent = new Intent(context, SettingActivity.class);
            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, settingActivityIntent, 0);
            try
            {
                configPendingIntent.send();
            }
            catch (CanceledException e)
            {
                // TODO Auto-generated catch block
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

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private boolean IsMobileConnected()
    {
        ConnectivityManager connManager = (ConnectivityManager) ScheduleWidget.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return mMobile.isConnected();
    }

    private boolean IsWifiConnected()
    {
        ConnectivityManager connManager = (ConnectivityManager) ScheduleWidget.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return mWifi.isConnected();
    }

    private void OnUpdateButtonClick()
    {
        // trololo
        Integer a = 127;
        Integer b = 127;
        Integer c = 128;
        Integer d = 128;
        boolean bAB = a == b;
        boolean bCD = c == d;
        System.out.println(a);
        System.out.println(b);
        System.out.println(bAB); // true
        System.out.println(bCD); // false
        System.out.println(a == b); // true
        System.out.println(c == d); // false

        int x=0;
        System.out.println(++x==++x);
        int xx=0;
        System.out.println(++xx==xx);
        int xxx=0;
        System.out.println(xxx==xxx++);
        int xxxx=0;
        System.out.println(xxxx++==xxxx++);
        int xxxxx=0;
        System.out.println(xxxxx++==++xxxxx);

        // trololo end ---
        
        Utility.SetTextToTextView(R.id.textViewLeft, "");
        Utility.TougleProgressBar(true);

        ScheduleDisplayManager scheduleDisplayManager = new ScheduleDisplayManager();

        // Toast
        Utility.Toasts.ShowToastMsg(R.string.xml_file_available_checking);

        if ( FileExistance(GlobalVariables.SCHEDULE_FILE_NAME) && scheduleDisplayManager.CheckXMLFileActuality() )
        {
            Utility.Toasts.ShowToastMsg(R.string.xml_available_and_actual_showing);
            
            scheduleDisplayManager.DisplaySchedule();
        }
        else
        {
            // Toast
            Utility.Toasts.ShowToastMsg(R.string.xml_not_found_localy);
            Utility.Toasts.ShowToastMsg(R.string.trying_download_data);

            // TODO
            if ( (IsWifiConnected() || IsMobileConnected()) )
            {
                Receiver htmlReceiver = new Receiver();
                htmlReceiver.getWebPageAsync.execute();
            }
            else
            {
                // Toast
                Utility.Toasts.ShowToastMsg(R.string.check_connection);

                // TODO maybe load non actual schedule
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
