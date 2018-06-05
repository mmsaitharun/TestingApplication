package com.demo.application.geotab.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.demo.application.dao.GeoTabDao;
import com.demo.application.dto.DateTimeDto;
import com.demo.application.dto.DeviceStatusInfoDto;
import com.demo.application.dto.LocationHierarchyDto;
import com.demo.application.geotab.Coordinates;
import com.demo.application.geotab.Device;
import com.demo.application.geotab.GeoTabConstants;
import com.demo.application.geotab.HierarchyDto;
import com.demo.application.geotab.NearestUserDto;
import com.demo.application.geotab.NearestUserDtoResponse;
import com.demo.application.geotab.ResponseMessage;
import com.demo.application.geotab.UserNameDto;
import com.demo.application.util.JsonUtil;
import com.demo.application.util.ServicesUtil;
import com.demo.application.util.SpringContextBridge;

public class GeoTabUtil {

	private static final Logger logger = LoggerFactory.getLogger(GeoTabUtil.class);
	
//	@Autowired
//	private GeoTabDao geoTabDao;


	/*public Session getSession() {
		try {
			return sessionFactory.getCurrentSession();
		} catch (HibernateException e){
			e.printStackTrace();
			return sessionFactory.openSession();
		}

	}*/
	
	/**
	 * @param entity - JSONEntity which is to be sent as Request Payload
	 * @return - JSONObject returned by the Rest Call
	 */
	private static JSONObject callRest(String entity) {
		HttpClient httpClient = HttpClientBuilder.create().build();
		JSONObject obj = null;

		try {
			HttpPost httpPost = new HttpPost(GeoTabConstants.GEO_TAB_BASE_URL);
			
			// converting qthe jsonEntity as String Entity
			StringEntity params = new StringEntity(entity);
			httpPost.addHeader("content-type", "application/json; charset=UTF-8");
			httpPost.setEntity(params);
			
			// executing the post method as HttpClient
			HttpResponse response = httpClient.execute(httpPost);
			
			// receiving the response as Json String
			String json = EntityUtils.toString(response.getEntity());
			obj = new JSONObject(json);
//			logger.info("[geotab] Entity Response from API : "+obj);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("Exception : "+e.getMessage());
		}
		return obj;
	}
	
	/**
	 * @param p1 - coordinates of the device
	 * @param p2 - coordinated of the center
	 * @return distance between two points
	 */
	private static Double getDistance(Coordinates p1, Coordinates p2) {

		Double dLat = GeoTabUtil.degreeToRad(p2.getLatitude() - p1.getLatitude());
		Double dLon = GeoTabUtil.degreeToRad(p2.getLongitude() - p1.getLongitude());

		Double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(GeoTabUtil.degreeToRad(p1.getLatitude()))
				* Math.cos(GeoTabUtil.degreeToRad(p2.getLatitude())) * Math.sin(dLon / 2) * Math.sin(dLon / 2);

		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		Double d = GeoTabConstants.EARTH_RADIUS_IN_MILES * c;
		return d;
	}
	
	/**
	 * @param degree - degree to be converted
	 * @return - Double value of converted radian
	 */
	private static Double degreeToRad(Double degree) {
		return degree * (Math.PI / 180);
	}
	
	/**
	 * @param center - well location
	 * @param point - device location
	 * @param radius - radius in Miles
	 * @return - Boolean if a device is inside formed perimeter or not
	 */
	@SuppressWarnings("unused")
	private Boolean CheckIfPointInside(Coordinates center, Coordinates point, Double radius) {
		Double distance = GeoTabUtil.getDistance(point, center);
		if(distance > radius) {
			return false;
		} else if(distance <= radius) {
			return true;
		} else {
			return false;
		}
	}
	
