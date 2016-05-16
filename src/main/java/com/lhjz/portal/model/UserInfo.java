/**
 * 立衡脊柱版权所有 (lhjz)
 */
package com.lhjz.portal.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.lhjz.portal.pojo.Enum.Status;

/**
 * 
 * @author xi
 * 
 * @date 2015年6月7日 下午6:25:21
 * 
 */
public class UserInfo implements Serializable {

	/** serialVersionUID long */
	private static final long serialVersionUID = -5501393570981445761L;
	private String username;
	private String password;
	private boolean enabled;

	private Status status = Status.Normal;
	private Date createDate;
	private long version;

	private Set<String> authorities = new HashSet<String>(0);

	public UserInfo() {
	}

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

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public Set<String> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Set<String> authorities) {
		this.authorities = authorities;
	}

	@Override
	public String toString() {
		return "UserInfo [username=" + username + ", password=" + password
				+ ", enabled=" + enabled + ", status=" + status
				+ ", createDate=" + createDate + ", version=" + version
				+ ", authorities=" + authorities + "]";
	}

}
