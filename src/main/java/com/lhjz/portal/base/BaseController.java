/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.base;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.lhjz.portal.entity.Log;
import com.lhjz.portal.entity.security.User;
import com.lhjz.portal.model.Message;
import com.lhjz.portal.pojo.Enum.Action;
import com.lhjz.portal.pojo.Enum.Target;
import com.lhjz.portal.repository.LogRepository;
import com.lhjz.portal.repository.UserRepository;
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
	UserRepository userRepository;

	@Autowired
	protected Environment env;

	protected Log log(Action action, Target target, String targetId,
			Object... vals) {

		return logWithProperties(action, target, targetId, null, vals);
	}

	protected Log log(Action action, Target target, Long targetId,
			Object... vals) {

		return logWithProperties(action, target, targetId, null, vals);
	}

	protected User getLoginUser() {
		return userRepository.findOne(WebUtil.getUsername());
	}

	protected User getUser(String username) {
		return userRepository.findOne(username);
	}

	protected Log logWithProperties(Action action, Target target,
			Long targetId, String properties, Object... vals) {

		return logWithProperties(action, target, String.valueOf(targetId),
				properties, vals);
	}

	protected Log logWithProperties(Action action, Target target,
			String targetId,
			String properties, Object... vals) {

		Log log = new Log();
		log.setAction(action);
		log.setTarget(target);
		log.setCreateDate(new Date());
		log.setProperties(properties);

		if (StringUtil.isNotEmpty(targetId)) {
			log.setTargetId(String.valueOf(targetId));
		}

		if (vals.length > 0) {
			log.setNewValue(String.valueOf(vals[0]));
		}

		if (vals.length > 1) {
			log.setOldValue(String.valueOf(vals[1]));
		}

		log.setCreator(getLoginUser());

		return logRepository.saveAndFlush(log);

	}

	@ExceptionHandler(Exception.class)
	public ModelAndView exceptionHandler(Exception ex) {

		return new ModelAndView("admin/error", "error", Message.error(
				ex.getMessage()).detail(ex.toString()));
	}
}
