package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.MenuItemCompat;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.smart_food_court_system.model.FoodOrder;
import com.example.smart_food_court_system.model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class GenerateReport extends AppCompatActivity {
    Button btnGenerateReport;
    DatabaseReference mDatabase;
    StringBuilder data;
    String all="";
    Toolbar toolbar = null;
    Calendar myCalendar;
    EditText edtGenerateReportFrom, edtGenerateReportTo;
    String strDateFrom = "", strDateTo = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_report);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myCalendar = Calendar.getInstance();
        edtGenerateReportFrom= (EditText) findViewById(R.id.edtGenerateReportFrom);
        edtGenerateReportTo= (EditText) findViewById(R.id.edtGenerateReportTo);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Duy");
        final DatePickerDialog.OnDateSetListener dateFrom = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy/MM/dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                edtGenerateReportFrom.setText(sdf.format(myCalendar.getTime()));
                strDateFrom = sdf.format(myCalendar.getTime());
            }

        };

        edtGenerateReportFrom.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(GenerateReport.this, dateFrom, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final DatePickerDialog.OnDateSetListener dateTo = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy/MM/dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                edtGenerateReportTo.setText(sdf.format(myCalendar.getTime()));
                strDateTo = sdf.format(myCalendar.getTime());
            }

        };
        edtGenerateReportTo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(GenerateReport.this, dateTo, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        btnGenerateReport = (Button)findViewById(R.id.btnGenerateReport);
        btnGenerateReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final SimpleDateFormat sdformat = new SimpleDateFormat("yyyy/MM/dd");
                if(strDateFrom.isEmpty() || strDateTo.isEmpty()){
                    Toast.makeText(GenerateReport.this, "You not choose date from or date to", Toast.LENGTH_SHORT).show();
                }
                else{
                    try {
                        final Date d1 = sdformat.parse(strDateFrom);
                        final Date d2 = sdformat.parse(strDateTo);
                        if(d1.compareTo(d2) > 0){
                            Toast.makeText(GenerateReport.this, "Your choose date from or date to is wrong", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            mDatabase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot datas: dataSnapshot.child("Order").getChildren()){
                                        Order order = datas.getValue(Order.class);
                                        try {
                                            Date dtemp = sdformat.parse(order.getTimeStamp());
                                            String temp = "ready " + order.getUserName();
                                            if(dtemp.compareTo(d1) < 0 || dtemp.compareTo(d2) > 0 || order.getStatus().equals(temp));
                                            else{
                                                String userName = order.getUserName();
                                                String orderId = order.getOrderID();
                                                String total = order.getTotal();
                                                String timeStamp = order.getTimeStamp();
                                                String methodPayment = order.getMethodPayment();
                                                String c = ",";
                                                Map.Entry<String, FoodOrder> entry1 = order.getFoodOrderList().entrySet().iterator().next();
                                                String firstFoodOrderName = entry1.getValue().getFoodName();
                                                all = all + "\n" +userName+c+orderId+c+total+c+timeStamp+c+methodPayment;
                                                for (Map.Entry<String,FoodOrder> entry : order.getFoodOrderList().entrySet()) {
                                                    String foodOrderName = entry.getValue().getFoodName();
                                                    String foodOrderStallName = entry.getValue().getFoodStallName();
                                                    String foodOrderPrice = entry.getValue().getPrice();
                                                    String foodOrderQuantity = entry.getValue().getQuantity();
                                                    if(foodOrderName == firstFoodOrderName) {
                                                        all = all +c+foodOrderName+c+foodOrderStallName+c+foodOrderPrice+c+foodOrderQuantity;
                                                    }
                                                    else{
                                                        all = all + "\n" + ",,,,," +foodOrderName+c+foodOrderStallName+c+foodOrderPrice+c+foodOrderQuantity;
                                                    }
                                                }
                                            }

                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if(all.isEmpty()){
                                        Log.e("AA", "ALL IS EMPTY");
                                    }
                                    export(view);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }


            }
        });

    }

    public void export(View view){
        data = new StringBuilder();
        data.append("User Name,Order ID,Total,Time Stamp, Method Payment, Food Order List");
        data.append("\n,,,,,Food Name, Food Stall Name, Food Price, Food Quantity");
        Log.e("Test", all);
        data.append(all);

        try{
            //saving the file into device
            FileOutputStream out = openFileOutput("report.csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes());
            out.close();

            //exporting
            Context context = getApplicationContext();
            File filelocation = new File(getFilesDir(), "report.csv");
            Uri path = FileProvider.getUriForFile(context, "com.example.exportcsv.fileprovider", filelocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "Send mail"));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_go_home) {
            Intent intent = new Intent(GenerateReport.this, HomeManager.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

    }
}