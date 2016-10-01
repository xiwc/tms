package com.lhjz.portal.entity.security.oauth2;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class OauthRefreshToken implements Serializable {

	private static final long serialVersionUID = -5919370650444204314L;

	@Id
	@GeneratedValue
	private Long id;

	private String tokenId;
	@Lob
	private String token;
	@Lob
	private String authentication;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getAuthentication() {
		return authentication;
	}

	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}

}
