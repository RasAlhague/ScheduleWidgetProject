package com.example.schedulewidget;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
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

    private final boolean SHOW_LESSON_NUMBER = false;
    private final boolean SHOW_LESSON_TITLE = true;
    private final boolean SHOW_TEACHER = true;
    private final boolean SHOW_CLASSROOM = true;

    private final String NEXT_LINE_CHAR = "\n";

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

            if (SHOW_LESSON_NUMBER)
            {
                complexStr += lessonN + NEXT_LINE_CHAR;
            }
            if (SHOW_LESSON_TITLE)
            {
                complexStr += lesson + NEXT_LINE_CHAR;
            }
            if (SHOW_CLASSROOM)
            {
                complexStr += classroom + NEXT_LINE_CHAR;
            }
            if (SHOW_TEACHER)
            {
                complexStr += teacher + NEXT_LINE_CHAR;
            }
            complexStr += NEXT_LINE_CHAR;
        }

        widgetViews.setTextViewText(R.id.textViewLeft, complexStr);

        // update widget layout
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.context);
        appWidgetManager
                .updateAppWidget(new ComponentName(this.context.getPackageName(), ScheduleWidget.class.getName()), this.widgetViews);
    }

    private ArrayList<ArrayList> GetDailyScheduleFromXML()
    {
        ArrayList<ArrayList> dailySchedule = new ArrayList<ArrayList>();

        try
        {
            // load file with schedule; warning: read xml after it will be written
            xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
            String xmlFile = GetFileAsString(GlobalVariables.SCHEDULE_FILE_NAME);
            this.xmlPullParser.setInput(new StringReader(xmlFile));

            String currentDate = GetCurrentDate();

            while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT)
            {
                if (xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals("day"))
                {
                    String attr = xmlPullParser.getAttributeValue(0);
                    if (attr.equals(currentDate))
                    {
                        // skip to lesson tag
                        while (!xmlPullParser.getName().equals("lesson"))
                        {
                            xmlPullParser.nextTag();
                        }

                        while ((!xmlPullParser.getName().equals("day")))
                        {
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

    private String GetFileAsString(String fileName) throws IOException
    {
        FileInputStream fis;
        fis = this.context.openFileInput(fileName);
        StringBuffer fileContent = new StringBuffer("");

        byte[] buffer = new byte[1024];

        while (fis.read(buffer) != -1)
        {
            fileContent.append(new String(buffer));
        }

        return fileContent.toString();
    }

    public String GetCurrentDate()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM.yyyy");
        String currentDate = dateFormat.format(calendar.getTime());
        return currentDate;
    }
}
