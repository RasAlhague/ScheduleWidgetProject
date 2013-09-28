package com.example.schedulewidget;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SettingActivity extends Activity
{
    EditText editTextLogin;
    EditText editTextPass;
    TextView editTextScheduleRelevanceTime;

    private final String PREFERENCE_LOGIN_KEY = "login";
    private final String PREFERENCE_PASS_KEY = "pass";
    private final String PREFERENCE_RELEVANCE_TIME_KEY = "relevance_time";
    private final String PREFERENCE_LOGIN_DEFVAL = "";
    private final String PREFERENCE_PASS_DEFVAL = "";
    private final String PREFERENCE_RELEVANCE_TIME_DEFVAL = "5";

    private String PreferenceLogin = "";
    private String PreferencePass = "";
    private String PreferenceRelevanceTime = "";

    private void InitializeViews()
    {
        editTextLogin = (EditText) findViewById(R.id.editTextLogin);
        editTextPass = (EditText) findViewById(R.id.editTextPass);
        editTextScheduleRelevanceTime = (TextView) findViewById(R.id.editTextScheduleRelevanceTime);
    }
    
    public void OnSaveButtonClick(View view)
    {
        SavePreference();
        
        this.finish();
    }

    private void SavePreference()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(GlobalVariables.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(PREFERENCE_LOGIN_KEY, editTextLogin.getText().toString());
        sharedPreferencesEditor.putString(PREFERENCE_PASS_KEY, editTextPass.getText().toString());
        sharedPreferencesEditor.putString(PREFERENCE_RELEVANCE_TIME_KEY, editTextScheduleRelevanceTime.getText().toString());
        sharedPreferencesEditor.commit();
    }

    private void LoadPreferenceToViews()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(GlobalVariables.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        PreferenceLogin = sharedPreferences.getString(PREFERENCE_LOGIN_KEY, PREFERENCE_LOGIN_DEFVAL);
        PreferencePass = sharedPreferences.getString(PREFERENCE_PASS_KEY, PREFERENCE_PASS_DEFVAL);
        PreferenceRelevanceTime = sharedPreferences.getString(PREFERENCE_RELEVANCE_TIME_KEY, PREFERENCE_RELEVANCE_TIME_DEFVAL);

        editTextLogin.setText(PreferenceLogin);
        editTextPass.setText(PreferencePass);
        editTextScheduleRelevanceTime.setText(PreferenceRelevanceTime);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        InitializeViews();
        LoadPreferenceToViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }

    @Override
    protected void onResume()
    {
        LoadPreferenceToViews();

        super.onResume();
    }

}
