/**
 * 立衡脊柱版权所有 (lhjz)
 */
package com.lhjz.portal.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.lhjz.portal.pojo.Enum.Status;

/**
 * 
 * @author xi
 * 
 * @date 2015年3月28日 下午2:03:20
 * 
 */
@Entity
public class Job implements Serializable {

	/** serialVersionUID (long) */
	private static final long serialVersionUID = -7244230054736049452L;

	@Id
	@GeneratedValue
	private Long id;

	private String category;
	private int size;

	private String experience;
	private String education;
	private String salary;
	private String labels;
	@Column(length = 16777216)
	private String duty;
	private String link;

	@Transient
	private java.util.List<String> labelList = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status = Status.Normal;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate = new Date();

	@Version
	private long version;

	@OneToMany(mappedBy = "job")
	private Set<JobApply> jobApplies = new HashSet<JobApply>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public String getLabels() {
		return labels;
	}

	public void setLabels(String labels) {
		this.labels = labels;
	}

	public java.util.List<String> getLabelList() {
		return labelList;
	}

	public void setLabelList(java.util.List<String> labelList) {
		this.labelList = labelList;
	}

	public String getDuty() {
		return duty;
	}

	public void setDuty(String duty) {
		this.duty = duty;
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

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public String toString() {
		return "Job [id=" + id + ", category=" + category + ", size=" + size
				+ ", experience=" + experience + ", education=" + education
				+ ", salary=" + salary + ", labels=" + labels + ", duty="
				+ duty + ", link=" + link + ", status=" + status
				+ ", createDate=" + createDate + ", version=" + version + "]";
	}


}
