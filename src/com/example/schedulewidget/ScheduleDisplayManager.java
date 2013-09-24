package com.example.schedulewidget;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.xmlpull.v1.XmlPullParser;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class ScheduleDisplayManager
{
    private RemoteViews widgetViews;
    private Context context;
    private XmlPullParser xmlPullParser;

    private final int LESSON_NUMBER_INDEX = 0;
    private final int LESSON_TITLE_INDEX = 1;
    private final int LESSON_CLASSROOM_INDEX = 2;
    private final int LESSON_TEACHER_INDEX = 3;

    public ScheduleDisplayManager()
    {
        context = ScheduleWidget.getAppContext();
        widgetViews = new RemoteViews(this.context.getPackageName(), R.layout.schedule_widget);
    }

    public void DisplaySchedule()
    {
        ArrayList<ArrayList> scheduleArray = GetDailyScheduleFromXML();
        SetDataIntoLabels(scheduleArray);
    }

    private void SetDataIntoLabels(ArrayList<ArrayList> array)
    {
        String complexStr = "";
        for (ArrayList lessons : array)
        {
            String lessonN = lessons.get(LESSON_NUMBER_INDEX).toString();
            String lesson = lessons.get(LESSON_TITLE_INDEX).toString();
            String classroom = lessons.get(LESSON_CLASSROOM_INDEX).toString();
            String teacher = lessons.get(LESSON_TEACHER_INDEX).toString();
            
            complexStr = complexStr + lessonN + " " + lesson + "\n" + classroom + "\n" + teacher + "\n" + "\n";
        }
        
        widgetViews.setTextViewText(R.id.textViewLeft, complexStr);
        
        //update widget layout
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.context);
        appWidgetManager.updateAppWidget(new ComponentName(this.context.getPackageName(), ScheduleWidget.class.getName()), this.widgetViews);
    }

    private ArrayList<ArrayList> GetDailyScheduleFromXML()
    {
        ArrayList<ArrayList> dailySchedule = new ArrayList<ArrayList>();

        try
        {
            // load file with schedule; warning: read xml after it will be written
            this.xmlPullParser = this.context.getResources().getXml(R.xml.schedule);

            String currentDate = GetCurrentDate();

            while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT)
            {
                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals("day"))
                {
                    String attr = xmlPullParser.getAttributeValue(0);
                    if (attr.equals(currentDate))
                    {
                        //skip to lesson tag
                        while (!xmlPullParser.getName().equals("lesson"))
                        {
//                            Log.w("", xmlPullParser.getName() + "   " + xmlPullParser.getEventType() + "   " + xmlPullParser.getDepth());
                            xmlPullParser.nextTag();
                        }
                        
                        //
                        while ( (! xmlPullParser.getName().equals("day")) )
                        {
//                            Log.w("", xmlPullParser.getName() + "   " + xmlPullParser.getEventType() + "   " + xmlPullParser.getDepth());

                            if (xmlPullParser.getEventType() == XmlPullParser.START_TAG)
                            {
                                ArrayList<String> attributeArray = new ArrayList<String>();
                                for (int atribute = 0; atribute < xmlPullParser.getAttributeCount(); atribute++)
                                {
                                    attributeArray.add(xmlPullParser.getAttributeValue(atribute));
                                }
                                dailySchedule.add(attributeArray);
                            }
                            
                            xmlPullParser.nextTag();
//                            Log.w("", xmlPullParser.getName() + "   " + xmlPullParser.getEventType() + "   " + xmlPullParser.getDepth());
                        }
                        
                        return dailySchedule;
                    }
                }

                xmlPullParser.next();
            }
        }
        catch (Throwable e)
        {
            Toast.makeText(this.context, "Ошибка при загрузке XML-документа: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        return dailySchedule;
    }

    public String GetCurrentDate()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM.yyyy");
        String currentDate = dateFormat.format(calendar.getTime());
        return currentDate;
    }
}
