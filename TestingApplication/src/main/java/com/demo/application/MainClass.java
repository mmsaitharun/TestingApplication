package com.demo.application;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainClass {

	public static void main(String[] args) {
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date currentDateTime = new Date();

		
//		dateDifference = currentDateTime.getTime() - dateFormat.parse(deviceInfo.getDateTime()).getTime();
//		seperateDateTime = GeoTabUtil.seperateDateTime(deviceInfo.getCurrentStateDuration());
//		finalDateTime = dateDifference + seperateDateTime;
//		durationBreakdown = GeoTabUtil.getDurationBreakdown(finalDateTime);
//		deviceInfo.setParkedDateTime(dateFormat.format(GeoTabUtil.getParkedTime(durationBreakdown)));
	}
}
