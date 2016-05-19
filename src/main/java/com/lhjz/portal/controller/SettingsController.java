/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.controller;

import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lhjz.portal.base.BaseController;
import com.lhjz.portal.entity.Settings;
import com.lhjz.portal.model.RespBody;
import com.lhjz.portal.pojo.Enum.Action;
import com.lhjz.portal.pojo.Enum.Target;
import com.lhjz.portal.pojo.SettingsForm;
import com.lhjz.portal.repository.SettingsRepository;
import com.lhjz.portal.util.EnumUtil;
import com.lhjz.portal.util.WebUtil;

/**
 * 
 * @author xi
 * 
 * @date 2015年3月28日 下午1:19:05
 * 
 */
@Controller
@RequestMapping("admin/settings")
public class SettingsController extends BaseController {

	static final Logger logger = LoggerFactory
			.getLogger(SettingsController.class);

	@Autowired
	SettingsRepository settingsRepository;

	@RequestMapping(value = "save", method = RequestMethod.POST)
	@ResponseBody
	public RespBody save(@Valid SettingsForm settingsForm,
			BindingResult bindingResult) {

		logger.debug("Enter method: {}", "save");

		if (bindingResult.hasErrors()) {
			return RespBody.failed(bindingResult.getAllErrors().stream()
					.map(err -> err.getDefaultMessage())
					.collect(Collectors.joining("<br/>")));
		}

		Settings settings = new Settings();
		settings.setPage(EnumUtil.page(settingsForm.getPage()));
		settings.setModule(EnumUtil.module(settingsForm.getModule()));
		settings.setImgUrl(settingsForm.getImgUrl());
		settings.setUsername(WebUtil.getUsername());
		settings.setLink(settingsForm.getLink());
		settings.setMore(settingsForm.getMore());
		settings.setContent(settingsForm.getContent());
		settings.setDetail(settingsForm.getDetail());
		settings.setTitle(settingsForm.getTitle());

		Settings settings2 = settingsRepository.save(settings);

		log(Action.Create, Target.Settings, settings2);

		return RespBody.succeed(settings2);
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	@ResponseBody
	public RespBody delete(@RequestParam(value = "id", required = true) Long id) {

		logger.debug("Enter method: {}", "delete");

		settingsRepository.delete(id);

		log(Action.Delete, Target.Settings, id);

		return RespBody.succeed(id);
	}
	
	@RequestMapping(value = "update", method = RequestMethod.POST)
	@ResponseBody
	public RespBody update(
			@RequestParam(value = "id", required = true) Long id,
			SettingsForm settingsForm) {

		logger.debug("Enter method: {}", "update");

		Settings settings = settingsRepository.findOne(id);

		if (settings == null) {
			return RespBody.failed("更新设置不存在,或已经被删除!");
		}

		settings.setTitle(settingsForm.getTitle());
		settings.setContent(settingsForm.getContent());
		settings.setLink(settingsForm.getLink());
		settings.setMore(settingsForm.getMore());

		Settings settings2 = settingsRepository.saveAndFlush(settings);

		log(Action.Update, Target.Settings, settings);

		return RespBody.succeed(settings2);
	}

	@RequestMapping(value = "find", method = RequestMethod.POST)
	@ResponseBody
	public RespBody find(@RequestParam(value = "id", required = true) Long id) {

		logger.debug("Enter method: {}", "find");

		Settings settings = settingsRepository.findOne(id);

		if (settings == null) {
			return RespBody.failed("更新设置不存在,或已经被删除!");
		}

		log(Action.Read, Target.Settings, id);

		return RespBody.succeed(settings);
	}
}