	public static NearestUserDtoResponse getUsers(String location, String type) {
		NearestUserDtoResponse userDtoResponse = new NearestUserDtoResponse();
		List<NearestUserDto> usersDto = null;
		List<HierarchyDto> listHierarchy = null;
		
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setStatus(GeoTabConstants.FAILURE);
		responseMessage.setStatusCode(GeoTabConstants.CODE_FAILURE);
		
		GeoTabDao geoTabDao = SpringContextBridge.services().getGeoTabDao();
		
		if(!ServicesUtil.isEmpty(location) && !ServicesUtil.isEmpty(type)) {
			listHierarchy = new GeoTabUtil().getHierarchy(location, type);
			List<Coordinates> coordinates = null;
			Coordinates coordinate = null;
			if(!ServicesUtil.isEmpty(listHierarchy)) {
				coordinate = new Coordinates();
				coordinates = new ArrayList<Coordinates>();
				usersDto = new ArrayList<NearestUserDto>();
				for(HierarchyDto hierarchyDto : listHierarchy) {
//					usersDto.addAll(getUsers(hierarchyDto.getLatitude(), hierarchyDto.getLongitude()).getNearestUsers());
					coordinates.add(new Coordinates(hierarchyDto.getLatitude(), hierarchyDto.getLongitude()));
				}
				coordinate = getAverageCoordinate(coordinates);
				usersDto.addAll(getUsers(coordinate.getLatitude(), coordinate.getLongitude()).getNearestUsers());
				Collections.sort(usersDto, new DistanceComparator());
				List<NearestUserDto> listFromDB = geoTabDao.getUsersFromDB();
				if(!ServicesUtil.isEmpty(listFromDB)){
					usersDto.addAll(listFromDB);
				}
				responseMessage.setStatus(GeoTabConstants.SUCCESS);
				responseMessage.setStatusCode(GeoTabConstants.CODE_SUCCESS);
				responseMessage.setMessage("Users Fetched Successfully");
			}
		} else {
			responseMessage.setMessage("Location and Type Required");
		}
		userDtoResponse.setResponseMessage(responseMessage);
		userDtoResponse.setNearestUsers(usersDto);
		return userDtoResponse;
	}
	
	private static Coordinates getAverageCoordinate(List<Coordinates> coordinates) {
		List<Double> latitudes = null;
		List<Double> longitudes = null;
		OptionalDouble averageLatitude = null;
		OptionalDouble averageLongitude = null;
		Coordinates averageCoordinate = null;
		if(!ServicesUtil.isEmpty(coordinates)) {
			latitudes = new ArrayList<Double>();
			longitudes = new ArrayList<Double>();
			for(Coordinates coordinate : coordinates) {
				latitudes.add(coordinate.getLatitude());
				longitudes.add(coordinate.getLongitude());
			}
			averageLatitude = latitudes.stream().mapToDouble(a -> a).average();
			averageLongitude = longitudes.stream().mapToDouble(a -> a).average();
			
			averageCoordinate = new Coordinates(averageLatitude.getAsDouble(), averageLongitude.getAsDouble());
		}
		return averageCoordinate;
	}
	
	private List<HierarchyDto> getHierarchy(String location, String type) {
		
		GeoTabDao geoTabDao = SpringContextBridge.services().getGeoTabDao();
		
		List<HierarchyDto> listHierarchy = null;
		String query = "";
		if(!ServicesUtil.isEmpty(location) && !ServicesUtil.isEmpty(type)) {
			listHierarchy = new ArrayList<HierarchyDto>();
			query = "SELECT JH.BUSINESSENTITY AS BUSINESSENTITY, JH.BUISNESSUNIT AS BUISNESSUNIT, JH.FIELD AS FIELD, JH.FACILITY AS FACILITY, JH.WELLPAD AS WELLPAD, JH.WELL AS WELL, JH.MUWI AS MUWI, JHL.LATITIUDE AS LATITIUDE, JHL.LONGITUDE AS LONGITUDE FROM JSA_HIERARCHY JH JOIN JSA_HIERARCHY_LOCATION JHL ON JH.MUWI = JHL.MUWI ";
			switch(type) {
//			case GeoTabConstants.FACILITY_FIELD :
//				System.out.println(GeoTabConstants.FACILITY_FIELD);
//				query += "WHERE JH.FIELD = '"+facility+"'";
// 				listHierarchy = geoTabDao.getHierarchy(query);
//				break;
			case GeoTabConstants.LOC_CF : 
				System.out.println(GeoTabConstants.LOC_CF);
				query += "WHERE JH.FACILITY = '"+location+"'";
				listHierarchy = geoTabDao.getHierarchy(query);
				break;
			case GeoTabConstants.LOC_WELLPAD : 
				System.out.println(GeoTabConstants.LOC_WELLPAD);
				query += "WHERE JH.WELLPAD = '"+location+"'";
				listHierarchy = geoTabDao.getHierarchy(query);
				break;
//			case GeoTabConstants.FACILITY_WELL : 
//				System.out.println(GeoTabConstants.FACILITY_WELL);
//				query += "WHERE JH.WELL = '"+facility+"'";
//				listHierarchy = geoTabDao.getHierarchy(query);
//				break;
			default :
				System.out.println("Please send the correct Facility Type");
				break;
			}
		}
		return listHierarchy;
	}
	
