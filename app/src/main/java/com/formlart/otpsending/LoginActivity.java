package com.formlart.otpsending;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.silvestrpredko.dotprogressbar.DotProgressBar;
import com.github.silvestrpredko.dotprogressbar.DotProgressBarBuilder;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission_group.CAMERA;

public class LoginActivity extends AppCompatActivity implements
        SMSReceiver.OTPReceiveListener{
    CardView go_now;
    EditText phone_number;
    String phone_number_data;
    Dialog dialog12;
    public static final String TAG = MainActivity.class.getSimpleName();
    private SMSReceiver smsReceiver;
    AppSignatureHashHelper appSignatureHashHelper;
    EditText  v1,v2,v3,v4,v5,v6;
    String o1,o2,o3,o4,o5,o6;
    String id;
    ProgressBar progressBar;
    DotProgressBar dotProgressBar;
    CardView go_now_otp;
    TextView linear_resend;
    String otp;
    String user_id,name,email,mobile,general;
    KProgressHUD hud;

    CardView card_view;
    private static final int PERMISSION_REQUEST_CODE = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        go_now=findViewById(R.id.go_now);
        phone_number=findViewById(R.id.phone_number);
        card_view=findViewById(R.id.card_view);
        phone_number_data=phone_number.getText().toString();

        appSignatureHashHelper = new AppSignatureHashHelper(this);

        // This code requires one time to get Hash keys do comment and share key
        Log.i(TAG, "HashKey: " + appSignatureHashHelper.getAppSignatures().get(0));
        Toast.makeText(LoginActivity.this, ""+ appSignatureHashHelper.getAppSignatures().get(0), Toast.LENGTH_SHORT).show();
        System.out.println("Message  -->"+ appSignatureHashHelper.getAppSignatures().get(0));




        phone_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                Toast.makeText(LoginActivity.this, "before text changed", Toast.LENGTH_SHORT).show();
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) card_view.getLayoutParams();

                layoutParams.setMargins(80, 50, 80, 100);
                card_view.requestLayout();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) card_view.getLayoutParams();

                layoutParams.setMargins(80, 50, 80, 100);
                card_view.requestLayout();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(phone_number.getText().toString().length()>=10){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(phone_number.getWindowToken(), 0);

                    ViewGroup.MarginLayoutParams layoutParams =
                            (ViewGroup.MarginLayoutParams) card_view.getLayoutParams();

                    layoutParams.setMargins(80, 50, 80, 100);
                    card_view.requestLayout();


                }
                else
                {

                }
            }
        });



        go_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phone_number.getText().toString().length()>=10){

                    hud = KProgressHUD.create(LoginActivity.this)
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setWindowColor(getResources().getColor(R.color.colorAccent))
                            .setAnimationSpeed(1)
                            .show();


                    DifferentDrandItemjsondata();


                }else {
                    Toast.makeText(LoginActivity.this, " Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
                }

            }
        });



        checkPermission();
        requestPermission();
    }


    public void Resend() {



        RequestQueue rq = Volley.newRequestQueue(LoginActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://quizabcd.com/index.php/api/registration",

                new Response.Listener<String>() {

                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            System.out.println("response_mobile_number"+response);

                            Toast.makeText(LoginActivity.this, ""+response, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    };
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {


                Toast.makeText(LoginActivity.this, ""+arg0, Toast.LENGTH_SHORT).show();
// TODO Auto-generated method stub
// pd.hide();
            }
        })

        {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile",phone_number.getText().toString());
                params.put("otp_id",appSignatureHashHelper.getAppSignatures().get(0));

                return params;
            }
        };
        rq.add(stringRequest);
    }





    public void DifferentDrandItemjsondata() {



        RequestQueue rq = Volley.newRequestQueue(LoginActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://quizabcd.com/index.php/api/registration",

                new Response.Listener<String>() {

                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            System.out.println("response_mobile_number"+response);
                            String Profile_status=""+jsonObject.getString("Profile_status");
                            String User_status=""+jsonObject.get("User_status");
                            String message=jsonObject.getString("message");
                            id=""+jsonObject.getString("id");


                            System.out.println("response_mobile_number"+message);

                            if(message.equals("OTP Sent To Your Mobile Number"))
                            {
                                hud.dismiss();


                                //
                                SharedPreferences sharedPreferences=getSharedPreferences("LoginDetails",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString("Profile_status",Profile_status);
                                editor.putString("User_status",User_status);
                                editor.putString("User_status",User_status);
                                editor.commit();
                                editor.apply();




                                dialog12=new Dialog(LoginActivity.this);
                                dialog12.setContentView(R.layout.activity_verify_otp);
                                dialog12.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog12.setCanceledOnTouchOutside(false);
                                dialog12.show();


                                go_now_otp=dialog12.findViewById(R.id.go_now_otp);
                                linear_resend=dialog12.findViewById(R.id.linear_resend);
                                v1=dialog12.findViewById(R.id.v1);
                                v2=dialog12.findViewById(R.id.v2);
                                v3=dialog12.findViewById(R.id.v3);
                                v4=dialog12.findViewById(R.id.v4);
                                v5=dialog12.findViewById(R.id.v5);
                                v6=dialog12.findViewById(R.id.v6);
                                dotProgressBar=dialog12.findViewById(R.id.dot_progress_bar);
                                dotProgressBar.setVisibility(View.GONE);
                                new DotProgressBarBuilder(LoginActivity.this)
                                        .setDotAmount(3)
                                        .setStartColor(Color.BLACK)
                                        .setAnimationDirection(DotProgressBar.LEFT_DIRECTION)
                                        .build();
                                linear_resend.setClickable(false);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        linear_resend.setTextColor(getResources().getColor(R.color.black));
                                        linear_resend.setClickable(true);

                                    }
                                }, 10000);
                                linear_resend.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Resend();
                                    }
                                });
                                v1.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        if ( v1.getText().toString().length() == 1) //size as per your requirement
                                        {
                                            o1= v1.getText().toString();
                                            v2.requestFocus();
                                        }

                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });
                                v2.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        if (v2.getText().toString().length() == 1) //size as per your requirement
                                        {
                                            o2=v2.getText().toString();
                                            v3.requestFocus();
                                        }

                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });
                                v3.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        if (v3.getText().toString().length() == 1) //size as per your requirement
                                        {
                                            o3=v3.getText().toString();
                                            v4.requestFocus();
                                        }

                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });
                                v4.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        if (v4.getText().toString().length() == 1) //size as per your requirement
                                        {
                                            o4=v4.getText().toString();

                                            v5.requestFocus();

                                        }

                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });
                                v5.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        if (v5.getText().toString().length() == 1) //size as per your requirement
                                        {
                                            o5=v5.getText().toString();
                                            v6.requestFocus();

                                        }

                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });
                                v6.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        if (v6.getText().toString().length() == 1) //size as per your requirement
                                        {
                                            o6=v6.getText().toString();
//et5.requestFocus();

                                        }

                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });

                                startSMSListener();

                                go_now_otp.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        go_now_otp.setBackgroundColor(Color.parseColor("#AEA8D7F3"));
                                        otp=o1+o2+o3+o4+o5+o6;
                                        OtpVerification(otp,id);

                                    }
                                });
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    };
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {


                Toast.makeText(LoginActivity.this, ""+arg0, Toast.LENGTH_SHORT).show();
// TODO Auto-generated method stub
// pd.hide();
            }
        })

        {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile",phone_number.getText().toString());
                params.put("otp_id",appSignatureHashHelper.getAppSignatures().get(0));

                return params;
            }
        };
        rq.add(stringRequest);
    }


    @Override
    public void onOTPReceived(String otp) {
        showToast("OTP Received: " + otp);
        // <#> Your QUIZ verification code is 697843,Message ID: JM5O/u2UX5k

        String str = otp;
        String substr = "";

        // prints the substring after index 7 till index 17
        substr = str.substring(35, 41);
        v1.setText(Character.toString(substr.charAt(0)));//substr.charAt(0));
        v2.setText(Character.toString(substr.charAt(1)));
        v3.setText(Character.toString(substr.charAt(2)));
        v4.setText(Character.toString(substr.charAt(3)));
        v5.setText(Character.toString(substr.charAt(4)));
        v6.setText(Character.toString(substr.charAt(5)));

        String total=v1.getText().toString()+v2.getText().toString()+v3.getText().toString()+v4.getText().toString()+v5.getText().toString()+v6.getText().toString();

        OtpVerification(total,id);

        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
            smsReceiver = null;
        }
    }

    @Override
    public void onOTPTimeOut() {
        showToast("OTP Time out");
    }

    @Override
    public void onOTPReceivedError(String error) {
        showToast(error);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
        }
    }
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    private void startSMSListener() {
        try {
            smsReceiver = new SMSReceiver();
            smsReceiver.setOTPListener(this);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
            this.registerReceiver(smsReceiver, intentFilter);

            SmsRetrieverClient client = SmsRetriever.getClient(this);

            Task<Void> task = client.startSmsRetriever();
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // API successfully started
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Fail to start API
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean checkPermission() {


        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, CAMERA}, PERMISSION_REQUEST_CODE);

    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted)
                        Toast.makeText(this, "Permission Granted, Now you can access location data and camera.", Toast.LENGTH_SHORT).show();

                    else {
                        // Toast.makeText(this, "Permission Denied, You cannot access location data and camera.", Toast.LENGTH_SHORT).show();



                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LoginActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    public void OtpVerification(final String otp, final String id){
        go_now_otp.setVisibility(View.GONE);
        dotProgressBar.setVisibility(View.VISIBLE);


        RequestQueue rq = Volley.newRequestQueue(LoginActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://quizabcd.com/index.php/api/verifyOTP",
                new Response.Listener<String>() {

                    public void onResponse(String response) {
                        System.out.println("mouni_response"+response);


                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            System.out.println("response_0123"+response);
                            String status=jsonObject.getString("status");
                            dotProgressBar.setVisibility(View.GONE);
                            String user_details=jsonObject.getString("user_details");
                            JSONArray jsonArray=new JSONArray(user_details);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                                user_id=jsonObject1.getString("user_id");
                                name=jsonObject1.getString("name");
                                email=jsonObject1.getString("email");
                                mobile=jsonObject1.getString("mobile");
                                general=jsonObject1.getString("general");
                            }
                            if(status.equals("200")){

                                SharedPreferences sharedPreferences=getSharedPreferences("LoginDetails",Context.MODE_PRIVATE);
                                String Profile_status_string=sharedPreferences.getString("Profile_status",null);
                                String User_status_string=sharedPreferences.getString("User_status",null);


                                System.out.println("User_status_string"+User_status_string);
                                if(Profile_status_string.equals("1"))
                                {
                                    SharedPreferences sharedPreferences1=getSharedPreferences("UserDetails",Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor=sharedPreferences1.edit();
                                    editor.putString("user_id",user_id);
                                    editor.putString("name",name);
                                    editor.putString("mail",email);
                                    editor.putString("mobile",mobile);
                                    editor.apply();
                                    editor.commit();


                                }
                                else if(Profile_status_string.equals("2"))
                                {
                                    SharedPreferences sharedPreferences1=getSharedPreferences("UserDetails",Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor=sharedPreferences1.edit();
                                    editor.putString("user_id",user_id);
                                    editor.putString("name",name);
                                    editor.putString("mail",email);
                                    editor.putString("mobile",mobile);
                                    editor.apply();
                                    editor.commit();

                                }

                            }else {
                                dotProgressBar.setVisibility(View.GONE);
                                go_now_otp.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    };
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {


// TODO Auto-generated method stub
// pd.hide();
            }
        })

        {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id",id);
                params.put("otp",otp);

                return params;
            }
        };
        rq.add(stringRequest);
    }
}