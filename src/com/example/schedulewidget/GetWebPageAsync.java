package com.example.schedulewidget;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import android.os.AsyncTask;
import android.util.Log;

public class GetWebPageAsync extends AsyncTask<Void, Void, String>
{
    private AsyncTaskCompleteListener<String> callback;
    private String ASUPORTAL_URL_WITH_PARAMETERS;

    GetWebPageAsync(/* AsyncTaskCompleteListener<String> callback */)
    {
        ASUPORTAL_URL_WITH_PARAMETERS = GetURLWithParameters("11@grebenyuk.ov", "0ev8a-7-");
        /*
         * if (callback != null)
         * {
         * this.callback = callback;
         * }
         */
    }

    private String GetURLWithParameters(String user, String pass)
    {
        String userEncoded = "";
        String passEncoded = "";
        try
        {
            userEncoded = URLEncoder.encode(user, "UTF-8");
            passEncoded = URLEncoder.encode(pass, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        return "http://www.asuportal.duikt.edu.ua/feeds/index/1?login=" + userEncoded + "&password=" + passEncoded
                + "&submit=%D0%92%D0%BE%D0%B9%D1%82%D0%B8";
    }

    @Override
    protected String doInBackground(Void... params)
    {
        String html = null;

        try
        {
            Response response = Jsoup.connect(ASUPORTAL_URL_WITH_PARAMETERS).timeout(10000).execute();

            html = response.parse().html();

            Log.i("HTML", html.toString());

            return html;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return html;
    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);

        callback.OnAsyncTaskComplete(result);
    }
}