	/*@SuppressWarnings("unchecked")
	private List<HierarchyDto> getHierarchy(String query) {
//		ResultSet resultSet = null;
		HierarchyDto hierarchyDto = null;
		List<HierarchyDto> listHierarchy = null;
		if(!ServicesUtil.isEmpty(query)) {
			listHierarchy = new ArrayList<HierarchyDto>();
//			resultSet = DBConnect.getDbCon().query(query);
			Query q =  this.getSession().createSQLQuery(query.trim());
			List<Object[]> resultList = q.list();
			if(!ServicesUtil.isEmpty(resultList)){ 
				for(Object[] obj : resultList){ 
					hierarchyDto = new HierarchyDto();
					hierarchyDto.setBusinessEntity(ServicesUtil.isEmpty(obj[0])?null:(String) obj[0]);
					hierarchyDto.setBusinessUnit(ServicesUtil.isEmpty(obj[1])?null:(String) obj[1]);
					hierarchyDto.setField(ServicesUtil.isEmpty(obj[2])?null:(String) obj[2]);
					hierarchyDto.setFacility(ServicesUtil.isEmpty(obj[3])?null:(String) obj[3]);
					hierarchyDto.setWellpad(ServicesUtil.isEmpty(obj[4])?null:(String) obj[4]);
					hierarchyDto.setWell(ServicesUtil.isEmpty(obj[5])?null:(String) obj[5]);
					hierarchyDto.setMuwi(ServicesUtil.isEmpty(obj[6])?null:(String) obj[6]);
					hierarchyDto.setLatitude(ServicesUtil.isEmpty(obj[7])?null:(Double) (obj[7]));
					hierarchyDto.setLongitude(ServicesUtil.isEmpty(obj[8])?null:(Double) obj[8]);
					
					listHierarchy.add(hierarchyDto);
				}
			}
		}
		return listHierarchy;
	}*/
	
	public static NearestUserDtoResponse getUsers(String muwi) {
		GeoTabDao geoTabDao = SpringContextBridge.services().getGeoTabDao();
		LocationHierarchyDto hierarchyDto = geoTabDao.getWellDetailsForMuwi(muwi);
		NearestUserDtoResponse userDtoResponse = new NearestUserDtoResponse();
		if(!ServicesUtil.isEmpty(hierarchyDto)) {
			userDtoResponse = getUsers(hierarchyDto.getLatValue().doubleValue(), hierarchyDto.getLongValue().doubleValue());
		} else {
			ResponseMessage responseMessage = new ResponseMessage();
			responseMessage.setMessage("Well Information Not Available");
			responseMessage.setStatus("SUCCESS");
			responseMessage.setStatusCode("0");
		}
		return userDtoResponse;
	}
	
	
	
