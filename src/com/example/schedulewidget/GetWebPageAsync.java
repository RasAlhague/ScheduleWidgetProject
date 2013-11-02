package com.example.schedulewidget;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class GetWebPageAsync extends AsyncTask<Void, Integer, String>
{
    private AsyncTaskCompleteListener callback;
    private String ASUPORTAL_URL_WITH_PARAMETERS;
    ProgressDialog progressDialog;
    
    private int ConnectTimeoutMs = 10000;

    GetWebPageAsync(AsyncTaskCompleteListener callback)
    {
        SharedPreferences sharedPreferences = ScheduleWidget
                .getAppContext()
                .getSharedPreferences(GlobalVariables.SHARED_PREFERENCE_NAME, 0);

        String userLogin = sharedPreferences.getString(SettingActivity.PREFERENCE_LOGIN_KEY, SettingActivity.PREFERENCE_LOGIN_DEFVAL);
        String userPass = sharedPreferences.getString(SettingActivity.PREFERENCE_PASS_KEY, SettingActivity.PREFERENCE_PASS_DEFVAL);

//        ASUPORTAL_URL_WITH_PARAMETERS = GetURLWithParameters("11@grebenyuk.ov", "0ev8a-7-");
         ASUPORTAL_URL_WITH_PARAMETERS = GetURLWithParameters(userLogin, userPass);

        this.callback = callback;
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
            Response response = Jsoup.connect(ASUPORTAL_URL_WITH_PARAMETERS).timeout(ConnectTimeoutMs).execute();
            html = response.parse().html();

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