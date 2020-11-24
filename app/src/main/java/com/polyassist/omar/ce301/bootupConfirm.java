package com.polyassist.omar.ce301;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class bootupConfirm extends Activity{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toast.makeText(getBaseContext(), "Health App Started", Toast.LENGTH_LONG).show();
    }
}