	/**
	 * @param latitude
	 * @param longitude
	 * @return Nearest Users to the given latitude and longitude
	 */
	public static NearestUserDtoResponse getUsers(Double latitude, Double longitude) {

		Coordinates center = new Coordinates(latitude, longitude);
		NearestUserDtoResponse nearestUserDtoResponse = new NearestUserDtoResponse();
		List<NearestUserDto> nearestUserDtos = new ArrayList<NearestUserDto>();

		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setStatus(GeoTabConstants.FAILURE);
		responseMessage.setStatusCode(GeoTabConstants.CODE_FAILURE);
		
//		GeoTabDao geoTabDao = SpringContextBridge.services().getGeoTabDao();

		final long startTime = System.nanoTime();

		JSONObject deviceStatusInfo = null;
		JSONObject deviceInfo = null;
//		JSONObject userInfo = null;
		try {
			deviceStatusInfo = GeoTabUtil.callRest(formDeviceStatusInfoEntity());
			deviceInfo = GeoTabUtil.callRest(formDeviceSearchEntity());
//			userInfo = GeoTabUtil.callRest(formUserSearchEntity());
			
//			System.out.println(deviceStatusInfo);
//			System.out.println(deviceInfo);

//			logger.debug("deviceStatusInfo : "+deviceStatusInfo);
//			logger.debug("deviceInfo : "+deviceInfo);
//			logger.debug("userInfo : "+userInfo);

		} catch (ParseException e) {
			logger.debug("Exception : "+e.getMessage());
		}

		final long duration = System.nanoTime() - startTime;
		logger.error("Duration calling  rest : "+duration);

		JSONObject inDeviceStatusInfoObject = null;
		String deviceName;
		String deviceId = null;
		UserNameDto userNameDto;
		NearestUserDto nearestUserDto = null;
		Map<String, NearestUserDto> userDtoMap = new HashMap<String, NearestUserDto>();

		if(!ServicesUtil.isEmpty(deviceInfo)) {
			JSONObject inDeviceInfoObject = null;
			JSONArray deviceInfoArray = deviceInfo.getJSONArray("result");
			
			for(Object inDeviceInfoArrayObject : deviceInfoArray) {
				inDeviceInfoObject = (JSONObject) inDeviceInfoArrayObject;
				
				deviceId = inDeviceInfoObject.getString("id");
				
				deviceName = inDeviceInfoObject.getString("name");
				userNameDto = GeoTabUtil.trimName(deviceName);
				
				if(!ServicesUtil.isEmpty(userNameDto)) {
					nearestUserDto = new NearestUserDto();
					nearestUserDto.setFirstName(userNameDto.getFirstName());
					nearestUserDto.setLastName(userNameDto.getLastName());
//					nearestUserDto.setEmailId("test_email@email.com");
					nearestUserDto.setUserId(userNameDto.getTruckId());
				}
				userDtoMap.put(deviceId, nearestUserDto);
			}
		}

		if(!ServicesUtil.isEmpty(deviceStatusInfo)) {
			JSONArray deviceStatusInfoArray = deviceStatusInfo.getJSONArray("result");
			for(Object inDeviceStatusInfoArrayObject : deviceStatusInfoArray) {
				inDeviceStatusInfoObject = (JSONObject) inDeviceStatusInfoArrayObject;
//				if ((inDeviceStatusInfoObject.getBoolean("isDeviceCommunicating"))) {
					
					JSONObject dev = inDeviceStatusInfoObject.getJSONObject("device");
					Device device = new Device();
					device.setId(dev.getString("id"));
					device.setLatitude(inDeviceStatusInfoObject.getDouble("latitude"));
					device.setLongitude(inDeviceStatusInfoObject.getDouble("longitude"));
					device.setIsDriving(inDeviceStatusInfoObject.getBoolean("isDriving"));
					
					NearestUserDto dto = new NearestUserDto();
					Coordinates deviceLoc = new Coordinates(device.getLatitude(), device.getLongitude());
					dto.setDistanceFromLocation(getDistance(deviceLoc, center));
					
					NearestUserDto mapDto = userDtoMap.get(device.getId());
					mapDto.setDistanceFromLocation(Math.round(dto.getDistanceFromLocation() * 10D) / 10D);
					mapDto.setUnRoundedDistance(dto.getDistanceFromLocation());
					mapDto.setIsDriving(device.getIsDriving());
					
					nearestUserDtos.add(mapDto);
//				}
			}
			responseMessage.setStatus(GeoTabConstants.SUCCESS);
			responseMessage.setStatusCode(GeoTabConstants.CODE_SUCCESS);
			responseMessage.setMessage("Users Fetched Successfully");
		} 
		Collections.sort(nearestUserDtos, new DistanceComparator());
//		List<NearestUserDto> listFromDB = geoTabDao.getUsersFromDB();
//		if(!ServicesUtil.isEmpty(listFromDB)){
//			nearestUserDtos.addAll(listFromDB);
//		}
		nearestUserDtoResponse.setNearestUsers(nearestUserDtos);
		nearestUserDtoResponse.setResponseMessage(responseMessage);
		return nearestUserDtoResponse;
	}
	
