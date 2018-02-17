package com.darkspede.smsforward.smsforward;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Darkspede-002 on 2017/11/28.
 */

public class SmsForward extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private MainActivity mActivity;
    public static List<String> phoneList = new ArrayList<String>();

    //incmoing phone call
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;  //because the passed incoming is only valid in ringing

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(SMS_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
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
                sendSMSMessage(message);

                // prevent any other broadcast receivers from receiving broadcast
                // abortBroadcast();
            }
        }

        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        if (intent.getAction().equals("android.intent.action.PHONE_STATE") && lastState == 0) {

            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            savedNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.d("sven", "SmsForward. get incoming phone call: " + savedNumber);

            String message = "[Get incoming phone call]: " + savedNumber;
            MainActivity.SetInfoMessage("[Get incoming phone call]: " + message);
            sendSMSMessage(message);

        }
//        else {
//            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
//            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
//            int state = 0;
//            if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
//                state = TelephonyManager.CALL_STATE_IDLE;
//            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
//                state = TelephonyManager.CALL_STATE_OFFHOOK;
//            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
//                state = TelephonyManager.CALL_STATE_RINGING;
//            }
//
//
//            onCallStateChanged(context, state, number);
//        }
    }

    public static void sendSMSMessage(String _message) {
        MainActivity mainActivity = new MainActivity();
        phoneList = mainActivity.getPhoneList();
        try {
            for (int i = 0; i < phoneList.size(); i++) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneList.get(i), null, "[Forward] " + _message, null, null);
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