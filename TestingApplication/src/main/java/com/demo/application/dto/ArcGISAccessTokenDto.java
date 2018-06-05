package com.demo.application.dto;

public class ArcGISAccessTokenDto {

	private String accessToken;
	private Integer validity;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public Integer getValidity() {
		return validity;
	}

	public void setValidity(Integer validity) {
		this.validity = validity;
	}

	@Override
	public String toString() {
		return "ArcGISAccessTokenDto [accessToken=" + accessToken + ", validity=" + validity + "]";
	}

}
