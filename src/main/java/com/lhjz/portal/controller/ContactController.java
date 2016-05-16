/**
 * 立衡脊柱版权所有 (lhjz)
 */
package com.lhjz.portal.controller;

import java.util.Date;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lhjz.portal.base.BaseController;
import com.lhjz.portal.entity.Config;
import com.lhjz.portal.model.RespBody;
import com.lhjz.portal.pojo.ContactForm;
import com.lhjz.portal.pojo.Enum.Action;
import com.lhjz.portal.pojo.Enum.Key;
import com.lhjz.portal.pojo.Enum.Target;
import com.lhjz.portal.repository.ConfigRepository;
import com.lhjz.portal.util.BeanUtil;
import com.lhjz.portal.util.EnumUtil;
import com.lhjz.portal.util.JsonUtil;
import com.lhjz.portal.util.WebUtil;

/**
 * 
 * @author xi
 * 
 * @date 2015年3月28日 下午1:19:05
 * 
 */
@Controller
@RequestMapping("admin/config")
public class ContactController extends BaseController {

	static Logger logger = LoggerFactory.getLogger(ContactController.class);

	@Autowired
	ConfigRepository configRepository;

	@RequestMapping(value = "save", method = RequestMethod.POST)
	@ResponseBody
	public RespBody save(@Valid ContactForm contactForm,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return RespBody.failed(bindingResult.getAllErrors().stream()
					.map(err -> err.getDefaultMessage())
					.collect(Collectors.joining("<br/>")));
		}

		Config config2 = configRepository.findFirstByKey(Key.Contact);

		if (config2 != null) {
			logger.error("保存配置已经存在, Key: {}", Key.Contact.name());
			return RespBody.failed("保存配置已经存在!");
		}

		Config config = new Config();
		config.setKey(Key.Contact);
		config.setValue(JsonUtil.toJson(contactForm));
		config.setUsername(WebUtil.getUsername());

		configRepository.saveAndFlush(config);

		log(Action.Create, Target.Config, config);

		return RespBody.succeed();
	}

	@RequestMapping(value = "saveOrUpdate", method = RequestMethod.POST)
	@ResponseBody
	public RespBody saveOrUpdate(@Valid ContactForm contactForm,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return RespBody.failed(bindingResult.getAllErrors().stream()
					.map(err -> err.getDefaultMessage())
					.collect(Collectors.joining("<br/>")));
		}

		Config config2 = configRepository.findFirstByKey(Key.Contact);

		if (config2 != null) {
			ContactForm contactForm2 = JsonUtil.json2Object(config2.getValue(),
					ContactForm.class);

			BeanUtil.copyNotEmptyFields(contactForm, contactForm2);

			config2.setValue(JsonUtil.toJson(contactForm2));
			config2.setUpdateDate(new Date());
			config2.setUsername(WebUtil.getUsername());

			configRepository.saveAndFlush(config2);

			log(Action.Update, Target.Config, config2);
		} else {
			Config config = new Config();
			config.setKey(Key.Contact);
			config.setValue(JsonUtil.toJson(contactForm));
			config.setUsername(WebUtil.getUsername());

			configRepository.saveAndFlush(config);

			log(Action.Create, Target.Config, config);
		}

		return RespBody.succeed();
	}

	@RequestMapping(value = "update", method = RequestMethod.POST)
	@ResponseBody
	public RespBody update(@Valid ContactForm contactForm,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return RespBody.failed(bindingResult.getAllErrors().stream()
					.map(err -> err.getDefaultMessage())
					.collect(Collectors.joining("<br/>")));
		}

		Config config = configRepository.findFirstByKey(Key.Contact);

		if (config == null) {
			logger.error("更新配置不存在, Key: {}", Key.Contact.name());
			return RespBody.failed("更新配置不存在!");
		}

		ContactForm contactForm2 = JsonUtil.json2Object(config.getValue(),
				ContactForm.class);

		BeanUtil.copyNotEmptyFields(contactForm, contactForm2);

		config.setValue(JsonUtil.toJson(contactForm2));
		config.setUpdateDate(new Date());
		config.setUsername(WebUtil.getUsername());

		configRepository.saveAndFlush(config);

		log(Action.Update, Target.Config, config);

		return RespBody.succeed();
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	@ResponseBody
	public RespBody delete(String key) {

		Key k = EnumUtil.key(key);

		Config config = configRepository.findFirstByKey(k);

		if (config == null) {
			logger.error("删除配置不存在, Key: {}", k.name());
			return RespBody.failed("删除配置不存在!");
		}

		configRepository.delete(config);

		log(Action.Delete, Target.Config, config);

		return RespBody.succeed();
	}

}
