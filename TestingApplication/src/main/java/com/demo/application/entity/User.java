package com.demo.application.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "USERS")
public class User {

	@Id
	@Column(name = "USER_ID")
	private String userId = UUID.randomUUID().toString().replaceAll("-", "");
	@Column(name = "USER_LOGIN_NAME")
	private String userLoginName;
	@Column(name = "USER_FULL_NAME")
	private String userFullName;
	@Column(name = "PHONE_NUMBER")
	private String phoneNumber;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserLoginName() {
		return userLoginName;
	}

	public void setUserLoginName(String userLoginName) {
		this.userLoginName = userLoginName;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", userLoginName=" + userLoginName + ", userFullName=" + userFullName
				+ ", phoneNumber=" + phoneNumber + "]";
	}

}
