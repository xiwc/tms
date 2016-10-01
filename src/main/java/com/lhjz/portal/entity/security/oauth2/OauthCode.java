package com.lhjz.portal.entity.security.oauth2;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class OauthCode implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	private static final long serialVersionUID = 2072255024118911470L;
	private String code;
	@Lob
	private String authentication;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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
