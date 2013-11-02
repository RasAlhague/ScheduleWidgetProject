package com.example.schedulewidget;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class SettingActivity extends Activity
{
    EditText editTextLogin;
    EditText editTextPass;
    TextView editTextScheduleRelevanceTime;
    CheckBox checkBoxToastMsgs;
    
    public final static String PREFERENCE_LOGIN_KEY = "login";
    public final static String PREFERENCE_LOGIN_DEFVAL = "11@grebenyuk.ov";

    public final static String PREFERENCE_PASS_KEY = "pass";
    public final static String PREFERENCE_PASS_DEFVAL = "0ev8a-7-";

    public final static String PREFERENCE_RELEVANCE_TIME_KEY = "relevance_time";
    public final static String PREFERENCE_RELEVANCE_TIME_DEFVAL = "5";

    public final static String PREFERENCE_SHOW_TOASTS_KEY = "is_toasts";
    public final static boolean PREFERENCE_SHOW_TOASTS_DEFVAL = false;

    private String PreferenceLogin = "";
    private String PreferencePass = "";
    private String PreferenceRelevanceTime = "";
    private boolean PreferenceShowToasts = true;

    private void InitializeViews()
    {
        editTextLogin = (EditText) findViewById(R.id.editTextLogin);
        editTextPass = (EditText) findViewById(R.id.editTextPass);
        editTextScheduleRelevanceTime = (TextView) findViewById(R.id.editTextScheduleRelevanceTime);
        checkBoxToastMsgs = (CheckBox) findViewById(R.id.checkBoxToastMsgs);
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
        sharedPreferencesEditor.putBoolean(PREFERENCE_SHOW_TOASTS_KEY, checkBoxToastMsgs.isChecked());
        
        sharedPreferencesEditor.commit();
    }

    private void LoadPreferenceToViews()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(GlobalVariables.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        PreferenceLogin = sharedPreferences.getString(PREFERENCE_LOGIN_KEY, PREFERENCE_LOGIN_DEFVAL);
        PreferencePass = sharedPreferences.getString(PREFERENCE_PASS_KEY, PREFERENCE_PASS_DEFVAL);
        PreferenceRelevanceTime = sharedPreferences.getString(PREFERENCE_RELEVANCE_TIME_KEY, PREFERENCE_RELEVANCE_TIME_DEFVAL);
        PreferenceShowToasts = sharedPreferences.getBoolean(PREFERENCE_SHOW_TOASTS_KEY, PREFERENCE_SHOW_TOASTS_DEFVAL);

        editTextLogin.setText(PreferenceLogin);
        editTextPass.setText(PreferencePass);
        editTextScheduleRelevanceTime.setText(PreferenceRelevanceTime);
        checkBoxToastMsgs.setChecked(PreferenceShowToasts);
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
