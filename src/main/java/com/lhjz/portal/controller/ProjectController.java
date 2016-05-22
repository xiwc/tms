/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lhjz.portal.base.BaseController;
import com.lhjz.portal.entity.Label;
import com.lhjz.portal.entity.Language;
import com.lhjz.portal.entity.Project;
import com.lhjz.portal.entity.Translate;
import com.lhjz.portal.entity.TranslateItem;
import com.lhjz.portal.model.RespBody;
import com.lhjz.portal.pojo.Enum.Action;
import com.lhjz.portal.pojo.Enum.Status;
import com.lhjz.portal.pojo.Enum.Target;
import com.lhjz.portal.pojo.ProjectForm;
import com.lhjz.portal.repository.AuthorityRepository;
import com.lhjz.portal.repository.LabelRepository;
import com.lhjz.portal.repository.LanguageRepository;
import com.lhjz.portal.repository.ProjectRepository;
import com.lhjz.portal.repository.TranslateItemRepository;
import com.lhjz.portal.repository.TranslateRepository;
import com.lhjz.portal.util.WebUtil;

/**
 * 
 * @author xi
 * 
 * @date 2015年3月28日 下午1:19:05
 * 
 */
@Controller
@RequestMapping("admin/project")
public class ProjectController extends BaseController {

	static Logger logger = LoggerFactory.getLogger(ProjectController.class);

	@Autowired
	TranslateRepository translateRepository;

	@Autowired
	TranslateItemRepository translateItemRepository;

	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	LanguageRepository languageRepository;

	@Autowired
	LabelRepository labelRepository;

	@Autowired
	AuthorityRepository authorityRepository;

	private boolean isContainsMainLanguage(ProjectForm projectForm) {

		String[] lngs = projectForm.getLanguages().split(",");
		for (String lng : lngs) {
			if (lng.equals(String.valueOf(projectForm.getLanguage()))) {
				return true;
			}
		}
		return false;
	}

	@RequestMapping(value = "create", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_SUPER", "ROLE_ADMIN" })
	public RespBody create(@Valid ProjectForm projectForm,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return RespBody.failed(bindingResult.getAllErrors().stream()
					.map(err -> err.getDefaultMessage())
					.collect(Collectors.joining("<br/>")));
		}

		if (!isContainsMainLanguage(projectForm)) {
			return RespBody.failed("设置语言必须包含主语言!");
		}

		Project project = new Project();
		project.setCreateDate(new Date());
		project.setCreator(WebUtil.getUsername());
		project.setDescription(projectForm.getDesc());
		project.setName(projectForm.getName());
		project.setStatus(Status.New);
		project.setLanguage(languageRepository.findOne(projectForm
				.getLanguage()));

		String[] lngArr = projectForm.getLanguages().split(",");
		List<Long> collect = Arrays.asList(lngArr).stream().map((lng) -> {
			return Long.valueOf(lng);
		}).collect(Collectors.toList());

		List<Language> languages = languageRepository.findAll(collect);

		project.getLanguages().addAll(languages);

		Project project2 = projectRepository.saveAndFlush(project);

		for (Language language : languages) {
			language.getProjects().add(project2);
		}

		languageRepository.save(languages);
		languageRepository.flush();

		log(Action.Create, Target.Project, projectForm);

		return RespBody.succeed(project2);
	}

	private boolean isExistLanguage(Set<Language> lngs, Language lng) {

		for (Language language : lngs) {
			if (language.getId().equals(lng.getId())) {
				return true;
			}
		}
		return false;
	}

	@RequestMapping(value = "update", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_SUPER", "ROLE_ADMIN" })
	public RespBody update(@RequestParam("id") Long id,
			@Valid ProjectForm projectForm, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return RespBody.failed(bindingResult.getAllErrors().stream()
					.map(err -> err.getDefaultMessage())
					.collect(Collectors.joining("<br/>")));
		}

		if (!isContainsMainLanguage(projectForm)) {
			return RespBody.failed("设置语言必须包含主语言!");
		}

		Project project = projectRepository.findOne(id);

		project.setDescription(projectForm.getDesc());
		project.setName(projectForm.getName());
		project.setStatus(Status.Updated);
		project.setUpdateDate(new Date());
		project.setUpdater(WebUtil.getUsername());

		if (!project.getLanguage().getId().equals(projectForm.getLanguage())) {
			project.setLanguage(languageRepository.findOne(projectForm
					.getLanguage()));
		}

		String[] lngArr = projectForm.getLanguages().split(",");
		List<Long> collect = Arrays.asList(lngArr).stream().map((lng) -> {
			return Long.valueOf(lng);
		}).collect(Collectors.toList());

		Set<Language> languages = project.getLanguages();
		for (Language language : languages) {
			// 语言不存在
			if (!collect.contains(language.getId())) {
				language.getProjects().remove(project);
			}
		}

		HashSet<Language> languages2 = new HashSet<Language>(
				languageRepository.findAll(collect));

		for (Language language : languages2) {
			if (!isExistLanguage(languages, language)) {
				language.getProjects().add(project);
			}
		}

		languageRepository.save(languages);
		languageRepository.save(languages2);
		languageRepository.flush();

		project.setLanguages(languages2);

		projectRepository.saveAndFlush(project);

		log(Action.Update, Target.Project, projectForm);

		return RespBody.succeed(project);
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_SUPER", "ROLE_ADMIN" })
	public RespBody delete(@RequestParam("id") Long id) {

		Project project = projectRepository.findOne(id);

		if (project == null) {
			return RespBody.failed("删除项目不存在！");
		}

		Set<Language> languages = project.getLanguages();
		for (Language language : languages) {
			language.getProjects().remove(project);
		}

		languageRepository.save(languages);
		languageRepository.flush();

		Set<Translate> translates = project.getTranslates();
		Set<TranslateItem> translateItems = new HashSet<TranslateItem>();
		Set<Label> labels = new HashSet<Label>();
		translates.stream().forEach((t) -> {
			Set<TranslateItem> translateItems2 = t.getTranslateItems();
			translateItems2.stream().forEach((ti) -> {
				ti.setTranslate(null);
			});
			translateItems.addAll(translateItems2);

			Set<Label> labels2 = t.getLabels();
			labels2.stream().forEach((l) -> {
				l.setTranslate(null);
			});
			labels.addAll(labels2);

			t.setProject(null);
		});

		translateItemRepository.deleteInBatch(translateItems);
		translateItemRepository.flush();

		labelRepository.deleteInBatch(labels);
		labelRepository.flush();

		translateRepository.deleteInBatch(translates);
		translateRepository.flush();

		// project.setTranslates(null);

		projectRepository.delete(project);
		projectRepository.flush();

		log(Action.Delete, Target.Project, id);

		return RespBody.succeed(id);
	}

	@RequestMapping(value = "get", method = RequestMethod.GET)
	@ResponseBody
	@Secured({ "ROLE_SUPER", "ROLE_ADMIN" })
	public RespBody get(@RequestParam("id") Long id) {

		Project project = projectRepository.findOne(id);

		if (project == null) {
			return RespBody.failed("获取项目不存在！");
		}

		log(Action.Read, Target.Project, id);

		return RespBody.succeed(project);
	}

}
