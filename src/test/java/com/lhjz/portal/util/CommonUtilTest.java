package com.lhjz.portal.util;

import java.util.ArrayList;
import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.lhjz.portal.pojo.Enum.Status;

public class CommonUtilTest {

	@Test
	public void replaceLinebreak() {
		String winString = "你好!\r\n好的啊\ndfgdfg";
		System.out.println(winString);
		String html = CommonUtil.replaceLinebreak(winString);
		System.out.println(html);
		Assert.assertTrue(html.contains("<br/>"));
	}

	@Test
	public void regex() {
		Assert.assertTrue("aa1".matches("^[a-z][a-z0-9]{2,3}$"));
		Assert.assertTrue("aaa1".matches("^[a-z][a-z0-9]{2,3}$"));
		Assert.assertFalse("a1".matches("^[a-z][a-z0-9]{2,3}$"));
		Assert.assertFalse("aaaa1".matches("^[a-z][a-z0-9]{2,3}$"));
	}

	@Test
	public void format() {
		String d = DateUtil.format(new Date(), DateUtil.FORMAT7);
		System.out.println(d);
	}

	@Test
	public void status() {
		System.out.println(Status.valueOf("New"));
		// System.out.println(Status.valueOf("New_"));
	}

	@Test
	public void stringJoin() {
		System.out.println(StringUtil.join(",", new ArrayList<>()));
		Assert.assertEquals(StringUtil.join(",", new ArrayList<>()), "");
	}

	// public static void main(String[] args) {
	// String d = DateUtil.format(new Date(), DateUtil.FORMAT7);
	// System.out.println(d);
	// }
}
