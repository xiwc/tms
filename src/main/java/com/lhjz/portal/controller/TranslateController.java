/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.controller;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonObject;
import com.lhjz.portal.base.BaseController;
import com.lhjz.portal.entity.Language;
import com.lhjz.portal.entity.Project;
import com.lhjz.portal.entity.Translate;
import com.lhjz.portal.entity.TranslateItem;
import com.lhjz.portal.model.RespBody;
import com.lhjz.portal.pojo.Enum.Action;
import com.lhjz.portal.pojo.Enum.Status;
import com.lhjz.portal.pojo.Enum.Target;
import com.lhjz.portal.pojo.TranslateForm;
import com.lhjz.portal.pojo.TranslateItemForm;
import com.lhjz.portal.repository.AuthorityRepository;
import com.lhjz.portal.repository.ProjectRepository;
import com.lhjz.portal.repository.TranslateItemRepository;
import com.lhjz.portal.repository.TranslateRepository;
import com.lhjz.portal.util.JsonUtil;
import com.lhjz.portal.util.StringUtil;
import com.lhjz.portal.util.WebUtil;

/**
 * 
 * @author xi
 * 
 * @date 2015年3月28日 下午1:19:05
 * 
 */
@Controller
@RequestMapping("admin/translate")
public class TranslateController extends BaseController {

	static Logger logger = LoggerFactory.getLogger(TranslateController.class);

	@Autowired
	TranslateRepository translateRepository;

	@Autowired
	TranslateItemRepository translateItemRepository;

	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	AuthorityRepository authorityRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@RequestMapping(value = "save", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_SUPER", "ROLE_ADMIN", "ROLE_USER" })
	public RespBody save(@RequestParam("projectId") Long projectId, @Valid TranslateForm translateForm,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return RespBody.failed(bindingResult.getAllErrors().stream().map(err -> err.getDefaultMessage())
					.collect(Collectors.joining("<br/>")));
		}

		Project project = projectRepository.findOne(projectId);

		if (translateRepository.findByKeyAndProject(translateForm.getKey(), project).size() > 0) {
			logger.error("添加名称已经存在, ID: {}", translateForm.getKey());
			return RespBody.failed("添加名称已经存在!");
		}

		Translate translate = new Translate();
		translate.setKey(translateForm.getKey());
		translate.setProject(project);
		translate.setCreateDate(new Date());
		translate.setCreator(WebUtil.getUsername());
		translate.setDescription(translateForm.getDesc());
		translate.setStatus(Status.New);

		JsonObject jsonO = (JsonObject) JsonUtil.toJsonElement(translateForm.getContent());
		Set<Language> lngs = project.getLanguages();

		for (Language language : lngs) {
			TranslateItem item = new TranslateItem();
			String name = language.getName();
			if (jsonO.has(name)) {
				item.setContent(jsonO.get(name).getAsString());
			} else {
				item.setContent(StringUtil.EMPTY);
			}
			item.setCreateDate(new Date());
			item.setCreator(WebUtil.getUsername());
			item.setLanguage(language);
			item.setStatus(Status.New);
			item.setTranslate(translate);

			translate.getTranslateItems().add(item);
		}

		translateRepository.saveAndFlush(translate);

		log(Action.Create, Target.Translate, translate);

		return RespBody.succeed(translate);
	}

	@RequestMapping(value = "update", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_SUPER", "ROLE_ADMIN", "ROLE_USER" })
	public RespBody update(@RequestParam("id") Long id, TranslateItemForm translateItemForm) {

		TranslateItem translateItem = translateItemRepository.findOne(id);

		if (translateItem != null) {
			translateItem.setContent(translateItemForm.getContent());

			translateItemRepository.saveAndFlush(translateItem);

			Translate translate = translateItem.getTranslate();
			translate.setStatus(Status.Updated);

			translateRepository.saveAndFlush(translate);
		} else {
			logger.error("更新翻译条目不存在! ID: {}", id);
			return RespBody.failed("更新翻译条目不存在!");
		}

		return RespBody.succeed(id);
	}

	private TranslateItem getExitTranslateItem(String lngName, Translate translate) {

		Set<TranslateItem> translateItems = translate.getTranslateItems();
		for (TranslateItem translateItem : translateItems) {
			if (translateItem.getLanguage().getName().equals(lngName)) {
				return translateItem;
			}
		}
		return null;
	}

	@RequestMapping(value = "update2", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_SUPER", "ROLE_ADMIN", "ROLE_USER" })
	public RespBody update2(@RequestParam("id") Long id, @Valid TranslateForm translateForm,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return RespBody.failed(bindingResult.getAllErrors().stream().map(err -> err.getDefaultMessage())
					.collect(Collectors.joining("<br/>")));
		}

		Translate translate = translateRepository.findOne(id);

		if (translate != null) {
			translate.setKey(translateForm.getKey());
			translate.setDescription(translateForm.getDesc());
			translate.setStatus(Status.Updated);

			JsonObject jsonO = (JsonObject) JsonUtil.toJsonElement(translateForm.getContent());
			Set<Language> lngs = translate.getProject().getLanguages();

			for (Language language : lngs) {

				String name = language.getName();
				String content = StringUtil.EMPTY;
				if (jsonO.has(name)) {
					content = jsonO.get(name).getAsString();
				}

				TranslateItem exitTranslateItem = getExitTranslateItem(name, translate);
				if (exitTranslateItem != null) {
					exitTranslateItem.setContent(content);
					exitTranslateItem.setStatus(Status.Updated);

					translateItemRepository.saveAndFlush(exitTranslateItem);
				} else {
					TranslateItem item = new TranslateItem();
					item.setContent(content);
					item.setCreateDate(new Date());
					item.setCreator(WebUtil.getUsername());
					item.setLanguage(language);
					item.setStatus(Status.New);
					item.setTranslate(translate);

					translateItemRepository.saveAndFlush(item);

					translate.getTranslateItems().add(item);
				}
			}

			log(Action.Update, Target.Translate, translate);
			translateRepository.saveAndFlush(translate);
		} else {
			logger.error("更新翻译不存在! ID: {}", id);
			return RespBody.failed("更新翻译不存在!");
		}

		return RespBody.succeed(id);
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_SUPER", "ROLE_ADMIN" })
	public RespBody delete(@RequestParam("id") Long id) {

		translateRepository.delete(id);

		return RespBody.succeed(id);
	}

	@RequestMapping(value = "get", method = RequestMethod.GET)
	@ResponseBody
	@Secured({ "ROLE_SUPER", "ROLE_ADMIN", "ROLE_USER" })
	public RespBody get(@RequestParam("projectId") Long projectId,
			@PageableDefault Pageable pageable) {

		Project project = projectRepository.findOne(projectId);

		if (project == null) {
			logger.error("项目不存在! ID: {}", projectId);
			return RespBody.failed("项目不存在!");
		}

		Page<Translate> page = translateRepository.findByProject(project,
				pageable);

		return RespBody.succeed(page);
	}

	@RequestMapping(value = "getById", method = RequestMethod.GET)
	@ResponseBody
	@Secured({ "ROLE_SUPER", "ROLE_ADMIN", "ROLE_USER" })
	public RespBody getById(@RequestParam("id") Long id) {

		Translate translate = translateRepository.findOne(id);

		if (translate == null) {
			logger.error("翻译不存在! ID: {}", id);
			return RespBody.failed("翻译不存在!");
		}

		return RespBody.succeed(translate);
	}
}
