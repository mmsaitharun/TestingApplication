package com.demo.application.dto;

import java.math.BigDecimal;

public class LocationHierarchyDto {

	private String muwi;
	private String businessEntity;
	private String businessUnit;
	private String field;
	private String facility;
	private String wellpad;
	private String well;
	private BigDecimal longValue;
	private BigDecimal latValue;
	private String childExist;
	private String location;

	public String getMuwi() {
		return muwi;
	}

	public void setMuwi(String muwi) {
		this.muwi = muwi;
	}

	public String getBusinessEntity() {
		return businessEntity;
	}

	public void setBusinessEntity(String businessEntity) {
		this.businessEntity = businessEntity;
	}

	public String getBusinessUnit() {
		return businessUnit;
	}

	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getFacility() {
		return facility;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}

	public String getWellpad() {
		return wellpad;
	}

	public void setWellpad(String wellpad) {
		this.wellpad = wellpad;
	}

	public String getWell() {
		return well;
	}

	public void setWell(String well) {
		this.well = well;
	}

	public BigDecimal getLongValue() {
		return longValue;
	}

	public void setLongValue(BigDecimal longValue) {
		this.longValue = longValue;
	}

	public BigDecimal getLatValue() {
		return latValue;
	}

	public void setLatValue(BigDecimal latValue) {
		this.latValue = latValue;
	}

	public String getChildExist() {
		return childExist;
	}

	public void setChildExist(String childExist) {
		this.childExist = childExist;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "LocationHierarchyDto [muwi=" + muwi + ", businessEntity=" + businessEntity + ", businessUnit="
				+ businessUnit + ", field=" + field + ", facility=" + facility + ", wellpad=" + wellpad + ", well="
				+ well + ", longValue=" + longValue + ", latValue=" + latValue + ", childExist=" + childExist
				+ ", location=" + location + "]";
	}

}
