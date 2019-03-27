package com.inferyx.framework.domain;



import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="organization")
public class Organization extends BaseEntity {
	private List<Contact> contact;
	private List<Phone> phone;
	private List<Email> email;
	private List<Address> address;
	private String logoPath;
	
	public List<Contact> getContact() {
		return this.contact;
	}
	public void setContact(List<Contact> contact) {
		this.contact = contact;
	}
	public List<Phone> getPhone() {
		return this.phone;
	}
	public void setPhone(List<Phone> phone) {
		this.phone = phone;
	}
	public List<Email> getEmail() {
		return this.email;
	}
	public void setEmail(List<Email> email) {
		this.email = email;
	}
	public List<Address> getAddress() {
		return this.address;
	}
	public void setAddress(List<Address> address) {
		this.address = address;
	}
	/**
	 * @Ganesh
	 *
	 * @return the logoPath
	 */
	public String getLogoPath() {
		return logoPath;
	}
	/**
	 * @Ganesh
	 *
	 * @param logoPath the logoPath to set
	 */
	public void setLogoPath(String logoPath) {
		this.logoPath = logoPath;
	}
	
}