	public static ResponseMessage signInUsers() {
		
		TimeZone defaultTZ = TimeZone.getDefault();
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		
		JSONObject deviceStatusInfo = null;
//		JSONObject deviceInfo = null;
		JSONObject inDeviceStatusInfoObject = null;
		List<DeviceStatusInfoDto> deviceStatusInfos = null;
		Map<String, List<DeviceStatusInfoDto>> map = null;
		List<DeviceStatusInfoDto> newDeviceStatusInfo = null;
		List<DeviceStatusInfoDto> existingDeviceStatusInfo = null;
		String deviceId = "";
		DeviceStatusInfoDto deviceInfo = null;
		Long dateDifference = 0L;
		Long seperateDateTime = 0L;
		Long finalDateTime = 0L;
		DateTimeDto durationBreakdown = null;
		try {
			deviceStatusInfo = GeoTabUtil.callRest(formDeviceStatusInfoEntity());
//			deviceInfo = GeoTabUtil.callRest(formDeviceSearchEntity());

			logger.debug("deviceStatusInfo : "+deviceStatusInfo);
//			logger.debug("deviceInfo : "+deviceInfo);

		} catch (ParseException e) {
			logger.debug("Exception : "+e.getMessage());
		}
		
		if(!ServicesUtil.isEmpty(deviceStatusInfo)) {
			deviceStatusInfos = new ArrayList<DeviceStatusInfoDto>();
			JSONArray deviceStatusInfoArray = deviceStatusInfo.getJSONArray("result");
			for(Object inDeviceStatusInfoArrayObject : deviceStatusInfoArray) {
				inDeviceStatusInfoObject = (JSONObject) inDeviceStatusInfoArrayObject;
				if ((!inDeviceStatusInfoObject.getBoolean("isDriving"))) {
					
					JSONObject dev = inDeviceStatusInfoObject.getJSONObject("device");
					
					DeviceStatusInfoDto statusInfoDto = new DeviceStatusInfoDto();
					statusInfoDto.setDeviceId(dev.getString("id"));
					statusInfoDto.setBearing(inDeviceStatusInfoObject.getInt("bearing"));
					statusInfoDto.setCurrentStateDuration(inDeviceStatusInfoObject.getString("currentStateDuration"));
					statusInfoDto.setDateTime(inDeviceStatusInfoObject.getString("dateTime"));
					statusInfoDto.setDeviceCommunicating(inDeviceStatusInfoObject.getBoolean("isDeviceCommunicating"));
					statusInfoDto.setDriving(inDeviceStatusInfoObject.getBoolean("isDriving"));
					statusInfoDto.setLatitude(inDeviceStatusInfoObject.getDouble("latitude"));
					statusInfoDto.setLongitude(inDeviceStatusInfoObject.getDouble("longitude"));
					statusInfoDto.setSpeed(inDeviceStatusInfoObject.getInt("speed"));
					
					deviceStatusInfos.add(statusInfoDto);
					System.out.println("statusInfoDto : "+statusInfoDto);
				}
			}
		}
		
		if(!ServicesUtil.isEmpty(deviceStatusInfos)) {
			map = new HashMap<String, List<DeviceStatusInfoDto>>();
			GeoTabDao geoTabDao = SpringContextBridge.services().getGeoTabDao();
			List<LocationHierarchyDto> allWellDetails = geoTabDao.getAllWellDetails();
			for(LocationHierarchyDto locationHierarchyDto : allWellDetails) {
				for(DeviceStatusInfoDto deviceStatus : deviceStatusInfos) {
					Coordinates well = new Coordinates(locationHierarchyDto.getLatValue(), locationHierarchyDto.getLongValue());
					Coordinates device = new Coordinates(deviceStatus.getLatitude(), deviceStatus.getLongitude());
					
					Double crowFlyDistance = GeoTabUtil.getDistance(well, device);
					if(crowFlyDistance < GeoTabConstants.GEO_TAB_AUTO_SIGNIN_DISTANCE) {
						deviceInfo = new DeviceStatusInfoDto();
						
						deviceId = deviceStatus.getDeviceId();
						deviceInfo.setDeviceId(deviceId);
						deviceInfo.setBearing(deviceStatus.getBearing());
						deviceInfo.setCurrentStateDuration(deviceStatus.getCurrentStateDuration());
						deviceInfo.setDateTime(deviceStatus.getDateTime());
						deviceInfo.setDeviceCommunicating(deviceStatus.isDeviceCommunicating());
						deviceInfo.setDriving(deviceStatus.isDriving());
						deviceInfo.setLatitude(deviceStatus.getLatitude());
						deviceInfo.setLongitude(deviceStatus.getLongitude());
						deviceInfo.setSpeed(deviceStatus.getSpeed());
						deviceInfo.setCrowFlyDistance(crowFlyDistance);
						deviceInfo.setFacility(locationHierarchyDto.getFacility());
						deviceInfo.setField(locationHierarchyDto.getField());
						deviceInfo.setWellPad(locationHierarchyDto.getWellpad());
						deviceInfo.setWellName(locationHierarchyDto.getWell());
						deviceInfo.setMuwi(locationHierarchyDto.getMuwi());
						
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						Date currentDateTime = new Date();
						
						try {
							
							dateDifference = currentDateTime.getTime() - dateFormat.parse(deviceInfo.getDateTime()).getTime();
							seperateDateTime = GeoTabUtil.seperateDateTime(deviceInfo.getCurrentStateDuration());
							finalDateTime = dateDifference + seperateDateTime;
							durationBreakdown = GeoTabUtil.getDurationBreakdown(finalDateTime);
							deviceInfo.setParkedDateTime(dateFormat.format(GeoTabUtil.getParkedTime(durationBreakdown)));
							
						} catch (java.text.ParseException e) {
							logger.error("[GeoTabUtil][getParkedtime] : Parse Exception : "+e.getMessage());
						}
						
						System.err.println("INFO : [GeoTabUtil][signInUsers][crowFlyDistance][userSignedIn][user] : "+deviceStatus);
						
						geoTabDao.saveOrUpdateAutoSignIn(deviceInfo);
						
						if(ServicesUtil.isEmpty(map.get(deviceId))) {
							newDeviceStatusInfo = new ArrayList<DeviceStatusInfoDto>();
							newDeviceStatusInfo.add(deviceInfo);
							map.put(deviceStatus.getDeviceId(), newDeviceStatusInfo);
						} else {
							existingDeviceStatusInfo = new ArrayList<DeviceStatusInfoDto>();
							existingDeviceStatusInfo.addAll(map.get(deviceId));
							existingDeviceStatusInfo.add(deviceInfo);
							map.put(deviceId, existingDeviceStatusInfo);
						}
					}
				}
			}
		}
		System.err.println("[INFO][map] : "+map);
		
		TimeZone.setDefault(defaultTZ);
		
		ResponseMessage rsp = new ResponseMessage();
		rsp.setMessage("deviceInfo : "+deviceInfo);
		
		return rsp;
	}
	
