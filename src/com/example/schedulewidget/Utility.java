package com.example.schedulewidget;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

public class Utility
{

    public static class Toasts
    {
        static SharedPreferences sharedPreferences = ScheduleWidget.context.getSharedPreferences(GlobalVariables.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);

        public static void ShowToastMsg(int resID)
        {
            if ( sharedPreferences.getBoolean(GlobalVariables.PREFERENCE_SHOW_TOASTS_KEY, GlobalVariables.PREFERENCE_SHOW_TOASTS_DEFVAL) )
            {
                Toast.makeText(ScheduleWidget.context, resID, Toast.LENGTH_SHORT).show();
            }
        }

        public static void ShowToastMsg(String msg)
        {
            if ( sharedPreferences.getBoolean(GlobalVariables.PREFERENCE_SHOW_TOASTS_KEY, GlobalVariables.PREFERENCE_SHOW_TOASTS_DEFVAL) )
            {
                Toast.makeText(ScheduleWidget.context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void SetTextToTextView(int textViewId, String textToSet)
    {
        RemoteViews views = new RemoteViews(ScheduleWidget.context.getPackageName(), R.layout.schedule_widget);

        views.setTextViewText(textViewId, textToSet);

        // update layout
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(ScheduleWidget.context);
        appWidgetManager.updateAppWidget(new ComponentName(ScheduleWidget.context.getPackageName(), ScheduleWidget.class.getName()), views);
    }

    public static String GetCurrentDate(String dateFormat)
    {
        if ( dateFormat == null || dateFormat == "" )
        {
            dateFormat = "dd.MM.yyyy";
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        String currentDate = simpleDateFormat.format(calendar.getTime());
        return currentDate;
    }

    public static String GetNearestMonday(String dateFormat)
    {
        if ( dateFormat == null || dateFormat == "" )
        {
            dateFormat = "dd.MM.yyyy";
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

        calendar.setTime(calendar.getTime());
        Log.d("GetNearestMonday", calendar.getTime().toString());

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        for ( int dayIterator = 1; calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY; dayIterator++ )
        {
            calendar.set(currentYear, currentMonth, currentDayOfMonth + dayIterator);
            Log.d("GetNearestMonday", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        }

        // calendar.setFirstDayOfWeek(Calendar.MONDAY);
        String nearestMonday = simpleDateFormat.format(calendar.getTime());
        return nearestMonday;
    }

    public static String GetXMLFileCreationDate(String xmlFile)
    {
        String creationDate = "";

        try
        {
            XmlPullParser xmlPullParser;
            xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
            xmlPullParser.setInput(new StringReader(xmlFile));

            while ( xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT )
            {
                if ( xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals("schedule") )
                {
                    creationDate = xmlPullParser.getAttributeValue(0);
                    break;
                }
                xmlPullParser.next();
            }

        }
        catch (XmlPullParserException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return creationDate;
    }

    public static void TougleProgressBar(boolean state)
    {
        RemoteViews views = new RemoteViews(ScheduleWidget.context.getPackageName(), R.layout.schedule_widget);
        if ( state )
        {
            views.setViewVisibility(R.id.widgetProgressBar, View.VISIBLE);
        }
        else
        {
            views.setViewVisibility(R.id.widgetProgressBar, View.GONE);
        }

        // update layout
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(ScheduleWidget.context);
        appWidgetManager.updateAppWidget(new ComponentName(ScheduleWidget.context.getPackageName(), ScheduleWidget.class.getName()), views);
    }

}
