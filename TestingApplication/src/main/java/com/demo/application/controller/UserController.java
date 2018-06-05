package com.demo.application.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.application.dto.UserDto;
import com.demo.application.service.UserFacadeLocal;

@RestController
@RequestMapping(value = "/user")
public class UserController {

	@Autowired
	private UserFacadeLocal userFacadeLocal;

	@RequestMapping(value = "/getUsers", method = RequestMethod.GET)
	public List<UserDto> getUsers(@RequestParam(value = "loginName", required = false) String loginName,
			@RequestParam(value = "userFullName", required = false) String fullName,
			@RequestParam(value = "phoneNumber", required = false) String phoneNumber) {
		return userFacadeLocal.getUser(loginName, fullName, phoneNumber);
	}

	@RequestMapping(value = "/createUser", method = RequestMethod.POST)
	public String createUser(@RequestBody(required = true) UserDto userDto) {
		return userFacadeLocal.createUser(userDto);
	}
}
