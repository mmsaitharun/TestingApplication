package com.demo.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.demo.application.geotab.NearestUserDtoResponse;
import com.demo.application.geotab.ResponseMessage;
import com.demo.application.geotab.util.GeoTabUtil;

@Service("geoTabFacade")
public class GeoTabFacade implements GeoTabFacadeLocal {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(GeoTabFacade.class);
	
	public GeoTabFacade() {
		
	}

	@Override
	public NearestUserDtoResponse getUsers(Double latitude, Double longitude) {
		return GeoTabUtil.getUsers(latitude, longitude);
	}
	
	@Override
	public NearestUserDtoResponse getUsers(String location, String type) {
		return GeoTabUtil.getUsers(location, type);
	}
	
	@Override
	public NearestUserDtoResponse getUsers(String muwi) {
		return GeoTabUtil.getUsers(muwi);
	}
	
	@Override
	public ResponseMessage signInUsers() {
		return GeoTabUtil.signInUsers();
	}

}
