package com.example.smart_food_court_system;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import vn.momo.momo_partner.AppMoMoLib;
import vn.momo.momo_partner.MoMoParameterNameMap;


public class TestPayment extends AppCompatActivity {

    private String amount = "10000";
    private String fee = "0";
    int environment = 0;//developer default
    private String merchantName = "smartfoodcourthcmut";
    private String merchantCode = "MOMOO1KC20200802";
    private String merchantNameLabel = "Nhà cung cấp";
    private String description = "Thanh toán dịch vụ ABC";
    private String customerNumber;
    private String partnerCode = merchantCode;
    private String partnerRefId = "orderId123456789";
    private String appData;
    private String hash;
    private String version = "2";
    private static final String PUBLIC_KEY_BASE64_ENCODED = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAh+fBkzLrYk7dMySeASH3U6Z8+sANdmYbESOzvoaMIJ4RsvE94HJY8mw6fcoq5NddSIcdJa2RabDCBaYvkljiW5064wFW9xRhUhT9lM/JI8w9WG01499qfn1+m1hAOx8CNGJuz91r/kQL7S+xCTc+s+mO0EwLWRqUiFVQZtPAXdR/wg1UTHuY1zOmoD0dWq5yO562tO2fOLKIlYAe5zC0+J4yypGODLN0FJ5OdGH99WNCHpYzJE1PgIOOKnadS04ql0wywfZtQTSp8mex/qAazwVZYNPLO5NtwV5ReSXDCNei+w0zdi3cui7l7KWbWuy9luwru32cKG5N6R9KKU+j5/X3g86HviCmasPx/azQXyyUl2MmAumjRRDkPv+iBIUjvc6+L7iUJNmOeclpOySSKP43K6oFyx+8KbCJNu2Y0xR2xCYrfWIqGsahO5HIm0VzER2k1kxxxlMvcjt9Yu6eqm1YykA1/zUc9zcaYG/EL3sypHdc1yJp6jBNC2wTWkcE+MpmGAH8B+uKOw8hNvoV1i5p18AFBPKxBNdus01cxKdg5lkRddqAtj2vMuR/UYUJhmB76wjc0hVSp/AvZP9nh2bwT7IMzzaOmzH98QvLyppJw4hTT0F2rJpdNqY4KG4y7EKBbc4W1CQGSDksgORRhsQ/26GMaITAXu26gvCkkN8CAwEAAQ==";

