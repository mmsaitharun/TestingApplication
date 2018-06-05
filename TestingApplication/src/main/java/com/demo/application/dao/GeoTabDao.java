package com.demo.application.dao;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.demo.application.dto.AutoSignInDto;
import com.demo.application.dto.DeviceStatusInfoDto;
import com.demo.application.dto.LocationHierarchyDto;
import com.demo.application.geotab.HierarchyDto;
import com.demo.application.geotab.NearestUserDto;
import com.demo.application.geotab.ResponseMessage;
import com.demo.application.geotab.util.GeoTabUtil;
import com.demo.application.util.ServicesUtil;

@SuppressWarnings("deprecation")
@Repository("geoTabDao")
@EnableTransactionManagement
@Transactional(value="sessionFactoryTransactionManager")
public class GeoTabDao {
	
	private static final Logger logger = LoggerFactory.getLogger(GeoTabDao.class);
	
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;
	
	@Autowired
	private AutoSignInDao autoSignInDao;

	public Session getSession() {
		try {
			return sessionFactory.getCurrentSession();
		} catch (HibernateException e) {
			e.printStackTrace();
			return sessionFactory.openSession();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<HierarchyDto> getHierarchy(String query) {
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
					hierarchyDto.setLatitude(ServicesUtil.isEmpty(obj[7])?null:GeoTabUtil.getBigDecimal(obj[7]).doubleValue());
					hierarchyDto.setLongitude(ServicesUtil.isEmpty(obj[8])?null:GeoTabUtil.getBigDecimal(obj[8]).doubleValue());
					
					listHierarchy.add(hierarchyDto);
				}
			}
		}
		return listHierarchy;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<NearestUserDto> getUsersFromDB() {
		List<NearestUserDto> usersDto = null;
		NearestUserDto user = null;
		Query query = this.getSession().createSQLQuery("SELECT ID, LOGIN_NAME, FIRST_NAME, LAST_NAME FROM GEOTAB_MANUAL_USERS");
		List<Object[]> resultList = query.list();
		if(!ServicesUtil.isEmpty(resultList)) {
			usersDto = new ArrayList<NearestUserDto>();
			for(Object[] object : resultList) {
				user = new NearestUserDto();
				user.setFirstName((String) object[2]);
				user.setLastName((String) object[3]);
				usersDto.add(user);
				logger.error("[geotab][userFromDB][user] : "+user);
			}
		}
		logger.error("[geotab][userFromDB][usersDto] : "+usersDto);
		return usersDto;
	}
	
	@SuppressWarnings("rawtypes")
	public LocationHierarchyDto getWellDetailsForMuwi(String muwi) {
		LocationHierarchyDto hierarchyDto = null;
		if(!ServicesUtil.isEmpty(muwi)) {
			String queryForWellDetails = "SELECT JS.MUWI, JS.WELL, JS.WELLPAD, JS.FACILITY, JS.FIELD, JS.BUISNESSUNIT, JS.BUSINESSENTITY, JH.LATITIUDE, JH.LONGITUDE FROM JSA_HIERARCHY JS JOIN JSA_HIERARCHY_LOCATION JH ON JS.MUWI = JH.MUWI WHERE JS.MUWI = '"+muwi+"'";
			Query query = this.getSession().createSQLQuery(queryForWellDetails);
			@SuppressWarnings("unchecked")
			List<Object[]> result = query.list();
			if(!ServicesUtil.isEmpty(result) && result.size() > 0) {
				hierarchyDto = new LocationHierarchyDto();
				Object[] object = result.get(0);
				if(!ServicesUtil.isEmpty(object)) {
					hierarchyDto.setMuwi((String) object[0]);
					hierarchyDto.setWell((String) object[1]);
					hierarchyDto.setWellpad((String) object[2]);
					hierarchyDto.setFacility((String) object[3]);
					hierarchyDto.setField((String) object[4]);
					hierarchyDto.setBusinessUnit((String) object[5]);
					hierarchyDto.setBusinessEntity((String) object[6]);
					hierarchyDto.setLatValue(new BigDecimal((Double) object[7]));
					hierarchyDto.setLongValue(new BigDecimal((Double) object[8]));
				}
			}
		}
		return hierarchyDto;
	}
	
	public ResponseMessage saveOrUpdateAutoSignIn(DeviceStatusInfoDto deviceStatusInfoDto) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		AutoSignInDto autoSignInDto = new AutoSignInDto();
		autoSignInDto.setDriverId(deviceStatusInfoDto.getDeviceId());
		autoSignInDto.setDriverLat(deviceStatusInfoDto.getLatitude());
		autoSignInDto.setDriverLon(deviceStatusInfoDto.getLongitude());
		autoSignInDto.setMuwi(deviceStatusInfoDto.getMuwi());
		autoSignInDto.setWellName(deviceStatusInfoDto.getWellName());
		autoSignInDto.setRoadDistance(deviceStatusInfoDto.getRoadDistance());
		autoSignInDto.setCrowFlyDistance(deviceStatusInfoDto.getCrowFlyDistance());
		try {
			autoSignInDto.setSignInStart(dateFormat.parse(deviceStatusInfoDto.getParkedDateTime()));
		} catch (ParseException e) {
			logger.error("ERROR : [GeoTabDao][saveOrUpdateAutoSignIn][ParseException] : "+e.getMessage());
		}
		return autoSignInDao.saveOrUpdateIntoSignInTable(autoSignInDto);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<LocationHierarchyDto> getAllWellDetails() {
		List<LocationHierarchyDto> locations = null;
		String allWellQueryString = "select jhl.MUWI, jhl.LATITIUDE, jhl.LONGITUDE, jh.BUSINESSENTITY, jh.BUISNESSUNIT, jh.FIELD, jh.FACILITY, jh.WELLPAD, jh.WELL from jsa_hierarchy jh join jsa_hierarchy_location jhl on jh.MUWI = jhl.MUWI";
		Query query = this.getSession().createSQLQuery(allWellQueryString);
		List<Object[]> result = query.list();
		
		if(!ServicesUtil.isEmpty(result) && result.size() > 0) {
			locations = new ArrayList<LocationHierarchyDto>();
			for(Object[] obj : result) {
				LocationHierarchyDto hierarchyDto = new LocationHierarchyDto();
				hierarchyDto.setMuwi((String) obj[0]);
				hierarchyDto.setLatValue(new BigDecimal((Double) obj[1]));
				hierarchyDto.setLongValue(new BigDecimal((Double) obj[2]));
				hierarchyDto.setBusinessEntity((String) obj[3]);
				hierarchyDto.setBusinessUnit((String) obj[4]);
				hierarchyDto.setField((String) obj[5]);
				hierarchyDto.setFacility((String) obj[6]);
				hierarchyDto.setWellpad((String) obj[7]);
				hierarchyDto.setWell((String) obj[8]);
				
				locations.add(hierarchyDto);
			}
		}
		return locations;
	}
	
	public static void main(String[] args) throws ParseException {
		Date date = new Date();
//		2018-05-14T07:13:26.080Z
		String dt = "2018-05-14T07:13:26.080Z";
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		System.out.println(df.format(date));
		
		Date formattedDate = df.parse(dt);
		System.out.println(formattedDate.getYear());
//		System.out.println(df.format(df.parse(dt)));
		
		DateTimeZone timeZone = DateTimeZone.UTC;
		DateTimeZone.setDefault(timeZone);
		DateTime dateTime = new DateTime();
		System.out.println(dateTime.getZone());
		System.out.println(dateTime);
	}
	
}
