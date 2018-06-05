package com.demo.application.dao;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.demo.application.dto.UserDto;
import com.demo.application.entity.User;
import com.demo.application.util.ServicesUtil;

@Repository("userDao")
@Transactional
public class UserDao {

	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;

	public Session getSession() {
		try {
			return sessionFactory.getCurrentSession();
		} catch (HibernateException e) {
			e.printStackTrace();
			return sessionFactory.openSession();
		}
	}
	
	public UserDto exportDto(User user) {
		UserDto dto = new UserDto();
		dto.setUserLoginName(user.getUserLoginName());
		dto.setUserFullName(user.getUserFullName());
		dto.setPhoneNumber(user.getPhoneNumber());
		dto.setUserId(user.getUserId());
		return dto;
	}
	
	public User importDto(UserDto dto) {
		User user = new User();
		user.setUserId(dto.getUserId());
		user.setUserLoginName(dto.getUserLoginName());
		user.setUserFullName(dto.getUserFullName());
//		user.setPhoneNumber(dto.getPhoneNumber());
		return user;
	}
	
	public List<UserDto> exportDtoList(List<User> userList) {
		List<UserDto> list = new ArrayList<UserDto>();
		for(User user : userList) {
			list.add(this.exportDto(user));
		}
		return list;
	}
	
	public List<User> importDtoList(List<UserDto> userList) {
		List<User> list = new ArrayList<User>();
		for(UserDto userDto : userList) {
			list.add(this.importDto(userDto));
		}
		return list;
	}
	
	public String createUser(UserDto userDto) {
		Session session = this.getSession();
		
		String str  = "";
		
		User user = session.find(User.class, userDto.getUserId());
		System.err.println("User : "+user);
		try {
			str = (String) session.save(this.importDto(userDto));
		} catch (NonUniqueObjectException e) {
			System.err.println("NonUniqueObjectException : "+e.getMessage());
		}
		return str;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<UserDto> getUser(String loginName, String fullName, String phoneNumber) {
//		return this.exportDto(this.getSession().filter(User.class, loginName));
		Criteria criteria = this.getSession().createCriteria(User.class);
		if(!ServicesUtil.isEmpty(loginName)) {
			criteria.add(Restrictions.eq("userLoginName", loginName));
		} else if(!ServicesUtil.isEmpty(fullName)) {
			criteria.add(Restrictions.eq("userFullName", fullName));
		} else if(!ServicesUtil.isEmpty(phoneNumber)) {
			criteria.add(Restrictions.eq("phoneNumber", phoneNumber));
		}
		List<User> result = criteria.list();
		return this.exportDtoList(result);
	}
}
