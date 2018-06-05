package com.demo.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.application.geotab.NearestUserDtoResponse;
import com.demo.application.geotab.ResponseMessage;
import com.demo.application.service.GeoTabFacadeLocal;

@RestController
@CrossOrigin
@ComponentScan("com.murphy")
@RequestMapping(value = "/location", produces = "application/json")
public class GeoTabRest {
	
	@Autowired
	GeoTabFacadeLocal facadeLocal;
	
	@RequestMapping(value = "/getNearestUsers", method = RequestMethod.GET)
	NearestUserDtoResponse getUsers(@RequestParam(value ="latitude") Double latitude, @RequestParam(value = "longitude") Double longitude){
		return facadeLocal.getUsers(latitude, longitude);
	}
	
	@RequestMapping(value = "/getNearestFacilityUsers", method = RequestMethod.GET)
	NearestUserDtoResponse getUsers(@RequestParam(value ="location") String location, @RequestParam(value = "type") String type){
		return facadeLocal.getUsers(location, type);
	}
	
	@RequestMapping(value = "/getNearestUsersByMuwi", method = RequestMethod.GET)
	NearestUserDtoResponse getUsers(@RequestParam(value = "muwi") String muwi){
		return facadeLocal.getUsers(muwi);
	}
	
	@RequestMapping(value = "/signInUsers", method = RequestMethod.GET)
	ResponseMessage signInUsers(){
		return facadeLocal.signInUsers();
	}

}
