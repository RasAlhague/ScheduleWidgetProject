package com.example.schedulewidget;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


public class ScheduleDisplayManager
{
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

    private final String XMLFILE_CREATION_DATE_MAP_DAY_KEY = "Day";
    private final String XMLFILE_CREATION_DATE_MAP_MONTH_KEY = "Month";
    private final String XMLFILE_CREATION_DATE_MAP_YEAR_KEY = "Year";

    public ScheduleDisplayManager()
    {
        context = ScheduleWidget.getAppContext();
    }

    public boolean CheckXMLFileActuality()
    {
        Map<String, Integer> XMLFileCreationDateMap = GetXMLFileCreationDate();

        Calendar thatDay = Calendar.getInstance();
        thatDay.set(Calendar.DAY_OF_MONTH, XMLFileCreationDateMap.get(XMLFILE_CREATION_DATE_MAP_DAY_KEY));
        thatDay.set(Calendar.MONTH, XMLFileCreationDateMap.get(XMLFILE_CREATION_DATE_MAP_MONTH_KEY) - 1); // 0-11 so 1 less
        thatDay.set(Calendar.YEAR, XMLFileCreationDateMap.get(XMLFILE_CREATION_DATE_MAP_YEAR_KEY));

        Calendar today = Calendar.getInstance();

        long diff = today.getTimeInMillis() - thatDay.getTimeInMillis(); // result in mills
        long daysGone = diff / (24 * 60 * 60 * 1000);

        SharedPreferences sharedPreferences = ScheduleWidget
                .getAppContext()
                .getSharedPreferences(GlobalVariables.SHARED_PREFERENCE_NAME, 0);

        String relevanceTimeStr = sharedPreferences.getString(SettingActivity.PREFERENCE_RELEVANCE_TIME_KEY,
                SettingActivity.PREFERENCE_RELEVANCE_TIME_DEFVAL);

        int relevanceTimeInt = Integer.parseInt(relevanceTimeStr);
        if ( daysGone < relevanceTimeInt )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private Map<String, Integer> GetXMLFileCreationDate()
    {
        String xmlFile = GetFileAsString(GlobalVariables.SCHEDULE_FILE_NAME);
        String xmlFileCreationDateStr = Utility.GetXMLFileCreationDate(xmlFile);
        Pattern pattern = Pattern.compile("(.*?)\\.(.*?)\\.(.*)");
        Matcher matcher = pattern.matcher(xmlFileCreationDateStr);

        matcher.find();
        int xmlFileCreationDateD = Integer.parseInt(matcher.group(1));
        int xmlFileCreationDateM = Integer.parseInt(matcher.group(2));
        int xmlFileCreationDateY = Integer.parseInt(matcher.group(3));

        Map<String, Integer> XMLFileCreationDateMap = new HashMap<String, Integer>();
        XMLFileCreationDateMap.put(XMLFILE_CREATION_DATE_MAP_DAY_KEY, xmlFileCreationDateD);
        XMLFileCreationDateMap.put(XMLFILE_CREATION_DATE_MAP_MONTH_KEY, xmlFileCreationDateM);
        XMLFileCreationDateMap.put(XMLFILE_CREATION_DATE_MAP_YEAR_KEY, xmlFileCreationDateY);

        return XMLFileCreationDateMap;
    }

    public void DisplaySchedule()
    {
        ArrayList<ArrayList<String>> scheduleArray = GetDailyScheduleFromXML();
        SetDataIntoLabels(scheduleArray);
//        DisplayScheduleDateCreation();
    }

    private void DisplayScheduleDateCreation()
    {
        String xmlFile = GetFileAsString(GlobalVariables.SCHEDULE_FILE_NAME);
        String xmlFileCreationDateStr = Utility.GetXMLFileCreationDate(xmlFile);

        Utility.SetTextToTextView(R.id.textView_ScheduleDate, xmlFileCreationDateStr );
    }

    private static boolean IsWeekends()
    {
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
        switch ( currentDay )
        {
            case Calendar.SUNDAY:
            {
                return true;
            }
            case Calendar.SATURDAY:
            {
                return true;
            }
            default:
            {
                return false;
            }
        }
    }

    private ArrayList<ArrayList<String>> GetDailyScheduleFromXML()
    {
        ArrayList<ArrayList<String>> dailySchedule = new ArrayList<ArrayList<String>>();

        try
        {
            // load file with schedule; warning: read xml after it will be written
            xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
            String xmlFile = GetFileAsString(GlobalVariables.SCHEDULE_FILE_NAME);
            this.xmlPullParser.setInput(new StringReader(xmlFile));

            String targetDate;
            if ( !IsWeekends() )
            {
                targetDate = Utility.GetCurrentDate(GlobalVariables.DATE_FORMAT);
            }
            else
            {
                targetDate = Utility.GetNearestMonday(GlobalVariables.DATE_FORMAT);
                Log.d("ScheduleDisplayManager: GetDailyScheduleFromXML", targetDate);
            }

            while ( xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT )
            {
                if ( xmlPullParser.getEventType() == XmlPullParser.START_TAG && xmlPullParser.getName().equals("day") )
                {
                    String attr = xmlPullParser.getAttributeValue(0);
                    if ( attr.equals(targetDate) )
                    {
                        // skip to lesson tag
                        while ( !xmlPullParser.getName().equals("lesson") )
                        {
                            xmlPullParser.nextTag();
                        }

                        while ( (!xmlPullParser.getName().equals("day")) )
                        {
                            if ( xmlPullParser.getEventType() == XmlPullParser.START_TAG )
                            {
                                ArrayList<String> attributeArray = new ArrayList<String>();
                                for ( int atribute = 0; atribute < xmlPullParser.getAttributeCount(); atribute++ )
                                {
                                    attributeArray.add(xmlPullParser.getAttributeValue(atribute));
                                }
                                dailySchedule.add(attributeArray);
                            }

                            xmlPullParser.nextTag();
                        }

                        Log.d("GetDailyScheduleFromXML()", dailySchedule.toString());
                        return dailySchedule;
                    }
                }

                if ( (xmlPullParser.getEventType() == XmlPullParser.END_TAG) && (xmlPullParser.getName().equals("schedule")) )
                {
                    Utility.Toasts.ShowToastMsg(R.string.xml_havnt_current_day);
                    break;
                }

                xmlPullParser.next();
            }
        }
        catch (Throwable e)
        {
            Utility.Toasts.ShowToastMsg(ScheduleWidget.context.getString(R.string.xml_parsing_displaying_error) + e.toString());
        }

        return dailySchedule;
    }

    private String GetFileAsString(String fileName)
    {
        FileInputStream fis;
        StringBuffer fileContent = new StringBuffer("");
        try
        {
            fis = this.context.openFileInput(fileName);

            byte[] buffer = new byte[1024];

            while ( fis.read(buffer) != -1 )
            {
                fileContent.append(new String(buffer));
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return fileContent.toString();
    }

    private void SetDataIntoLabels(ArrayList<ArrayList<String>> array)
    {
        String complexStr = "";
        for ( ArrayList<String> lessons : array )
        {
            String lessonN = lessons.get(LESSON_NUMBER_INDEX).toString();
            String lesson = lessons.get(LESSON_TITLE_INDEX).toString();
            String classroom = lessons.get(LESSON_CLASSROOM_INDEX).toString();
            String teacher = lessons.get(LESSON_TEACHER_INDEX).toString();

            if ( SHOW_LESSON_NUMBER )
            {
                complexStr += lessonN + GlobalVariables.NEXT_LINE_CHAR;
            }
            if ( SHOW_LESSON_TITLE )
            {
                complexStr += lesson + GlobalVariables.NEXT_LINE_CHAR;
            }
            if ( SHOW_CLASSROOM )
            {
                complexStr += classroom + GlobalVariables.NEXT_LINE_CHAR;
            }
            if ( SHOW_TEACHER )
            {
                complexStr += teacher + GlobalVariables.NEXT_LINE_CHAR;
            }
            complexStr += GlobalVariables.NEXT_LINE_CHAR;
        }

        Utility.TougleProgressBar(false);
        Utility.SetTextToTextView(R.id.textView_Schedule, complexStr);
    }

}
