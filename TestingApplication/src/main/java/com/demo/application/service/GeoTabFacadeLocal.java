package com.demo.application.service;

import com.demo.application.geotab.NearestUserDtoResponse;
import com.demo.application.geotab.ResponseMessage;

public interface GeoTabFacadeLocal {

	NearestUserDtoResponse getUsers(Double latitude, Double longitude);

	NearestUserDtoResponse getUsers(String facility, String type);

	NearestUserDtoResponse getUsers(String muwi);

	ResponseMessage signInUsers();
}
