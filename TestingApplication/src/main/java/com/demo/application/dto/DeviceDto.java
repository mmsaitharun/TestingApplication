package com.demo.application.dto;

public class DeviceDto {

	private String deviceType;
	private String id;
	private String name;
	private String serialNumber;
	private String vehicleIdentificationNumber;

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getVehicleIdentificationNumber() {
		return vehicleIdentificationNumber;
	}

	public void setVehicleIdentificationNumber(String vehicleIdentificationNumber) {
		this.vehicleIdentificationNumber = vehicleIdentificationNumber;
	}

	@Override
	public String toString() {
		return "DeviceDto [deviceType=" + deviceType + ", id=" + id + ", name=" + name + ", serialNumber="
				+ serialNumber + ", vehicleIdentificationNumber=" + vehicleIdentificationNumber + "]";
	}

}
