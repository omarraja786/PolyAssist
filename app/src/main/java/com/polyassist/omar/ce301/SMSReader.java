package com.polyassist.omar.ce301;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class SMSReader extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsreader);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if ((findViewById(R.id.fragment_container)) != null) {
            if (savedInstanceState != null) {
                return;
            }
            getFragmentManager().beginTransaction().add(R.id.fragment_container, new SettingsFragment()).commit();
        }
    }
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }


}
