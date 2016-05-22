package com.lhjz.portal.pojo;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

public class ProjectForm {

	@NotBlank(message = "项目名称不能为空！")
	@Length(min = 3, max = 10, message = "项目名称长度需要大于2且不能超过10！")
	@Pattern(regexp = "^[A-Z][A-Z0-9_]+[A-Z0-9]$", message = "项目名称必须是[A-Z_]组合,而且需要以[A-Z]开头和以[A-Z0-9]结尾!")
	private String name;

	@Length(max = 2000, message = "项目描述过长！")
	private String desc;

	@NotBlank(message = "项目语言不能为空！")
	private String languages;

	@NotNull(message = "项目主语言不能为空！")
	private Long language;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getLanguages() {
		return languages;
	}

	public void setLanguages(String languages) {
		this.languages = languages;
	}

	public Long getLanguage() {
		return language;
	}

	public void setLanguage(Long language) {
		this.language = language;
	}

	@Override
	public String toString() {
		return "ProjectForm [name=" + name + ", desc=" + desc + ", languages="
				+ languages + ", language=" + language + "]";
	}

}
