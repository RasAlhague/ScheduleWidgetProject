package com.example.schedulewidget;

public class HTMLReceiver implements AsyncTaskCompleteListener
{
    private HTMLPageParser htmlPageParser;
    private ScheduleDisplayManager scheduleDisplayManager;
    public GetWebPageAsync getWebPageAsync;

    public HTMLReceiver()
    {
        this.htmlPageParser = new HTMLPageParser();
        this.scheduleDisplayManager = new ScheduleDisplayManager();
        this.getWebPageAsync = new GetWebPageAsync(this);
    }

    @Override
    public void OnAsyncTaskComplete(String result)
    {
        String stringToParse = result;
        try
        {
            this.htmlPageParser.ParsePage(stringToParse);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        this.scheduleDisplayManager.DisplaySchedule();
    }
}