	static class DistanceComparator implements Comparator<NearestUserDto> {

		@Override
		public int compare(NearestUserDto o1, NearestUserDto o2) {
			return Double.compare(o1.getUnRoundedDistance(), o2.getUnRoundedDistance());
			/*
			System.err.println("b-- "+compare);
			if(o1.getUnRoundedDistance() > o2.getUnRoundedDistance()) {
				System.err.println("a-- 1");
				return 1;
			} else {
				System.err.println("a-- -1");
				return -1;
			}*/
		}
	}
	
//	@SuppressWarnings("unused")
//	private static String formUserSearchEntity() throws ParseException {
//		
//		Map<String, String> cred = new HashMap<String, String>();
//		cred.put("database", GeoTabConstants.GEO_TAB_DATABASE);
//		cred.put("userName", GeoTabConstants.GEO_TAB_USER_ID);
//		cred.put("password", GeoTabConstants.GEO_TAB_USER_PASS);
//		
//		Map<String, Object> crede = new HashMap<String, Object>();
//		crede.put("credentials", cred);
//		crede.put("typeName", GeoTabConstants.TYPE_NAME_USER_SEARCH);
//		
//		Map<String, Object> parameters = new HashMap<String, Object>();
//		parameters.put("params", crede);
//		parameters.put("method", GeoTabConstants.GEO_TAB_METHOD_GET);
//		
//		String jsonString = JsonUtil.addObjects(null, parameters);
//		
//		return jsonString;
//	}
	
	private static String formDeviceSearchEntity() throws ParseException {
		
		Map<String, String> cred = new HashMap<String, String>();
		cred.put("database", GeoTabConstants.GEO_TAB_DATABASE);
		cred.put("userName", GeoTabConstants.GEO_TAB_USER_ID);
		cred.put("password", GeoTabConstants.GEO_TAB_USER_PASS);
		
		Map<String, Object> crede = new HashMap<String, Object>();
		crede.put("credentials", cred);
		crede.put("typeName", GeoTabConstants.TYPE_NAME_DEVICE_SEARCH);
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("params", crede);
		parameters.put("method", GeoTabConstants.GEO_TAB_METHOD_GET);
		
		String jsonString = JsonUtil.addObjects(null, parameters);
		
		return jsonString;
	}
	
	private static String formDeviceStatusInfoEntity() throws ParseException {
		
		Map<String, String> cred = new HashMap<String, String>();
		cred.put("database", GeoTabConstants.GEO_TAB_DATABASE);
		cred.put("userName", GeoTabConstants.GEO_TAB_USER_ID);
		cred.put("password", GeoTabConstants.GEO_TAB_USER_PASS);
		
		Map<String, Object> crede = new HashMap<String, Object>();
		crede.put("credentials", cred);
		crede.put("typeName", GeoTabConstants.TYPE_NAME_DEVICESTATUSINFO);
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("params", crede);
		parameters.put("method", GeoTabConstants.GEO_TAB_METHOD_GET);
		
		String jsonString = JsonUtil.addObjects(null, parameters);
		
		return jsonString;
	}
	
	private static UserNameDto trimName(String fullName) {
		UserNameDto userNameDto = new UserNameDto();
		if((Character.isDigit(fullName.charAt(0))) && (fullName.contains(","))) {
			String trim = "-";
			String subStr = fullName.substring(fullName.indexOf(trim)+trim.length());
			String[] strArr = new String[2];
			strArr = subStr.split(",");
			userNameDto.setTruckId(fullName.substring(0, fullName.indexOf(trim.substring(0))).trim());
			userNameDto.setLastName(strArr[0].trim());
			userNameDto.setFirstName(strArr[1].trim());
			userNameDto.setFullName(fullName.trim());
		} else {
//			return null;
//			String trim = "- ";
//			String subStr = fullName.substring(fullName.indexOf(trim)+trim.length());
//			String[] strArr = new String[2];
//			strArr = subStr.split("-");
			if((Character.isDigit(fullName.charAt(0)))) {
				String trim = "- ";
				userNameDto.setTruckId(fullName.substring(0, fullName.indexOf(trim.substring(0))).trim());
				String[] strArr = new String[2];
				strArr = fullName.split("-");
				userNameDto.setFirstName(strArr[1].trim());
				userNameDto.setFullName(fullName);
			} else {
				userNameDto.setFirstName(fullName.trim());
				//System.out.println(fullName);
			}
		}
		return userNameDto;
	}
	
