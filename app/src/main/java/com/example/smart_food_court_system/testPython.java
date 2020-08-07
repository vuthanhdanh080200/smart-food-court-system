package com.example.smart_food_court_system;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

public class testPython extends AppCompatActivity {
    TextView tv;
    public String partnerCode = "MOMOO1KC20200802";
    public String partnerRefId = "orderId123456789";
    public String amount = "10000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_python);

        initPython();
        getHash();
    }

    private void initPython(){
        if(!Python.isStarted()){
            Python.start(new AndroidPlatform(testPython.this));

        }
    }

    private void getHash(){
        Python python = Python.getInstance();
        PyObject pythonFile = python.getModule("hashPython");
        PyObject obj = pythonFile.callAttr("hash", partnerCode, partnerRefId, amount);
        tv = (TextView)findViewById(R.id.tv);
        tv.setText(obj.toString());

    }
}