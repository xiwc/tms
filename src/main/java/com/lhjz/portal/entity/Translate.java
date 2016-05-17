/**
 * 立衡脊柱版权所有 (lhjz)
 */
package com.lhjz.portal.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.persistence.CascadeType;

import org.hibernate.validator.constraints.NotBlank;

import com.lhjz.portal.pojo.Enum.Status;

/**
 * 
 * @author xi
 * 
 * @date 2015年3月28日 下午2:03:20
 * 
 */
@Entity
public class Translate implements Serializable {

	/** serialVersionUID (long) */
	private static final long serialVersionUID = 4730479799042412659L;

	@Id
	@GeneratedValue
	private Long id;

	@NotBlank
	@Column(nullable = false, name = "_key")
	private String key;

	@Column(length = 2000)
	private String description;

	@NotBlank
	private String creator;

	@Version
	private long version;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status = Status.Normal;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	@ManyToOne
	@JoinColumn(name = "project_id")
	private Project project;

	@OneToMany(mappedBy = "translate", cascade = { CascadeType.ALL })
	private Set<TranslateItem> translateItems = new HashSet<TranslateItem>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
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

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Set<TranslateItem> getTranslateItems() {
		return translateItems;
	}

	public void setTranslateItems(Set<TranslateItem> translateItems) {
		this.translateItems = translateItems;
	}

	@Override
	public String toString() {
		return "Translate [id=" + id + ", key=" + key + ", description=" + description + ", creator=" + creator
				+ ", version=" + version + ", status=" + status + ", createDate=" + createDate + "]";
	}

}
