package com.lhjz.portal.entity.security.oauth2;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
public class OauthRefreshToken implements Serializable {

	private static final long serialVersionUID = -5919370650444204314L;
	private String tokenId;
	@Lob
	private String token;
	@Lob
	private String authentication;

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
