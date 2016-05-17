package com.lhjz.portal.pojo;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

public class TranslateForm {

	@NotBlank(message = "翻译名称不能为空！")
	@Length(max = 255, message = "翻译名称长度不能超过255！")
	private String key;

	@NotBlank(message = "描述不能为空！")
	@Length(max = 2000, message = "描述过长！")
	private String desc;

	private String content;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "TranslateForm [key=" + key + ", desc=" + desc + ", content=" + content + "]";
	}

}
