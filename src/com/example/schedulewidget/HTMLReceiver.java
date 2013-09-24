package com.example.schedulewidget;

public class HTMLReceiver implements AsyncTaskCompleteListener<Object>
{
    HTMLPageParser htmlPageParser;
    ScheduleDisplayManager scheduleDisplayManager;

    public HTMLReceiver()
    {
        this.htmlPageParser = new HTMLPageParser();
        this.scheduleDisplayManager = new ScheduleDisplayManager();
    }

    @Override
    public void OnAsyncTaskComplete(Object result)
    {
        String stringToParse = result.toString();
        this.htmlPageParser.ParsePage(stringToParse);
        
        this.scheduleDisplayManager.DisplaySchedule();
    }
}
