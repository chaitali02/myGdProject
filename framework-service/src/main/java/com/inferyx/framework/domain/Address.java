package com.inferyx.framework.domain;

public class Address {
	
	String type;
	String addressLine1;
	String addressLine2;
	String city;
	String state;
	String country;
	String zipcode;
	
	public String getType() {
		return this.type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAddressLine1() {
		return this.addressLine1;
	}
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	public String getAddressLine2() {
		return this.addressLine2;
	}
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}
	public String getCity() {
		return this.city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return this.state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCountry() {
		return this.country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getZipcode() {
		return this.zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

}
