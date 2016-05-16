package com.lhjz.portal.pojo;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

public class JobForm {

	@NotBlank(message = "职位类别不能为空!")
	private String category;
	@Min(value = 1, message = "职位数设置不能小于1!")
	@Max(value = 100, message = "职位数设置不能大于100!")
	@NotNull(message = "职位数设置不能为空!")
	private Integer size;
	@NotBlank(message = "工作经验不能为空!")
	private String experience;
	@NotBlank(message = "学历要求不能为空!")
	private String education;
	@NotBlank(message = "转正工资不能为空!")
	private String salary;
	@NotBlank(message = "职位标签不能为空!")
	private String labels;
	@NotBlank(message = "岗位职责不能为空!")
	private String duty;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
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

	public String getDuty() {
		return duty;
	}

	public void setDuty(String duty) {
		this.duty = duty;
	}

	@Override
	public String toString() {
		return "JobForm [experience=" + experience + ", education=" + education
				+ ", salary=" + salary + ", labels=" + labels + ", duty="
				+ duty + "]";
	}

}
