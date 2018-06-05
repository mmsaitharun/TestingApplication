package com.demo.application.geotab;

import java.util.List;

/**
 * @author INC00718
 *
 */
public class NearestUserDtoResponse {
	
	private List<NearestUserDto> nearestUsers;
	ResponseMessage responseMessage;
	
	/**
	 * @return the nearestUsers
	 */
	public List<NearestUserDto> getNearestUsers() {
		return nearestUsers;
	}
	/**
	 * @param nearestUsers the nearestUsers to set
	 */
	public void setNearestUsers(List<NearestUserDto> nearestUsers) {
		this.nearestUsers = nearestUsers;
	}
	/**
	 * @return the responseMessage
	 */
	public ResponseMessage getResponseMessage() {
		return responseMessage;
	}
	/**
	 * @param responseMessage the responseMessage to set
	 */
	public void setResponseMessage(ResponseMessage responseMessage) {
		this.responseMessage = responseMessage;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NearestUserDtoResponse [nearestUsers=" + nearestUsers + ", responseMessage=" + responseMessage + "]";
	}
}
