package com.ka12.ayirimatrimony;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class FAQ extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f_a_q);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null)actionBar.setTitle("FAQ's");
    }
}