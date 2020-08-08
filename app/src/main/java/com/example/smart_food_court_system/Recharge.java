package com.example.smart_food_court_system;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smart_food_court_system.common.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import vn.momo.momo_partner.AppMoMoLib;
import vn.momo.momo_partner.MoMoParameterNamePayment;

public class Recharge extends AppCompatActivity {
    //MOMO SDK
    String amount;
    String orderId = "Merchant123556666";

    //HTTP
    String token;       //appData
    String phoneNumber; //customerNumber
    String hashCode;    //hash

    //General
    String description = "Nap tien vao tai khoan BKSFCS";

    EditText edtRechargeAmount;
    Button btnRecharge;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Duy");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT);
        edtRechargeAmount = (EditText)findViewById(R.id.edtRechargeAmount);
        btnRecharge = (Button)findViewById(R.id.btnRecharge);
        //mDatabase = FirebaseDatabase.getInstance().getReference().child("Duy");

        btnRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog mDiaglog = new ProgressDialog(Recharge.this);
                mDiaglog.setMessage("Please wait");
                mDiaglog.show();
                final String rechargeAmount = edtRechargeAmount.getText().toString();
                if(rechargeAmount.isEmpty()){
                    mDiaglog.dismiss();
                    Toast.makeText(Recharge.this, "You haven't type recharge amount", Toast.LENGTH_SHORT).show();
                }
                else if(rechargeAmount.equals("0")){
                    mDiaglog.dismiss();
                    Toast.makeText(Recharge.this, "Not a proper amount", Toast.LENGTH_SHORT).show();
                }
                else{
                    orderId = "MerchantBKFCS" + "user" + Common.currentUser.getUserName() + System.currentTimeMillis();
                    requestPayment();
                    mDiaglog.dismiss();

                }

            }
        });

    }
    private void requestPayment() {
        AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.PAYMENT);
        AppMoMoLib.getInstance().setActionType(AppMoMoLib.ACTION_TYPE.GET_TOKEN);
        if (edtRechargeAmount.getText().toString() != null && edtRechargeAmount.getText().toString().trim().length() != 0)
            amount = edtRechargeAmount.getText().toString().trim();

        Map<String, Object> eventValue = new HashMap<>();
        //client Required
        eventValue.put("merchantname", Common.merchantName); //Tên đối tác. được đăng ký tại https://business.momo.vn. VD: Google, Apple, Tiki , CGV Cinemas
        eventValue.put("merchantcode", Common.merchantCode); //Mã đối tác, được cung cấp bởi MoMo tại https://business.momo.vn
        eventValue.put("amount", (long)Integer.parseInt(amount)); //Kiểu integer
        eventValue.put("orderId", orderId); //unique id cho Bill order, giá trị duy nhất cho mỗi đơn hàng
        eventValue.put("orderLabel", "Mã đơn hàng"); //gán nhãn

        //client Optional - bill info
        eventValue.put("merchantnamelabel","Dịch vụ");//gán nhãn
        eventValue.put("fee", 0); //Kiểu integer
        eventValue.put("description", description); //mô tả đơn hàng - short description

        //client extra data
        eventValue.put("requestId", Common.merchantCode + "merchant_billId_" + System.currentTimeMillis());
        eventValue.put("partnerCode", Common.merchantCode);

        eventValue.put("extra", "");
        eventValue.put(MoMoParameterNamePayment.EXTRA_DATA, "");

        AppMoMoLib.getInstance().requestMoMoCallBack(this, eventValue);
    }

    //Get token callback from MoMo app an submit to server side
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO && resultCode == -1) {
            if (data != null) {
                if (data.getIntExtra("status", -1) == 0) {
                    //TOKEN IS AVAILABLE
                    //tvMessage.setText("n4yg7wP2BKzJNzdOh0czmI4WpJ75biPIUG6S8Rx0qun4Vx/KhvdPMDpzi9J9FV6f7/jPawW0ZjKE04Deqbbw1utpLsHXR2Cto/PX75W+kzv1CwQAwh+Jjzfj2r8TnfQN4x/fATplENMRjijztkwsegFhsNWcPYl87z1mh2yEDB5b2Xo4ktL0lg+xSq+mO+g5be6NyjvppLoLuWlFqdvg9TXRbYI8q7kwe3Yd1V+Z2HwK6ZRyGs22rhFY/ZhzrTbjnOfO1cOtMYpzlIFo5lJMkDYJ/+dGZFohfh2JkJbDD0e0qZK/TghmlC7Im4p+0vwa54s9I0FrLmmC3ZTM60nWCdjyt//K3hMt/hcNqBr3c1C7USC98a+T0jzHVMgU3TWNBOa7Qw41SjSVdkcvZbcKmZI/IYIqrWWtp12en/ZpTdxKitkbWo6RCSkWeVISTLdQZHS39iPmY5qDN/xRSJbPx3A9TsysoFWdvyXRIS6ts+3pR30+0dob8QWh/wvBa5l0/X4c/f0EE4e1E0i9Vsp3YRMG9IsL+9f6/8dPaxuB8nOy/IYvOaLqcRmVmbNLH5496rI6HTm4zdUiq286rtUjgg86qo03YUml3AfRAzVa1enPMYxQkQ0FyBgHNHYS1+fQIt6fB3pHUcdU9tckxgffJfaLGH3R1EQ1Cw1I1IubDGE=message: " + "Get token " + data.getStringExtra("message"));
                    token = data.getStringExtra("data"); //Token response
                    phoneNumber = data.getStringExtra("phonenumber");
                    hashCode = hashRSA(Common.merchantCode, orderId, (long)Integer.parseInt(amount),
                            "", "", "","","");
                    String env = data.getStringExtra("env");
                    if (env == null) {
                        env = "app";
                    }

                    if (token != null && !token.equals("")) {
                        // TODO: send phoneNumber & token to your server side to process payment with MoMo server
                        submitPayment();
                        // IF Momo topup success, continue to process your order


                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String currentBalance = dataSnapshot.child("User")
                                        .child(Common.currentUser.getUserName())
                                        .child("accountBalance").getValue().toString();
                                String rechargeAmount = edtRechargeAmount.getText().toString();
                                String newBalance = String.valueOf(Integer.parseInt(currentBalance) + Integer.parseInt(rechargeAmount));
                                mDatabase.child("User")
                                        .child(Common.currentUser.getUserName())
                                        .child("accountBalance").setValue(newBalance);
                                Common.currentUser.setAccountBalance(newBalance);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        Toast.makeText(Recharge.this, "Recharge successfully", Toast.LENGTH_SHORT).show();
                        Intent RechargeToHome = new Intent(Recharge.this, Home.class);
                        startActivity(RechargeToHome);

                    } else {
                        // tvMessage.setText("message: " + this.getString(R.string.not_receive_info));
                    }
                } else if (data.getIntExtra("status", -1) == 1) {
                    //TOKEN FAIL
                    String message = data.getStringExtra("message") != null ? data.getStringExtra("message") : "Thất bại";
                    //tvMessage.setText("message: " + message);
                } else if (data.getIntExtra("status", -1) == 2) {
                    //TOKEN FAIL
                    //tvMessage.setText("message: " + this.getString(R.string.not_receive_info));
                } else {
                    //TOKEN FAIL
                    //tvMessage.setText("message: " + this.getString(R.string.not_receive_info));
                }
            } else {
                //tvMessage.setText("message: " + this.getString(R.string.not_receive_info));
            }
        } else {
            //tvMessage.setText("message: " + this.getString(R.string.not_receive_info_err));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void submitPayment(){
        JSONObject root = new JSONObject();
        try {
            root.put("partnerCode", Common.merchantCode);
            root.put("partnerRefId", orderId);
            root.put("customerNumber", phoneNumber);
            root.put("appData", token);
            root.put("hash", hashCode);
            root.put("version", 2.0);
            root.put("payType", 3);
            root.put("description", description);
            Log.e("hash",hashCode);
            Log.e("ROOT", root.toString());
        }catch (Exception e1) {
            Log.d("ERROR", "Can't format JSON");
        }

        String submit = root.toString().replace("\\","");
        Log.e("Root GSON", submit);


        String url = "https://test-payment.momo.vn/pay/app";
        MediaType contentType = MediaType.parse("application/json; charset=utf-8");

        final OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(submit, contentType);
        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
                .build();
        Log.d("OKHTTP3", body.toString());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("OKHTTP3", "Request done");
                        try {
                            Log.d("OKHTTP3", response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(Recharge.this, "Report Received", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String hashRSA(String partnerCode, String partnerRefId, long amount, String partnerName,
                           String partnerTransId, String storeId, String storeName, String publicKey) {

        try {
            JSONObject root = new JSONObject();
            root.put("partnerCode", partnerCode);
            root.put("partnerRefId", partnerRefId);
            root.put("amount", amount);
            root.put("partnerName", partnerName);
            root.put("partnerTransId", partnerTransId);
            root.put("storeId", storeId);
            root.put("storeName", storeName);

//            byte[] hashBytes = new RSAEncryption().encrypt(Common.publicKey.getBytes(), root.toString());
//            String hash = new String(hashBytes);
            byte[] testByte = root.toString().getBytes(StandardCharsets.UTF_8);
            String hash = Encoder.encryptRSA(testByte, Common.publicKey);
            return hash;
        } catch (JSONException e1) {
            Log.d("ERROR", "Can't format JSON");
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}