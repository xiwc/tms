package com.lhjz.portal.pojo;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

public class UserForm {

	@NotBlank(message = "用户名不能为空!")
	private String username;
	// @NotBlank(message = "密码不能为空!")
	// @Length(min = 6, max = 255, message = "密码长度不能少于6位并且不能超过255!")
	private String password;
	@Email(message = "邮件格式不正确!")
	private String mail;
	private Boolean enabled;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	@Override
	public String toString() {
		return "UserForm [username=" + username + ", password=" + password
				+ ", mail=" + mail + ", enabled=" + enabled + "]";
	}

}
