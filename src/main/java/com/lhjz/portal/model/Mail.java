/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.lhjz.portal.entity.Translate;
import com.lhjz.portal.entity.security.User;

/**
 * 
 * @author xi
 * 
 * @date 2016年5月20日 下午8:38:54
 * 
 */
public class Mail {

	private Set<String> set = new HashSet<String>();

	private Mail() {
	}

	public static Mail instance() {
		return new Mail();
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

		Set<User> watchers = translate.getProject().getWatchers();
		Set<String> watcherMails = watchers.stream().map((user) -> {
			return user.getMails();
		}).collect(Collectors.toSet());

		this.addAll(watcherMails);

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
		return this.set.toArray(new String[0]);
	}

}
