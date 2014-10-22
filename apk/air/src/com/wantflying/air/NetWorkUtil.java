package com.wantflying.air;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkUtil {
	   public static final String NET_TYPE_WIFI = "WIFI"; 
	    public static final String NET_TYPE_MOBILE = "MOBILE"; 
	    public static final String NET_TYPE_NO_NETWORK = "no_network"; 
	     
	    private Context mContext = null; 
	    private ConnectivityManager connectivityManager;
	    private NetworkInfo networkInfo ;
	     

		public NetWorkUtil(Context pContext) { 
	        this.mContext = pContext; 
	        connectivityManager = (ConnectivityManager) this.mContext.getSystemService(Context.CONNECTIVITY_SERVICE); 
	        networkInfo = connectivityManager.getActiveNetworkInfo(); 
	    } 
	     
	    public static final String IP_DEFAULT = "0.0.0.0"; 
	 
	    public static boolean isConnectInternet(final Context pContext) 
	    { 
	        final ConnectivityManager conManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE); 
	        final NetworkInfo networkInfo = conManager.getActiveNetworkInfo(); 
	 
	        if (networkInfo != null) 
	        { 
	            return networkInfo.isAvailable(); 
	        } 
	 
	        return false; 
	    } 
	     
	    public static boolean isConnectWifi(final Context pContext) { 
	        ConnectivityManager mConnectivity = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE); 
	        NetworkInfo info = mConnectivity.getActiveNetworkInfo(); 
	        //�ж������������ͣ�ֻ����3G��wifi�����һЩ���ݸ��¡�    
	        int netType = -1; 
	        if(info != null){ 
	            netType = info.getType(); 
	        } 
	        if (netType == ConnectivityManager.TYPE_WIFI) { 
	            return info.isConnected(); 
	        } else { 
	            return false; 
	        } 
	    } 
	 
	    public static String getNetTypeNamefromid(final int pNetType) 
	    { 
	        switch (pNetType) 
	        { 
	            case 0: 
	                return "unknown"; 
	            case 1: 
	                return "GPRS"; 
	            case 2: 
	                return "EDGE"; 
	            case 3: 
	                return "UMTS"; 
	            case 4: 
	                return "CDMA: Either IS95A or IS95B"; 
	            case 5: 
	                return "EVDO revision 0"; 
	            case 6: 
	                return "EVDO revision A"; 
	            case 7: 
	                return "1xRTT"; 
	            case 8: 
	                return "HSDPA"; 
	            case 9: 
	                return "HSUPA"; 
	            case 10: 
	                return "HSPA"; 
	            case 11: 
	                return "iDen"; 
	            case 12: 
	                return "EVDO revision B"; 
	            case 13: 
	                return "LTE"; 
	            case 14: 
	                return "eHRPD"; 
	            case 15: 
	                return "HSPA+"; 
	            default: 
	                return "unknown"; 
	        } 
	    } 
	 
	    public static String getIPAddress() {
			try {
				for (Enumeration<NetworkInterface> en = NetworkInterface
						.getNetworkInterfaces(); en.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					for (Enumeration<InetAddress> enumIpAddr = intf
							.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress()) {
							return inetAddress.getHostAddress().toString();
						}
					}
				}
			} catch (SocketException ex) {
	            return "fault"; 
			}
            return "fault2"; 
		}

	    public String getConnTypeName() { 
	        if(networkInfo == null) { 
	            return NET_TYPE_NO_NETWORK; 
	        } else { 
	            return networkInfo.getTypeName(); 
	        } 
	    } 
	    public String getExtraInfo() { 
	        if(networkInfo == null) { 
	            return NET_TYPE_NO_NETWORK; 
	        } else { 
	            return networkInfo.getExtraInfo(); 
	        } 
	    } 
	    public String getNetTypeName() { 
	        if(networkInfo == null) { 
	            return NET_TYPE_NO_NETWORK; 
	        } else { 
	            return getNetTypeNamefromid(networkInfo.getSubtype()); 
	        } 
	    } 
	} 