	public static BigDecimal getBigDecimal( Object value ) {
        BigDecimal ret = null;
        if( value != null ) {
            if( value instanceof BigDecimal ) {
                ret = (BigDecimal) value;
            } else if( value instanceof String ) {
                ret = new BigDecimal( (String) value );
            } else if( value instanceof BigInteger ) {
                ret = new BigDecimal( (BigInteger) value );
            } else if( value instanceof Number ) {
                ret = new BigDecimal( ((Number)value).doubleValue() );
            } else {
                throw new ClassCastException("Not possible to coerce ["+value+"] from class "+value.getClass()+" into a BigDecimal.");
            }
        }
        return ret;
    }
	
	/*public static void main(String[] args) throws java.text.ParseException {
//		logger.debug(GeoTabUtil.formDeviceStatusInfoEntity());
//		System.out.println(GeoTabUtil.getUsers(28.4248829, -99.6108322));
		
//		System.out.println(formDeviceStatusInfoEntity());
		
//		JSONObject deviceStatusInfo = null;
//		String entity = "{\"method\":\"Get\", \"isDriving\": true, \"params\":{\"credentials\":{\"database\":\"murphy_oil\",\"password\":\"$tr3amlin3\",\"userName\":\"SVC_IOP@murphyoilcorp.com\"},\"typeName\":\"DeviceStatusInfo\"}}";
//		deviceStatusInfo = GeoTabUtil.callRest(entity);
//		logger.debug("deviceStatusInfo : "+deviceStatusInfo);
//		
//		System.out.println(deviceStatusInfo);
		
		
//		GeoTabUtil.signInUsers();
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		
		Long dateDifference = 0L;
		Long seperateDateTime = 0L;
		Long finalDateTime = 0L;
		DateTimeDto durationBreakdown = null;
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		
		String currentStateDuration = "06:02:06.0630000";
		String dateTime = "2018-05-16T05:08:25.063Z"; // 2 hour 11 minutes 50 seconds
		Date currentDateTime = new Date();
//		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		try {
			
			dateDifference = currentDateTime.getTime() - dateFormat.parse(dateTime).getTime();
			seperateDateTime = GeoTabUtil.seperateDateTime(currentStateDuration);
			finalDateTime = dateDifference + seperateDateTime;
			durationBreakdown = GeoTabUtil.getDurationBreakdown(finalDateTime);
			System.out.println(durationBreakdown);
			System.out.println((GeoTabUtil.getParkedTime(durationBreakdown)));
			
		} catch (java.text.ParseException e) {
			logger.error("[GeoTabUtil][getParkedtime] : Parse Exception : "+e.getMessage());
		}
		
		
//		Long dateDifference = dateFormat.parse(dateFormat.format(currentDateTime)).getTime() - dateFormat.parse(dateTime).getTime();
		
//		Long diffSeconds = dateDifference / 1000 % 60;
//		Long diffMinutes = dateDifference / (60 * 1000) % 60;
//		Long diffHours = dateDifference / (60 * 60 * 1000);
		
//		Long seperateDateTime = GeoTabUtil.seperateDateTime(currentStateDuration);
		
//		Long finalDateTime = dateDifference + seperateDateTime;
		
//		Long diffMinutes = finalDateTime / (60 * 1000) % 60;
		
//		System.out.println(finalDateTime);
		
//		DateTimeDto durationBreakdown = GeoTabUtil.getDurationBreakdown(finalDateTime);
		
//		System.out.println(durationBreakdown);
		
//		System.out.println(dateFormat.format(GeoTabUtil.getParkedTime(durationBreakdown)));
	}*/
	
