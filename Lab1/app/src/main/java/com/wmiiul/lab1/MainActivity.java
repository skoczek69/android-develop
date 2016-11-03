package com.wmiiul.lab1;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button buttonPL = (Button) findViewById(R.id.buttonPL);
        final Button buttonEN = (Button) findViewById(R.id.buttonEN);
        final Button buttonDE = (Button) findViewById(R.id.buttonDE);
        final TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText("Proszę wybrać język");


        buttonPL.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                textView.setText("Witamy");
            }
        });

        buttonEN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                textView.setText("Welcome");
            }
        });

        buttonDE.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                textView.setText("Willkommen");
            }
        });
    }
}
