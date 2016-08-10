/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.lhjz.portal.entity.security.User;
import com.lhjz.portal.pojo.Enum.Status;

/**
 * 
 * @author xi
 * 
 * @date 2015年3月28日 下午2:03:20
 * 
 */
@Entity
public class ChatStow implements Serializable {

	private static final long serialVersionUID = 7164999687714543043L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	@JoinColumn(name = "chat_id")
	private Chat chat;

	@ManyToOne
	@JoinColumn(name = "stow_user")
	private User stowUser;

	@ManyToOne
	@JoinColumn(name = "creator")
	private User creator;

	@ManyToOne
	@JoinColumn(name = "updater")
	private User updater;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status = Status.New;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate = new Date();

	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;

	@Version
	private long version;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Chat getChat() {
		return chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	public User getStowUser() {
		return stowUser;
	}

	public void setStowUser(User stowUser) {
		this.stowUser = stowUser;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public User getUpdater() {
		return updater;
	}

	public void setUpdater(User updater) {
		this.updater = updater;
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

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "ChatStow [id=" + id + ", chat=" + chat + ", stowUser="
				+ stowUser + ", creator=" + creator + ", updater=" + updater
				+ ", status=" + status + ", createDate=" + createDate
				+ ", updateDate=" + updateDate + ", version=" + version + "]";
	}

}
