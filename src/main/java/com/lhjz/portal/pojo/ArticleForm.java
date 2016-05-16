package com.lhjz.portal.pojo;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

public class ArticleForm {

	@NotBlank(message = "文章名称不能为空！")
	@Length(max = 255, message = "文章名称长度不能超过255！")
	private String name;
	@NotBlank(message = "文章内容不能为空！")
	@Length(max = 16777216, message = "文章内容过长！")
	private String content;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "ArticleForm [name=" + name + ", content=" + content + "]";
	}

}