	public static void main(String[] args) throws ParseException {
//		System.out.println(GeoTabUtil.getUsers(28.81175754, -98.53525117));
		
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				for(;;) {
					JSONObject jsonObject = null;
					try {
						jsonObject = GeoTabUtil.callRest(formDeviceStatusInfoEntity());
					} catch (ParseException e) {
						e.printStackTrace();
					}
					System.out.println(GeoTabUtil.checkIfResultExist(jsonObject));
				}
			}
		});
		
		thread.start();
	}
	
	private static Date getParkedTime(DateTimeDto dateTimeDto) throws java.text.ParseException {
		Date currentDate = new Date();
		DateFormat dateFormat = new SimpleDateFormat();
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		currentDate = dateFormat.parse(dateFormat.format(currentDate));
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		cal.setTime(currentDate);
		
		cal.add(Calendar.DATE, -dateTimeDto.getDay().intValue());
		cal.add(Calendar.HOUR, -dateTimeDto.getHour().intValue());
		cal.add(Calendar.MINUTE, -dateTimeDto.getMinute().intValue());
		cal.add(Calendar.SECOND, -dateTimeDto.getSecond().intValue());
		
		return cal.getTime();
	}
	
	private static DateTimeDto getDurationBreakdown(Long millis) {
		DateTimeDto dateTimeDto = new DateTimeDto();
        if(millis < 0) {
        	throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        Long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        Long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        Long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        Long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        dateTimeDto.setDay(days);
        dateTimeDto.setHour(hours);
        dateTimeDto.setMinute(minutes);
        dateTimeDto.setSecond(seconds);
        
        dateTimeDto.setDateTimeInString(days + "." + hours + ":" + minutes + ":" + seconds);
        
        return dateTimeDto;
    }
	
	private static Long seperateDateTime(String dateTimeString) {
		String[] array = dateTimeString.split("\\.");
		String date = "";
		@SuppressWarnings("unused")
		String milliSeconds = "";
		String[] timeString;
		String hours = "";
		String minutes = "";
		String seconds = "";
		
		Long ret = 0L;
		
		if(array.length == 3) {
			date = array[0];
			milliSeconds = array[2];
			timeString = array[1].split("\\:");
			hours = timeString[0];
			minutes = timeString[1];
			seconds = timeString[2];
			
			ret = GeoTabUtil.convertToMilliSeconds(Long.valueOf(date), GeoTabConstants.GEO_TAB_TIME_DAY) + GeoTabUtil.convertToMilliSeconds(Long.valueOf(hours), GeoTabConstants.GEO_TAB_TIME_HOUR) + GeoTabUtil.convertToMilliSeconds(Long.valueOf(minutes), GeoTabConstants.GEO_TAB_TIME_MINUTE) + GeoTabUtil.convertToMilliSeconds(Long.valueOf(seconds), GeoTabConstants.GEO_TAB_TIME_SECOND);
			
		} else if (array.length == 2){
			date = "00";
			milliSeconds = array[1];
			timeString = array[0].split("\\:");
			hours = timeString[0];
			minutes = timeString[1];
			seconds = timeString[2];
			
			ret = GeoTabUtil.convertToMilliSeconds(Long.valueOf(date), GeoTabConstants.GEO_TAB_TIME_DAY) + GeoTabUtil.convertToMilliSeconds(Long.valueOf(hours), GeoTabConstants.GEO_TAB_TIME_HOUR) + GeoTabUtil.convertToMilliSeconds(Long.valueOf(minutes), GeoTabConstants.GEO_TAB_TIME_MINUTE) + GeoTabUtil.convertToMilliSeconds(Long.valueOf(seconds), GeoTabConstants.GEO_TAB_TIME_SECOND);
			
		} else {
			logger.error("[seperateDateTime][dateTimeString] : "+dateTimeString);
		}
		
		return ret;
	}
	
	private static Long convertToMilliSeconds(Long value, String type) {
		switch(type) {
		case (GeoTabConstants.GEO_TAB_TIME_HOUR) :
			return TimeUnit.HOURS.toMillis(value);
		case (GeoTabConstants.GEO_TAB_TIME_MINUTE) :
			return TimeUnit.MINUTES.toMillis(value);
		case (GeoTabConstants.GEO_TAB_TIME_SECOND) :
			return TimeUnit.SECONDS.toMillis(value);
		case (GeoTabConstants.GEO_TAB_TIME_DAY) :
			return TimeUnit.DAYS.toMillis(value);
		default: 
			return 0L;
		}
	}
	
	private static Boolean checkIfResultExist(JSONObject jsonObject) {
		
		if(!ServicesUtil.isEmpty(jsonObject)) {
			if(jsonObject.toString().contains("result")) {
				return true;
			} else {
				System.out.println(jsonObject.toString());
			}
		}
		return false;
	}
	
//	public static int getCharacterType(char ch) {
//		int ret = -1;
//		int ascii = (int) ch;
//		if((ascii >= 33 && ascii <= 47) || (ascii >= 58 && ascii <=64) || (ascii >= 91 && ascii <= 96) || (ascii >= 123 && ascii <= 126)) {
//			ret = 0;
//		} else if(ascii >= 48 && ascii <= 57) {
//			ret = 1;
//		} else if ((ascii >= 65 && ascii<= 90) || (ascii >= 97 && ascii <= 122)) {
//			ret = 2;
//		}
//		return ret;
//	}
}
