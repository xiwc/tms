package com.lhjz.portal.pojo;

public class ContactForm {

	private String name;
	private String addr;
	private String phone;
	private String mail;
	private String qq;
	private String bus;
	private String map;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getBus() {
		return bus;
	}

	public void setBus(String bus) {
		this.bus = bus;
	}

	public String getMap() {
		return map;
	}

	public void setMap(String map) {
		this.map = map;
	}

	@Override
	public String toString() {
		return "ContactForm [name=" + name + ", addr=" + addr + ", phone="
				+ phone + ", mail=" + mail + ", qq=" + qq + ", bus=" + bus
				+ ", map=" + map + "]";
	}

}
