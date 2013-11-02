package com.example.schedulewidget;

public class Receiver implements AsyncTaskCompleteListener
{
    private HTMLPageParser htmlPageParser;
    private GetWebPageAsync getWebPageAsync;
    private ScheduleDisplayManager scheduleDisplayManager;

    public Receiver()
    {
        this.getWebPageAsync = new GetWebPageAsync(this);
        this.htmlPageParser = new HTMLPageParser();
        this.scheduleDisplayManager = new ScheduleDisplayManager();
    }
    
    public void StartExecSequence()
    {
        getWebPageAsync.execute();
    }

    @Override
    public void OnAsyncTaskComplete(String result)
    {
        String htmlPage = result;
        
        if(Utility.CheckHTMLPage(htmlPage))
        {
            try
            {
                this.htmlPageParser.ParsePage(htmlPage);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            
            this.scheduleDisplayManager.DisplaySchedule();
        }
        else
        {
            Utility.SetTextToTextView(R.id.textView_Schedule, ScheduleWidget.getAppContext().getString(R.string.site_unaviable));
        }
        
        Utility.TougleProgressBar(false);
    }
}