    Button button, btnConfirmPayment;
    EditText edAmount;
    TextView tvMessage;
    int total_amount = 90000;
    int total_fee = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_payment);
        AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT); // AppMoMoLib.ENVIRONMENT.PRODUCTION
        button = (Button) findViewById(R.id.btnMomoPayment);
        edAmount = (EditText) findViewById(R.id.edAmount);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPayment();
            }
        });
        btnConfirmPayment = (Button) findViewById(R.id.btnConfirmPayment);
        btnConfirmPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDataToServer();
            }
        });

    }

    private void requestPayment() {
        AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.PAYMENT);
        AppMoMoLib.getInstance().setActionType(AppMoMoLib.ACTION_TYPE.GET_TOKEN);
        if (edAmount.getText().toString() != null && edAmount.getText().toString().trim().length() != 0)
            amount = edAmount.getText().toString().trim();

        Map<String, Object> eventValue = new HashMap<>();
        //client Required
        eventValue.put("merchantname", merchantName); //Tên đối tác. được đăng ký tại https://business.momo.vn. VD: Google, Apple, Tiki , CGV Cinemas
        eventValue.put("merchantcode", merchantCode); //Mã đối tác, được cung cấp bởi MoMo tại https://business.momo.vn
        eventValue.put("amount", total_amount); //Kiểu integer
        eventValue.put("orderId", "orderId123456789"); //uniqueue id cho Bill order, giá trị duy nhất cho mỗi đơn hàng
        eventValue.put("orderLabel", "Mã đơn hàng"); //gán nhãn

        //client Optional - bill info
        eventValue.put("merchantnamelabel", "Dịch vụ");//gán nhãn
        eventValue.put("fee", total_fee); //Kiểu integer
        eventValue.put("description", description); //mô tả đơn hàng - short description

        //client extra data
        eventValue.put("requestId", merchantCode + "merchant_billId_" + System.currentTimeMillis());
        eventValue.put("partnerCode", merchantCode);
        //Example extra data
        JSONObject objExtraData = new JSONObject();
        try {
            objExtraData.put("site_code", "008");
            objExtraData.put("site_name", "CGV Cresent Mall");
            objExtraData.put("screen_code", 0);
            objExtraData.put("screen_name", "Special");
            objExtraData.put("movie_name", "Kẻ Trộm Mặt Trăng 3");
            objExtraData.put("movie_format", "2D");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        eventValue.put("extraData", objExtraData.toString());

        eventValue.put("extra", "");
        AppMoMoLib.getInstance().requestMoMoCallBack(this, eventValue);


    }

    //Get token callback from MoMo app an submit to server side
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO && resultCode == -1) {
            if (data != null) {
                if (data.getIntExtra("status", -1) == 0) {
                    //TOKEN IS AVAILABLE
                    tvMessage.setText("message: " + "Get token " + data.getStringExtra("message"));
                    String token = data.getStringExtra("data"); //Token response
                    String phoneNumber = data.getStringExtra("phonenumber");
                    String env = data.getStringExtra("env");
                    if (env == null) {
                        env = "app";
                    }

                    if (token != null && !token.equals("")) {
                        // TODO: send phoneNumber & token to your server side to process payment with MoMo server
                        // IF Momo topup success, continue to process your order
                        customerNumber = phoneNumber;
                        appData = token;
                        hash = hashRSA();
                        Log.d("hash:", hash);


                    } else {
                        tvMessage.setText("message: " + this.getString(R.string.not_receive_info));
                    }
                } else if (data.getIntExtra("status", -1) == 1) {
                    //TOKEN FAIL
                    String message = data.getStringExtra("message") != null ? data.getStringExtra("message") : "Thất bại";
                    tvMessage.setText("message: " + message);
                } else if (data.getIntExtra("status", -1) == 2) {
                    //TOKEN FAIL
                    tvMessage.setText("message: " + this.getString(R.string.not_receive_info));
                } else {
                    //TOKEN FAIL
                    tvMessage.setText("message: " + this.getString(R.string.not_receive_info));
                }
            } else {
                tvMessage.setText("message: " + this.getString(R.string.not_receive_info));
            }
        } else {
            tvMessage.setText("message: " + this.getString(R.string.not_receive_info_err));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void sendDataToServer() {
        JSONObject root = new JSONObject();

        try {
            root.put("customerNumber", customerNumber);
            root.put("partnerCode", partnerCode);
            root.put("partnerRefId", partnerRefId);
            root.put("appData", appData);
            root.put("hash", hash);
            root.put("version", version);
        }catch (JSONException e1) {
            Log.d("ERROR", "Can't format JSON");
        }
        String url = "https://test-payment.momo.vn";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(root.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Log.d("OKHTTP3", body.toString());
        try (Response response = client.newCall(request).execute()) {
            Log.d("OKHTTP3", "Request done");
            Log.d("OKHTTP3", response.body().string());
        }catch (IOException e){
            e.printStackTrace();
            Log.d("OKHTTP3", "Exception while doing post request");
        }

    }

    private String hashRSA() {
        final JSONObject root = new JSONObject();
        try {
            root.put("partnerCode", partnerCode);
            root.put("partnerRefId", partnerRefId);
            root.put("amount", total_amount);

            byte[] base64EncryptedMessage = new RSAEncryption().encrypt(PUBLIC_KEY_BASE64_ENCODED.getBytes(), root.toString());
            String base64EncryptedMessageString = new String(base64EncryptedMessage);
            return base64EncryptedMessageString;
        } catch (JSONException e1) {
            Log.d("ERROR", "Can't format JSON");
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


}