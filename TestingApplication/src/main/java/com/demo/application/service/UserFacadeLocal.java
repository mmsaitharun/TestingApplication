package com.demo.application.service;

import java.util.List;

import com.demo.application.dto.UserDto;

public interface UserFacadeLocal {

	String createUser(UserDto dto);

	List<UserDto> getUser(String loginName, String fullName, String phoneNumber);

}
