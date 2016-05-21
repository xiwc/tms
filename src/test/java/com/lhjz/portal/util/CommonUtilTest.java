package com.lhjz.portal.util;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CommonUtilTest {

	@Test
	public void replaceLinebreak() {
		String winString = "你好!\r\n好的啊\ndfgdfg";
		System.out.println(winString);
		String html = CommonUtil.replaceLinebreak(winString);
		System.out.println(html);
		Assert.assertTrue(html.contains("<br/>"));
	}
}
