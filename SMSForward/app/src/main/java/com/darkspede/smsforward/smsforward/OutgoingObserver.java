//package com.darkspede.smsforward.smsforward;
//
//import android.content.Context;
//import android.database.ContentObserver;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Handler;
//
//public class OutgoingObserver extends ContentObserver {
//    private String lastSmsId;
//    public Context mContext;
//
//    public OutgoingObserver(Handler handler, Context c) {
//        super(handler);
//        mContext = c;
//    }
//
//    @Override
//    public void onChange(boolean selfChange) {
//        super.onChange(selfChange);
//        Uri uriSMSURI = Uri.parse("content://sms/sent");
//        Cursor cur = mContext.getContentResolver().query(uriSMSURI, null, null, null, null);
//        cur.moveToNext();
//        String id = cur.getString(cur.getColumnIndex("_id"));
//        if (smsChecker(id)) {
//            String address = cur.getString(cur.getColumnIndex("address"));
//            // Optional: Check for a specific sender
//            if (address.equals("phoneNumber")) {
//                String message = cur.getString(cur.getColumnIndex("body"));
//                // Use message content for desired functionality
//            }
//        }
//    }
//
//    // Prevent duplicate results without overlooking legitimate duplicates
//    public boolean smsChecker(String smsId) {
//        boolean flagSMS = true;
//
//        if (smsId.equals(lastSmsId)) {
//            flagSMS = false;
//        }
//        else {
//            lastSmsId = smsId;
//        }
//
//        return flagSMS;
//    }
//}