package com.darkspede.smsforward.smsforward;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    Button sendBtn;
    Button displayBtn;
    Button resetBtn;
    EditText txtphoneNo;
    static TextView info;
    static TextView displayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtphoneNo = (EditText) findViewById(R.id.phone_input);
        txtphoneNo = (EditText) findViewById(R.id.phone_input);
        sendBtn = (Button) findViewById(R.id.forward_button);
        displayBtn = (Button) findViewById(R.id.showlist_button);
        resetBtn = (Button) findViewById(R.id.reset_button);
        info = (TextView) findViewById(R.id.info_text);
        displayTextView = (TextView) findViewById(R.id.display_text);

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

    }

    public void AddPhoneToList(String _newPhone) {
        Log.d("sven", "MainActivity.start AddPhoneToList");

        if (!PhoneList.contains(_newPhone)) {
            try {
                PhoneList.add(_newPhone);
                SetInfoMessage("New number " + _newPhone + " added");
                SmsForward.sendSMSMessage("Message Forward Service Added");
                Log.d("sven", "MainActivity. Forward Service Added");

                ListToString();
                StoreInfo(DisplayMessage);
                Log.d("sven", "MainActivity.Store Data message successful");
            } catch (Exception e) {
                SetInfoMessage(e.getMessage());
            }


        } else {
            SetInfoMessage("Number " + _newPhone + " exits.");
            Log.d("sven", "MainActivity. phone number exited");
        }
    }


    public static  void sendSMSMessage(String _message) {
        try {
            for (int i = 0; i < PhoneList.size(); i++) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(PhoneList.get(i), null, "[Forward] " + _message, null, null);
                SetInfoMessage("Forward Message to " + PhoneList.get(i) + " added");
                Log.d("sven", "MainActivity. message sent");
            }
        }catch ( Exception e){
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
        }catch ( Exception e){
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

    public List<String> getPhoneList(){
        return PhoneList;
    }

}