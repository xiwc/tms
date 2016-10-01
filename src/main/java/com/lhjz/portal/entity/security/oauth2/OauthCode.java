package com.lhjz.portal.entity.security.oauth2;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
public class OauthCode implements Serializable {

	private static final long serialVersionUID = 2072255024118911470L;
	private String code;
	@Lob
	private String authentication;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAuthentication() {
		return authentication;
	}

	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}

}
