package com.wmiiul.calculator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import org.apache.log4j.Logger;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity
{
    private ArrayList<String> operationList;
    private ArrayList<Character> signList;
    private ArrayList<Double> numberList;
    private Logger logger= Logger.getLogger(MainActivity.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Bundle extras=getIntent().getExtras();
        operationList=extras.getStringArrayList("operationList");
        signList=(ArrayList<Character>) extras.getSerializable("signList");
        numberList=(ArrayList<Double>) extras.getSerializable("numberList");
        showHistory();
    }

    private void showHistory(){
        final TextView textView=(TextView) findViewById(R.id.textViewHistory);
        textView.setMovementMethod(new ScrollingMovementMethod());
        String text="";
        for(String s:operationList)
        {
            text+=s;
            text+="\n\n";
        }
        textView.setText(text);
        final ScrollView scrollView=(ScrollView)findViewById(R.id.ScrollView);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    public void onClickDeleteHistory(View view){
        logger.debug("DELETE HISTORY BUTTON CLICKED");
        operationList.clear();
        showHistory();
    }

    public void onClickBack(View view)
    {
        logger.debug("BACK BUTTON CLICKED");
        Intent intent=new Intent(this, MainActivity.class);
        Bundle bundle=new Bundle();
        bundle.putStringArrayList("operationList",operationList);
        bundle.putSerializable("signList",signList);
        bundle.putSerializable("numberList",numberList);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}