package com.polyassist.omar.ce301;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

//https://www.youtube.com/watch?v=4_CkU9L2mCo

public class startOnBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent arg1) {
        SharedPreferences sharedPrefs = context.getSharedPreferences("com.polyassist.omar.ce301", MODE_PRIVATE);
        Intent intent = new Intent(context, backgroundService.class);

        if (sharedPrefs.getBoolean("toggleState", true) == true) {
            context.startService(intent);
            Log.i("Autostart", "Service Started");
        }

        else {
            Toast.makeText(context, "Auto bootup is disabled.", Toast.LENGTH_LONG).show();
        }
    }
}
