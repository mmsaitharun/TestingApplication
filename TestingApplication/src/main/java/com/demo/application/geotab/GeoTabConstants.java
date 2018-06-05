package com.demo.application.geotab;

public interface GeoTabConstants {

	Double EARTH_RADIUS_IN_MILES = 3959.00;
	Double EARTH_RADIUS_IN_METRE = 6371000.00;
	Double EARTH_RADIUS_IN_KILOMETRE = 6371.00;
	

	
	String TYPE_NAME_DEVICESTATUSINFO = "DeviceStatusInfo";
	String TYPE_NAME_DEVICE_SEARCH = "Device";
//	String TYPE_NAME_USER_SEARCH = "User";
	
	String GEO_TAB_METHOD_GET = "Get";
	String GEO_TAB_METHOD_POST = "Post";
	
	String CODE_FAILURE = "1";
	String CODE_SUCCESS = "0";
	
	String SUCCESS = "SUCCESS";
	String FAILURE = "FAILURE";
	
	String LOC_FIELD = "FIELD";
	String LOC_CF = "FACILITY";
	String LOC_WELLPAD = "WELLPAD";
	String LOC_WELL = "WELL";
	
	/* Geo Tab technical user credentials*/
	String GEO_TAB_BASE_URL = "https://my87.geotab.com/apiv1";
	
	String GEO_TAB_USER_ID = "SVC_IOP@murphyoilcorp.com";
	String GEO_TAB_USER_PASS = "$tr3amlin3";
	String GEO_TAB_DATABASE = "murphy_oil";
	
	Double GEO_TAB_AUTO_SIGNIN_DISTANCE_METRE = 200.0; 
	Double GEO_TAB_AUTO_SIGNIN_DISTANCE = 0.000621371 * GEO_TAB_AUTO_SIGNIN_DISTANCE_METRE; // 200 met = 0.124274 miles
	
	String GEO_TAB_TIME_DAY = "day";
	String GEO_TAB_TIME_HOUR = "hour";
	String GEO_TAB_TIME_MINUTE = "minute";
	String GEO_TAB_TIME_SECOND = "second";
	
	String IOP_PROPERTY_FILE_LOCATION = "application.properties";
	
	String ARCGIS_SERVICE_URL = "https://route.arcgis.com/arcgis/rest/services/World/Route/NAServer/Route_World/solve";
	
	String ARCGIS_SERVICE_TOKEN_URL = "https://www.arcgis.com/sharing/rest/oauth2/token";
	String ARCGIS_CLIENT_ID = "0STGycHh2BgNDnkq";
	String ARCGIS_CLIENT_SECRET = "1be4dafb7f0d4d67a9e8ee03325f7c2c";
	
	String CONTENT_TYPE_URL_FORM_ENCODED = "application/x-www-form-urlencoded";
	
	
	
}
