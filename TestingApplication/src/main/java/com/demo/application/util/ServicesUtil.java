package com.demo.application.util;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.demo.application.arcgis.util.ArcGISUtil;
import com.demo.application.dto.ArcGISAccessTokenDto;
import com.demo.application.geotab.GeoTabConstants;

public class ServicesUtil {
	public static boolean isEmpty(Object[] objs) {
		if (objs == null || objs.length == 0) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(Object o) {
		if (o == null) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(Collection<?> o) {
		if (o == null || o.isEmpty()) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(String str) {
		if (str == null || str.trim().isEmpty()) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(StringBuffer sb) {
		if (sb == null || sb.length() == 0) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(StringBuilder sb) {
		if (sb == null || sb.length() == 0) {
			return true;
		}
		return false;
	}
	
	public static Object getProperty(String propertyName) throws ConfigurationException {
		PropertiesConfiguration configuration = new PropertiesConfiguration(GeoTabConstants.IOP_PROPERTY_FILE_LOCATION);
		return configuration.getProperty(propertyName);
	}
	
	public static void setProperty(String propertyName, Object propertyValue) throws ConfigurationException {
		PropertiesConfiguration configuration = new PropertiesConfiguration(GeoTabConstants.IOP_PROPERTY_FILE_LOCATION);
		configuration.setProperty(propertyName, propertyValue);
		configuration.save();
	}
	
	public static void refreshArcGISToken() {
		ArcGISAccessTokenDto accessTokenDto = ArcGISUtil.getAccessToken();
		Date currentDate = null;
		Date validTill = null;
		if(!ServicesUtil.isEmpty(accessTokenDto)) {
			try {
				currentDate = new Date();
				validTill = new Date();
				validTill.setTime((currentDate.getTime()  + (accessTokenDto.getValidity() * 1000)));
				ServicesUtil.setProperty("arcgis.token", accessTokenDto.getAccessToken());
				ServicesUtil.setProperty("arcgis.token.timestamp", currentDate);
				ServicesUtil.setProperty("arcgis.token.validity", accessTokenDto.getValidity());
				ServicesUtil.setProperty("arcgis.token.validtill", validTill);
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		ServicesUtil.refreshArcGISToken();
	}
}
