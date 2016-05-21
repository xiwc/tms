package com.lhjz.portal.util;

import org.apache.log4j.Logger;

public class CommonUtil {

	private static final Logger logger = Logger.getLogger(CommonUtil.class);

	/**
	 * 转换win换行符成html<br/>
	 * 
	 * @param winString
	 * @return
	 */
	public static String replaceLinebreak(String winString) {
		if (winString != null) {
			return winString.replaceAll("\n", "<br/>");
		}
		return StringUtil.EMPTY;
	}
}
