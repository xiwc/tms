package com.lhjz.portal.pojo;

import org.hibernate.validator.constraints.NotBlank;

public class DiagnoseForm {

	private String mail;
	private String phone;

	@NotBlank(message = "症状描述不能为空！")
	private String description;

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "DiagnoseForm [mail=" + mail + ", phone=" + phone
				+ ", description=" + description + "]";
	}

}
