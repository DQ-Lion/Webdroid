package com.wantflying.air;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

public class GetContactsJson {
    static Context mcontext;
	private static List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();
	private static List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
	
    public GetContactsJson(Context context) {
		mcontext=context;
		getPhoneContacts();
		getContactsGroups();
    }
	public String getContacts(){
		String jsonresult = "";
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
            JSONArray jsonarray2 = new JSONArray();

    		for (Map<String, Object> m : groups) {
                JSONObject jsonObj = new JSONObject();
    		    for (String k : m.keySet()) {
    		    	jsonObj.put(k, m.get(k));
    		    }
                jsonarray2.put(jsonObj);
    		}

            object.put("status", jsonarray);//���ܶ���������Ӱ���pet������  
            object.put("groups", jsonarray2);//���ܶ���������Ӱ���pet������  
            jsonresult = object.toString();//���ɷ����ַ���  
        } catch (JSONException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        return jsonresult;  
	};
    public void getPhoneContacts(){
        //�õ�ContentResolver����
        ContentResolver cr = mcontext.getContentResolver();
        //ȡ�õ绰���п�ʼһ��Ĺ��
        
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, getSortOrder(ContactsContract.Contacts.DISPLAY_NAME));
        //�����ƶ����
        while(cursor.moveToNext()){
            //ȡ����ϵ������
            String contact = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
            String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Long PhotoId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
            
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
            String Number="";
            while(phone.moveToNext()){
                Number += phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))+";";
            }
			Map<String, Object> listem = new HashMap<String, Object>();
            listem.put("id", ContactId);
            listem.put("head", PhotoId);
            listem.put("name",contact); 
            listem.put("desc", Number);
            listems.add(listem); 
            phone.close();
        }
        cursor.close();
    }
    protected static String getSortOrder(String fieldName) {  
        //COLLATE��Ҫ���ڶ��ַ�������  
        //COLLATE LOCALIZED ���������Խ�������  
        return "CASE WHEN substr(UPPER(" + fieldName + "), 1, 1) BETWEEN 'A' AND 'Z' THEN 1 else 10 END," +  
                fieldName + " COLLATE LOCALIZED ASC";  
    }

	public InputStream contactHead(Long PhotoId)  {
        ContentResolver cr = mcontext.getApplicationContext().getContentResolver();
        String selection = ContactsContract.Data._ID + " = " + PhotoId;
        String[] projection = new String[] { ContactsContract.Data.DATA15 };
        Cursor cur = cr.query(ContactsContract.Data.CONTENT_URI, projection, selection, null, null);
        cur.moveToFirst();
        byte[] contactIcon = cur.getBlob(0);
        cur.close();
        InputStream is = new ByteArrayInputStream(contactIcon);
		return is;
	}
	public String getPhones(){

		List<Map<String, Object>> listems2 = new ArrayList<Map<String, Object>>();
        ContentResolver cr = mcontext.getApplicationContext().getContentResolver();
		Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI,null, null, null, null);
		if(cursor.moveToFirst()){
		    do{
		        String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
		        String type;
		        switch (Integer.parseInt(cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE)))) {
		        case CallLog.Calls.INCOMING_TYPE:
		            type = "INCOMING";
		            break;
		        case CallLog.Calls.OUTGOING_TYPE:
		            type = "OUTGOING";
		            break;
		        case CallLog.Calls.MISSED_TYPE:
		            type = "MISSED";
		            break;
		        default:
		            type = "NEW";
		            break;
		        }
		        Date date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE))));
		        String time = GetFileJson.time_n.format(date);
		        String name = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME));
		        String duration = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION));
		        
		        String photoid="-1";
		        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/"+number);
		        Cursor cursor1 = cr.query(uri, new String[]{ContactsContract.Data.PHOTO_ID,PhoneLookup.DISPLAY_NAME}, null, null, null);	
		        if(cursor1.moveToFirst()){
			          photoid = cursor1.getString(0);
			          name = cursor1.getString(1);
		        }
		        cursor1.close();
		         
		         
				Map<String, Object> listem = new HashMap<String, Object>();
	            listem.put("name", name);
	            listem.put("photoid", photoid);
	            listem.put("time", time);
	            listem.put("type",type); 
	            listem.put("number", number);
	            listem.put("duration", duration);
	            listems2.add(listem); 
		    }while(cursor.moveToNext());    
		    cursor.close();
            
		                                                                                                             
		}
		
		System.out.println(listems2);
		
		String jsonresult = "";
        JSONObject object = new JSONObject();
        try {  
            JSONArray jsonarray = new JSONArray();
    		for (Map<String, Object> m : listems2) {
                JSONObject jsonObj = new JSONObject();
    		    for (String k : m.keySet()) {
    		    	jsonObj.put(k, m.get(k));
    		    }
                jsonarray.put(jsonObj);
    		}
            object.put("phones", jsonarray);//���ܶ���������Ӱ���pet������  
            jsonresult = object.toString();//���ɷ����ַ���  
        } catch (JSONException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        return jsonresult;  
	};
    // ��ȡ��ϵ�˷���
    public void getContactsGroups() {
    	Cursor cursorAll = mcontext.getContentResolver().query(ContactsContract.Groups.CONTENT_URI,null,ContactsContract.Groups.GROUP_VISIBLE + "=1 and "+ContactsContract.Groups.GROUP_IS_READ_ONLY+"=0",null,null);  
    	for (cursorAll.moveToFirst();!(cursorAll.isAfterLast());cursorAll.moveToNext()) {  
    	//������
    		int id = cursorAll.getInt(cursorAll.getColumnIndex(ContactsContract.Groups._ID));
			Map<String, Object> listem = new HashMap<String, Object>();
           listem.put("id",String.valueOf(id) );
           listem.put("name",cursorAll.getString(cursorAll.getColumnIndex(ContactsContract.Groups.TITLE))); 
           listem.put("phones",getAllContactsByGroupId(id)); 
           groups.add(listem); 
    	//��id��
    		//out+="-"+cursorAll.getLong(cursorAll.getColumnIndex(ContactsContract.Groups.))+"\n";
    	}
    	cursorAll.close();
    	  
    	//�½��飨����Ϊname����  
    	//    ContentValues values = new ContentValues();  
    	//   values.put(Groups.NAME, "�½���");  
    	//    getContentResolver().inser(Groups.CONTENT_URI, values);  
    	  
    	//ɾ����(IdΪgroupId):  
    	//    getContentResolver().delete(Uri.parse(Groups.CONTENT_URI +"?" +ContactsContract.CALLER_IS_SYNCADAPTER + "=true"),Groups._ID+"="+groupId,null);  
    	  
    	//����������(oldName;newName;groupId):  
    	//Uri uri = ContentUris.withAppendedId(Groups.CONTENT_URI, groupId);  
    	//ContentValues values = new ContentValues();  
    	//values.put(Groups.TITLE,newName);  
    	//getContentResolver().update(uri,values,null,null);  
    	  
    	//������ӳ�Ա(groupId,personId):  
    	//ContentValues values = new ContentValues();        
    	//values.put(ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID,personId);        
    	//values.put(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID,groupId);        
    	//values.put(ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE,ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE);          
    	//getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);  
    	  
    	//�����Ƴ���Ա(groupId,personId):  
    	//getContentResolver().delete(ContactsContract.Data.CONTENT_URI,ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID + "=? and " +ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID + "=? and " +ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE + "=?",new String[]{"" + personId,"" + groupId,ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE});  
    }
    /**
	 * ��ȡĳ�������µ� ������ϵ����Ϣ
	 * ˼·��ͨ�����id ȥ��ѯ RAW_CONTACT_ID, ͨ��RAW_CONTACT_IDȥ��ѯ��ϵ��
		Ҫ��ѯ�õ� data���Data.RAW_CONTACT_ID�ֶ�
	 * @param groupId
	 * @return
	 */
	public String getAllContactsByGroupId(int groupId) {
		
		String[] RAW_PROJECTION = new String[] { ContactsContract.Data.CONTACT_ID, };

		String RAW_CONTACTS_WHERE = ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID
				+ "=?"
				+ " and "
				+ ContactsContract.Data.MIMETYPE
				+ "="
				+ "'"
				+ ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE
				+ "'";

		// ͨ�������id ��ѯ�õ�RAW_CONTACT_ID
		Cursor cursor = mcontext.getContentResolver().query(
				ContactsContract.Data.CONTENT_URI, RAW_PROJECTION,
				RAW_CONTACTS_WHERE, new String[] { groupId + "" }, "data1 asc");
String out="";

		while (cursor.moveToNext()) {
			// RAW_CONTACT_ID
			int raw_contact_id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID));

			// Log.i("getAllContactsByGroupId", "raw_contact_id:" +
			// raw_contact_id);


			out+=String.valueOf(raw_contact_id);
/*
			Uri dataUri = Uri.parse("content://com.android.contacts/data");
			Cursor dataCursor = mcontext.getContentResolver().query(dataUri,
					null, "raw_contact_id=?",
					new String[] { raw_contact_id + "" }, null);

			while (dataCursor.moveToNext()) {
				String data1 = dataCursor.getString(dataCursor
						.getColumnIndex("data1"));
				String mime = dataCursor.getString(dataCursor
						.getColumnIndex("mimetype"));

				if ("vnd.android.cursor.item/phone_v2".equals(mime)) {
					out+=data1;
				} else if ("vnd.android.cursor.item/name".equals(mime)) {
					out+=data1;
				}
			}

			dataCursor.close();*/
			out+="-";
		}

		cursor.close();

		return out;
	}
}
