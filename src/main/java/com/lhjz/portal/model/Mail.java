/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lhjz.portal.entity.Label;
import com.lhjz.portal.entity.Translate;
import com.lhjz.portal.entity.TranslateItem;
import com.lhjz.portal.entity.security.User;
import com.lhjz.portal.pojo.TranslateForm;
import com.lhjz.portal.util.StringUtil;

/**
 * 
 * @author xi
 * 
 * @date 2016年5月20日 下午8:38:54
 * 
 */
public class Mail {

	static Logger logger = LoggerFactory.getLogger(Mail.class);

	private Set<String> set = new HashSet<String>();

	private Map<String, String> map = new HashMap<String, String>();

	private Mail() {
	}

	public static Mail instance() {
		return new Mail();
	}

	public Mail parseTranslateForm(TranslateForm translateForm) {

		this.put("翻译名称", translateForm.getKey());
		this.put("翻译标签", translateForm.getTags());

		return this;
	}

	public Mail parseTranslate(Translate translate) {

		map.put("翻译名称", translate.getKey());

		List<String> list = new ArrayList<String>();
		for (Label label : translate.getLabels()) {
			list.add(label.getName());
		}
		this.put("翻译标签", StringUtil.join(",", list));

		Set<TranslateItem> translateItems = translate.getTranslateItems();
		for (TranslateItem translateItem : translateItems) {
			String name = translateItem.getLanguage().getDescription() + "["
					+ translateItem.getLanguage().getName() + "]";
			this.put(name, translateItem.getContent());
		}

		return this;
	}

	public Mail put(String name, Object value) {

		map.put(name, String.valueOf(value));

		return this;
	}

	public String body() {

		List<String> list = new ArrayList<String>();
		for (String name : map.keySet()) {
			list.add(name + ": " + map.get(name));
		}

		return StringUtil.join("<br/>", list);
	}

	public Mail add(String... objs) {

		for (String object : objs) {
			set.add(object);
		}

		return this;
	}

	public Mail addUsers(User... users) {

		if (users != null) {
			for (User user : users) {
				this.add(user.getMails());
			}
		}

		return this;
	}

	public Mail addWatchers(Translate translate) {

		// 项目关注者
		Set<User> watchers = translate.getProject().getWatchers();
		Set<String> watcherMails = watchers.stream().map((user) -> {
			return user.getMails();
		}).collect(Collectors.toSet());

		this.addAll(watcherMails);

		// 翻译关注者
		Set<User> watchers2 = translate.getWatchers();
		Set<String> watcherMails2 = watchers2.stream().map((user) -> {
			return user.getMails();
		}).collect(Collectors.toSet());

		this.addAll(watcherMails2);

		return this;
	}

	public Mail addAll(Collection<String> collection) {

		if (collection != null) {
			for (String object : collection) {
				this.set.add(object);
			}
		}

		return this;
	}

	public Mail removeUser(User user) {

		this.set.remove(user.getMails());

		return this;
	}

	public String[] get() {

		String[] mails = this.set.toArray(new String[0]);

		logger.info("发送邮件对象: {}", StringUtil.join(",", mails));

		return mails;
	}

}