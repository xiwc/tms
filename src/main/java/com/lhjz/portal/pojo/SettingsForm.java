package com.lhjz.portal.pojo;

import org.hibernate.validator.constraints.NotBlank;

public class SettingsForm {

	@NotBlank
	private String page;
	@NotBlank
	private String module;

	private String imgUrl;
	private String title;
	private String content;
	private String detail;
	private String link;
	private String more;
	private double index;

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getMore() {
		return more;
	}

	public void setMore(String more) {
		this.more = more;
	}

	public double getIndex() {
		return index;
	}

	public void setIndex(double index) {
		this.index = index;
	}

	@Override
	public String toString() {
		return "SettingsForm [page=" + page + ", module=" + module
				+ ", imgUrl=" + imgUrl + ", title=" + title + ", content="
				+ content + ", detail=" + detail + ", link=" + link + ", more="
				+ more + ", index=" + index + "]";
	}

}
