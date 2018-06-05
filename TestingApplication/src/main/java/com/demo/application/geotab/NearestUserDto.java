package com.demo.application.geotab;

/**
 * @author INC00718
 *
 */
public class NearestUserDto {

	private String userId;
	private String firstName;
	private String lastName;
	private String emailId;
	private Double distanceFromLocation;
	private Boolean isDriving;
	private Double unRoundedDistance;
	// private Coordinates center;

	public Boolean getIsDriving() {
		return isDriving;
	}

	public Double getUnRoundedDistance() {
		return unRoundedDistance;
	}

	public void setUnRoundedDistance(Double unRoundedDistance) {
		this.unRoundedDistance = unRoundedDistance;
	}

	public void setIsDriving(Boolean isDriving) {
		this.isDriving = isDriving;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the emailId
	 */
	public String getEmailId() {
		return emailId;
	}

	/**
	 * @param emailId
	 *            the emailId to set
	 */
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	/**
	 * @return the distanceFromLocation
	 */
	public Double getDistanceFromLocation() {
		return distanceFromLocation;
	}

	/**
	 * @param distanceFromLocation
	 *            the distanceFromLocation to set
	 */
	public void setDistanceFromLocation(Double distanceFromLocation) {
		this.distanceFromLocation = distanceFromLocation;
	}

	@Override
	public String toString() {
		return "NearestUserDto [userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName + ", emailId="
				+ emailId + ", distanceFromLocation=" + distanceFromLocation + ", isDriving=" + isDriving
				+ ", unRoundedDistance=" + unRoundedDistance + "]";
	}

}
