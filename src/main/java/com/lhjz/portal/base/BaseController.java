/**
 * 立衡脊柱版权所有 (lhjz)
 */
package com.lhjz.portal.base;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.lhjz.portal.entity.Log;
import com.lhjz.portal.model.Message;
import com.lhjz.portal.pojo.Enum.Action;
import com.lhjz.portal.pojo.Enum.Target;
import com.lhjz.portal.repository.LogRepository;
import com.lhjz.portal.util.StringUtil;
import com.lhjz.portal.util.WebUtil;

/**
 * 
 * @author weichx
 * 
 * @date Apr 2, 2015 2:59:47 PM
 * 
 */
public abstract class BaseController {

	@Autowired
	protected LogRepository logRepository;

	@Autowired
	protected Environment env;

	protected Log log(Action action, Target target, Object... vals) {

		return logWithProperties(action, target, null, vals);

	}

	protected Log logWithProperties(Action action, Target target,
			String properties,
			Object... vals) {

		Log log = new Log();
		log.setAction(action);
		log.setTarget(target);
		log.setCreateDate(new Date());
		log.setProperties(properties);

		if (vals.length > 0) {
			log.setNewValue(String.valueOf(vals[0]));
		}

		if (vals.length > 1) {
			log.setOldValue(String.valueOf(vals[1]));
		}

		String username = WebUtil.getUsername();
		if (StringUtil.isEmpty(username)) {
			username = env.getProperty("lhjz.anonymous.user.name",
					"anonymous_user");
		}
		log.setUsername(username);

		return logRepository.saveAndFlush(log);

	}

	@ExceptionHandler(Exception.class)
	public ModelAndView exceptionHandler(Exception ex) {

		return new ModelAndView("admin/error", "error", Message.error(ex.getMessage()));
	}
}
