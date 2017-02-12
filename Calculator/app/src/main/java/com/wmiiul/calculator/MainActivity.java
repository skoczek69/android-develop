package com.wmiiul.calculator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Double> numberList;
    private ArrayList<Character> signList;
    private ArrayList<String> operationList;
    private String operation;
    private StringBuffer number;
    TextView textView;
    String buttonText;
    Boolean toClear;
    private Logger logger= Logger.getLogger(MainActivity.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numberList=new ArrayList<>();
        signList=new ArrayList<>();
        operationList=new ArrayList<>();
        textView=(TextView) findViewById(R.id.textView);
        number=new StringBuffer("");
        textView.setMovementMethod(new ScrollingMovementMethod());
        toClear=false;
        operation="";
    }

    public void clickNumber(View view)
    {
        if (toClear)
            clear();
        Button b=(Button)view;
        buttonText=b.getText().toString();
        switch (buttonText)
        {
            case "+":
                logger.debug("PLUS BUTTON CLICKED");
                if(tryMakeNumber())
                {
                    signList.add('+');
                }
                break;
            case "-":
                logger.debug("MINUS BUTTON CLICKED");
                if(number.toString().equals(""))
                {
                    number.append("-");
                }
                else if (tryMakeNumber())
                {
                    signList.add('-');
                }
                break;
            case "*":
                logger.debug("MULTIPLE BUTTON CLICKED");
                if(tryMakeNumber())
                {
                    signList.add('*');
                }
                break;
            case "/":
                logger.debug("DIVIDE BUTTON CLICKED");
                if(tryMakeNumber())
                {
                    signList.add('/');
                }
                break;
            case ".":
                logger.debug("DOT BUTTON CLICKED");
                if (isToLong(number))
                    break;
                if (number.length()>=1 )
                {
                    if (number.indexOf(".")==-1)
                    {
                        number.append(".");
                    }
                }
                break;
            case "0":
                logger.debug("0 BUTTON CLICKED");
                if (isToLong(number))
                    break;
                if (number.length()>2)
                {
                    number.append("0");
                }
                else if(number.toString().equals("0") || number.toString().equals("-0"))
                {
                    break;
                }
                else
                    number.append("0");
                break;
            default:
                logger.debug(buttonText+" BUTTON CLICKED");
                if (isToLong(number))
                    break;
                number.append(buttonText);
        }
        textView.setText(showHistory()+ getOperationText()+number.toString());
    }

    private boolean isToLong(StringBuffer string)
    {
        if (string.length()<12)
            return false;
        else
            return true;
    }

    private boolean tryMakeNumber()
    {
        Double newNumber;
        if (!number.toString().equals("") && (!number.toString().equals("-"))) {
            try
            {
                newNumber = Double.parseDouble(number.toString());
                if (newNumber==0 && signList.get(signList.size()-1).equals('/')){
                    return false;
                }
                numberList.add(newNumber);
                number.delete(0,number.length());
                return true;
            }
            catch (Exception e)
            {
                return false;
            }
        }
        return false;
    }


    private String getOperationText()
    {
        String textToReturn="";
        int numberListSize=numberList.size();
        int signListSize=signList.size();
        int max;
        if (numberListSize>=signListSize)
            max=numberListSize;
        else
            max=signListSize;
        for(int i=0;i<max;i++)
        {
            if (i<numberListSize)
                textToReturn+=numberList.get(i);
            if(i<signListSize)
                textToReturn+=signList.get(i).toString();
        }
        return textToReturn;
    }

    public void clickEqual(View view)
    {
        logger.debug("EQUAL BUTTON CLICKED");
        if(toClear){
            clear();
        }
        tryMakeNumber();
        Double result=null;
        if((signList.size()+1)==numberList.size())
        {
            operation= getOperationText();
            for(int i=0;i<signList.size();i++){
                switch(signList.get(i)){
                    case '*':
                        result=numberList.get(i)*numberList.get(i+1);
                        numberList.set(i,result);
                        numberList.remove(i+1);
                        signList.remove(i);
                        i--;
                        break;
                    case '/':
                        result=numberList.get(i)/numberList.get(i+1);
                        numberList.set(i,result);
                        numberList.remove(i+1);
                        signList.remove(i);
                        i--;
                        break;
                }
            }
            result=numberList.get(0);
            for (int i=0;i<signList.size();i++)
            {
                switch (signList.get(i))
                {
                    case '+':
                        result+=numberList.get(i+1);
                        break;
                    case '-':
                        result-=numberList.get(i+1);
                        break;
                }
            }
            operation+="="+result;
            operationList.add(operation);
            textView.setText(showHistory());
            toClear=true;
        }
    }

    private void clear()
    {
        signList.removeAll(signList);
        numberList.removeAll(numberList);
        operation="";
        number.delete(0,number.length());
        textView.setText(showHistory());
        toClear=false;
    }

    public void toHistoryActivity(View view)
    {
        logger.debug("HISTORY BUTTON CLICKED");
        tryMakeNumber();
        if(toClear){
            clear();
        }
        Intent intent=new Intent(this, HistoryActivity.class);
        intent.putExtra("operationList",operationList);
        intent.putExtra("signList",signList);
        intent.putExtra("numberList",numberList);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle extras=getIntent().getExtras();
        try {
            operationList=extras.getStringArrayList("operationList");
            signList=(ArrayList<Character>) extras.getSerializable("signList");
            numberList=(ArrayList<Double>)extras.getSerializable("numberList");
            if ((numberList.size() != 0) && (numberList.size() > signList.size())) {
                number.append(numberList.get(numberList.size() - 1));
                numberList.remove(numberList.size() - 1);
            }
            if (getOperationText().equals("")) {
                textView.setText(showHistory());
            }
            else{
                textView.setText(showHistory() + getOperationText() + number);
            }
        }
        catch (Exception e){
            numberList=new ArrayList<>();
            signList=new ArrayList<>();
            operationList=new ArrayList<>();
            textView=(TextView) findViewById(R.id.textView);
            number=new StringBuffer("");
            textView.setMovementMethod(new ScrollingMovementMethod());
            toClear=false;
            operation="";
            BasicConfigurator.configure();
            logger.setLevel(Level.DEBUG);
        }

    }

    public void CE(View view)
    {
        logger.debug("CE BUTTON CLICKED");
        if(toClear){
            clear();
        }
        if(number.length()!=0){
            number.delete(0,number.length());
            textView.setText(showHistory()+ getOperationText());
            if(numberList.size()==0){
                textView.setText(showHistory());
            }
        }
        else{
            if(numberList.size()>0){
                signList.remove(signList.size()-1);
                number.append(numberList.get(numberList.size()-1));
                numberList.remove(numberList.size()-1);
                textView.setText(showHistory()+ getOperationText()+number);
            }
        }
    }

    public void C(View view)
    {
        logger.debug("C BUTTON CLICKED");
        clear();
        operationList.clear();
        textView.setText(showHistory());

    }

    private String showHistory(){
        String result="";
        int sizeOfOperationList=operationList.size();
        if(sizeOfOperationList>=3){
            result+=operationList.get(sizeOfOperationList-3)+"\n";
        }
        if(sizeOfOperationList>=2){
            result+=operationList.get(sizeOfOperationList-2)+"\n";
        }
        if(sizeOfOperationList>=1){
            result+=operationList.get(sizeOfOperationList-1)+"\n";
        }
        return result;
    }
}
