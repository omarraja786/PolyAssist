package com.polyassist.omar.ce301;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


public class splashScreen extends AppCompatActivity {
    //https://www.youtube.com/watch?v=E5k696ekXwg

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }
}
