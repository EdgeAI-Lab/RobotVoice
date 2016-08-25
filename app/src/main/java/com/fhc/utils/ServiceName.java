package com.fhc.utils;

public enum ServiceName {
	
	baike,calc,music,schedule,translation,weather,openQA,datetime,faq,chat,CALL,message,story,noService;
	
	public static ServiceName getServiceName(String serviceName){

		
		ServiceName mServiceName = noService;
		try {
			mServiceName = valueOf(serviceName);
		} catch (IllegalArgumentException e) {
			return mServiceName;
		}catch (NullPointerException e) {
			return mServiceName;
		}
		
		return mServiceName;
	}
}
