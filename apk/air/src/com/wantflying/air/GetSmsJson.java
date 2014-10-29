package com.wantflying.air;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.wantflying.server.NanoWebSocketServer;

public class GetSmsJson {
    static Context mcontext;
    public final String SMS_URI_ALL = "content://sms/";
    public final String SMS_URI_INBOX = "content://sms/inbox";
    public final String SMS_URI_SEND = "content://sms/sent";
    public final String SMS_URI_DRAFT = "content://sms/draft";
    public final String SMS_URI_OUTBOX = "content://sms/outbox";
    public final String SMS_URI_FAILED = "content://sms/failed";
    public final String SMS_URI_QUEUED = "content://sms/queued";
    
    PendingIntent sentPI;
    PendingIntent deliverPI;
    String id;
    
    public GetSmsJson(Context context){
    	mcontext = context;
    	//---------------------------------------------------------------------------------���ŷ���״̬
    	String SENT_SMS_ACTION = "SENT_SMS_ACTION";
    	Intent sentIntent = new Intent(SENT_SMS_ACTION);
    	sentPI = PendingIntent.getBroadcast(mcontext, 0, sentIntent,0);
    	// register the Broadcast Receivers
    	mcontext.registerReceiver(new BroadcastReceiver() {
    	    @Override
    	    public void onReceive(Context _context, Intent _intent) {
    	        switch (getResultCode()) {
    	        case Activity.RESULT_OK:
    				NanoWebSocketServer.userList.sendToAll("{\"type\":\"sms\",\"data\":{\"mes\":\"sended\",\"id\":\""+id+"\"}}");
    	            Toast.makeText(mcontext,"���ŷ��ͳɹ�", Toast.LENGTH_SHORT).show();
    	        break;
    	        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
    	        break;
    	        case SmsManager.RESULT_ERROR_RADIO_OFF:
    	        break;
    	        case SmsManager.RESULT_ERROR_NULL_PDU:
    	        break;
    	        }
    	    }
    	}, new IntentFilter(SENT_SMS_ACTION));

    	//---------------------------------------------------------------------------------���Ž���״̬
    	String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
    	// create the deilverIntent parameter
    	Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
    	deliverPI = PendingIntent.getBroadcast(mcontext, 0,
    	       deliverIntent, 0);
    	mcontext.registerReceiver(new BroadcastReceiver() {
    	   @Override
    	   public void onReceive(Context _context, Intent _intent) {
				NanoWebSocketServer.userList.sendToAll("{\"type\":\"sms\",\"data\":{\"mes\":\"received\",\"id\":\""+id+"\"}}");
    	       Toast.makeText(mcontext, "�������Ѿ��ɹ�����", Toast.LENGTH_SHORT).show();
    	   }
    	}, new IntentFilter(DELIVERED_SMS_ACTION));
    }
    public String getSmsGroups() {
		List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();
		String jsonresult = "{}";
        try {  
            Uri uri = Uri.parse(SMS_URI_ALL);
            String[] projection = new String[] {"thread_id", "address", "body", "date","COUNT(*)" };
            Cursor cur = mcontext.getContentResolver().query(uri, projection, "deleted = 0) GROUP BY (thread_id", null, "date desc");
  
            if (cur.moveToFirst()) {  
                int index_Address = cur.getColumnIndex("address");  
                int index_thread_id = cur.getColumnIndex("thread_id");  
                int index_Body = cur.getColumnIndex("body");  
                int index_Date = cur.getColumnIndex("date");  
  
                do {  
                    String strAddress = cur.getString(index_Address);  
                    String thread_id = cur.getString(index_thread_id);  
                    String strbody = cur.getString(index_Body);  
                    String strDate = cur.getString(index_Date);
                    String count = cur.getString(4);
                    String name="";
    		        String photoid="-1";
    		        
                    Uri personUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, strAddress);  
                    Cursor cur1 = mcontext.getContentResolver().query(personUri,new String[] {ContactsContract.Data.PHOTO_ID, PhoneLookup.DISPLAY_NAME },null, null, null );  
                    if( cur1.moveToFirst() ) {   
  			          photoid = cur1.getString(0);
  			          name = cur1.getString(1);
                    }  
                    cur1.close();
                    
    				Map<String, Object> listem = new HashMap<String, Object>();
    		        listem.put("strAddress",strAddress);
    		        listem.put("thread_id",thread_id);
    		        listem.put("strbody",strbody);
    		        listem.put("strDate",strDate);
    		        listem.put("name",name);
    		        listem.put("count",count);
    		        listem.put("photoid",photoid);
    		        listems.add(listem);
                } while (cur.moveToNext());
                if (!cur.isClosed()) {  
                    cur.close();  
                    cur = null;  
                }
            } else {  
                return jsonresult;  
            } // end if  
            JSONObject object = new JSONObject();
            try {  
                JSONArray jsonarray = new JSONArray();
        		for (Map<String, Object> m : listems) {
                    JSONObject jsonObj = new JSONObject();
        		    for (String k : m.keySet()) {
        		    	jsonObj.put(k, m.get(k));
        		    }
                    jsonarray.put(jsonObj);
        		}
                object.put("sms", jsonarray);
                jsonresult = object.toString();
            } catch (JSONException e) {
                e.printStackTrace();  
            }  
  
        } catch (SQLiteException ex) {  
        }  
        return jsonresult;  
  
    }
    public String getSmsfromthreadid(String threadid,String limit,String offset) {
		List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();
		String jsonresult = "{}";
        try {  
            Uri uri = Uri.parse(SMS_URI_ALL);
            String[] projection = new String[] {"body", "date","type" };
            Cursor cur = mcontext.getContentResolver().query(uri, projection, "deleted = 0 AND thread_id = "+threadid, null, "date desc limit "+limit+" offset "+offset);
  
            if (cur.moveToFirst()) {  
                int index_Body = cur.getColumnIndex("body");  
                int index_Date = cur.getColumnIndex("date");
                int index_type = cur.getColumnIndex("type");
  
                do {  
                    String type = cur.getString(index_type);  
                    String strbody = cur.getString(index_Body);  
                    String strDate = cur.getString(index_Date);
                    
    				Map<String, Object> listem = new HashMap<String, Object>();
    		        listem.put("strbody",strbody);
    		        listem.put("strDate",strDate);
    		        listem.put("type",type);
    		        listems.add(listem);
                } while (cur.moveToNext());
                if (!cur.isClosed()) {  
                    cur.close();  
                    cur = null;  
                }
            } else {  
                return jsonresult;  
            } // end if  
            JSONObject object = new JSONObject();
            try {  
                JSONArray jsonarray = new JSONArray();
        		for (Map<String, Object> m : listems) {
                    JSONObject jsonObj = new JSONObject();
        		    for (String k : m.keySet()) {
        		    	jsonObj.put(k, m.get(k));
        		    }
                    jsonarray.put(jsonObj);
        		}
                object.put("sms", jsonarray);
                jsonresult = object.toString();
            } catch (JSONException e) {
                e.printStackTrace();  
            }  
  
        } catch (SQLiteException ex) {  
        }  
        return jsonresult;  
  
    }
    public String sendSMS(String number,String content,String _id){
    	//---------------------------------------------------------------------------------���ŷ���
    	id = _id;
    	SmsManager smsManager = SmsManager.getDefault();
    	List<String> divideContents = smsManager.divideMessage(content);
    	for (String text : divideContents) {
    	    smsManager.sendTextMessage(number, null, text, sentPI, deliverPI);
    	}
    	ContentValues values = new ContentValues();  
        //����ʱ��  
    	values.put("date", System.currentTimeMillis());
        //�Ķ�״̬              
       values.put("read", 0);
        //1Ϊ�� 2Ϊ��             
      values.put("type", 2);
        //�ʹ����              
      values.put("address",number);
        //�ʹ�����            
      values.put("body", content);
        //������ſ�    
     
      mcontext.getContentResolver().insert(Uri.parse("content://sms/sent"), values); 
    	return "{\"status\":\"ok\"}";//���ﷵ��number��id
    }

}
