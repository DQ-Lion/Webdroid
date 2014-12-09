package com.wantflying.air;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.wantflying.server.NanoWebSocketServer;

public class SmsReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		StringBuilder sb = new StringBuilder();
		Bundle bundle = arg1.getExtras();
		if(bundle!=null){
			Object[] pdus = (Object[])bundle.get("pdus");
			SmsMessage[] msg = new SmsMessage[pdus.length];
			for(int i = 0 ;i<pdus.length;i++){
				msg[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
			}
			for(SmsMessage curMsg:msg){
				String sender = curMsg.getDisplayOriginatingAddress();
				sb.append("You got the message From:��");
				sb.append(sender);
				sb.append("��Content��");
				sb.append(curMsg.getDisplayMessageBody());
	        	NanoWebSocketServer.userList.sendToAll("{\"type\":\"smsR\",\"data\":{\"sendtime\":\""+curMsg.getTimestampMillis()+"\",\"sender\":\""+sender+"\",\"content\":\""+curMsg.getDisplayMessageBody()+"\"}}");
			}	
		}
	}

}
