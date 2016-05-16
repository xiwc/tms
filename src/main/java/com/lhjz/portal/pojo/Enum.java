package com.lhjz.portal.pojo;

/**
 * 
 * @author xi
 * 
 * @date 2015年4月25日 上午11:25:00
 * 
 */
public class Enum {

	public static enum Status {
		Unknow, Normal, Deleted, Bultin, New, Opening, Analyzing, Accepted, Processing, Resolved, Closed, Ignored, Failed, Checked;
	}

	public static enum Action {
		Create, Read, Update, Delete, Upload, Visit;
	}

	public static enum Target {
		Article, File, Feedback, Diagnose, Settings, Page, User, Authority, Config, Feature, Case, Product, Env, Health, Job, JobApply;
	}

	public static enum Page {
		Unknow, Index, About, Feature, Case, Product, Env, Health, Job, Diagnose, Contact;
	}

	public static enum Module {
		Unknow, BigImg, HotNews, MoreNews, Branch, Expert, More, Introduction;
	}

	public static enum Key {
		Unknow, Contact, PageEnable;
	}

	public static enum Role {
		ROLE_USER, ROLE_ADMIN;
	}
}
