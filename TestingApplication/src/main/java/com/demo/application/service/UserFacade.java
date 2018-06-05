package com.demo.application.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.application.dao.UserDao;
import com.demo.application.dto.UserDto;

@Service
public class UserFacade implements UserFacadeLocal {
	
	@Autowired
	private UserDao userDao;
	
	@Override
	public String createUser(UserDto dto) {
		return userDao.createUser(dto);
	}
	
	@Override
	public List<UserDto> getUser(String loginName, String fullName, String phoneNumber) {
		return userDao.getUser(loginName, fullName, phoneNumber);
	}
}
