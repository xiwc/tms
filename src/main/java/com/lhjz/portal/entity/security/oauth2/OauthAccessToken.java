package com.lhjz.portal.entity.security.oauth2;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class OauthAccessToken implements Serializable {

	private static final long serialVersionUID = 7609852366022754294L;
	@Id
	private String authenticationId;
	private String tokenId;
	private Byte[] token;
	private String userName;
	private String clientId;
	private Byte[] authentication;
	private String refreshToken;

	public String getAuthenticationId() {
		return authenticationId;
	}

	public void setAuthenticationId(String authenticationId) {
		this.authenticationId = authenticationId;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public Byte[] getToken() {
		return token;
	}

	public void setToken(Byte[] token) {
		this.token = token;
	}

	public Byte[] getAuthentication() {
		return authentication;
	}

	public void setAuthentication(Byte[] authentication) {
		this.authentication = authentication;
	}

}
