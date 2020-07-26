package com.example.smart_food_court_system;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.smart_food_court_system.common.Common;

public class ItStaffHome extends AppCompatActivity {
    Button btnSystem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_it_staff_home);
        btnSystem=(Button)findViewById(R.id.btnSystem);
        if(Common.power.equals("offSystem")) {
            btnSystem.setText("Turn off system");
        }
        else{
            btnSystem.setText("Turn on system");
        }

        btnSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Common.power.equals("offSystem")){
                    Toast.makeText(ItStaffHome.this, "The system has been turned off!", Toast.LENGTH_SHORT).show();
                    Common.power="offSystem";
                }
                else{
                    Toast.makeText(ItStaffHome.this, "The system has been turned on!", Toast.LENGTH_SHORT).show();
                    Common.power="offSystem";
                }
                if(Common.power.equals("offSystem")) {
                    btnSystem.setText("Turn off system");
                }
                else{
                    btnSystem.setText("Turn on system");
                }

            }
        });
    }
}