package com.demo.application.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Embeddable
public class AutoSignInIdDoPK implements Serializable {

	/**
	 * Generated Serial Version ID
	 */
	private static final long serialVersionUID = 2713178264137136552L;

	@Column(name = "DRIVER_ID", length = 100)
	private String driverId;

	@Column(name = "MUWI", length = 150)
	private String muwi;

	@Column(name = "SIGNIN_START_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date signInStart;

	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

	public String getMuwi() {
		return muwi;
	}

	public void setMuwi(String muwi) {
		this.muwi = muwi;
	}

	public Date getSignInStart() {
		return signInStart;
	}

	public void setSignInStart(Date signInStart) {
		this.signInStart = signInStart;
	}

	@Override
	public String toString() {
		return "AutoSignInIdDoPK [driverId=" + driverId + ", muwi=" + muwi + ", signInStart=" + signInStart + "]";
	}

}
