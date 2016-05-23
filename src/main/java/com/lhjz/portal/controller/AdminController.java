/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lhjz.portal.base.BaseController;
import com.lhjz.portal.entity.Label;
import com.lhjz.portal.entity.Language;
import com.lhjz.portal.entity.Project;
import com.lhjz.portal.entity.Translate;
import com.lhjz.portal.entity.security.Authority;
import com.lhjz.portal.entity.security.User;
import com.lhjz.portal.model.UserInfo;
import com.lhjz.portal.pojo.Enum.Status;
import com.lhjz.portal.repository.FileRepository;
import com.lhjz.portal.repository.LabelRepository;
import com.lhjz.portal.repository.LanguageRepository;
import com.lhjz.portal.repository.ProjectRepository;
import com.lhjz.portal.repository.TranslateRepository;
import com.lhjz.portal.repository.UserRepository;
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
@RequestMapping("admin")
public class AdminController extends BaseController {

	static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	FileRepository fileRepository;

	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	LanguageRepository languageRepository;

	@Autowired
	TranslateRepository translateRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	LabelRepository labelRepository;

	@RequestMapping("login")
	public String login(Model model) {

		logger.debug("Enter method: {}", "login");

		return "admin/login";
	}

	@RequestMapping()
	public String home(Model model) {

		model.addAttribute("cntProject", projectRepository.count());
		model.addAttribute("cntUser", userRepository.count());
		model.addAttribute("cntLanguage", languageRepository.count());
		model.addAttribute("cntTranslate", translateRepository.count());

		return "admin/index";
	}

	@RequestMapping("user")
	@Secured("ROLE_ADMIN")
	public String user(Model model) {

		logger.debug("Enter method...");

		List<User> users = userRepository.findAll();
		List<UserInfo> userInfos = new ArrayList<UserInfo>();
		for (User user : users) {
			UserInfo userInfo = new UserInfo();
			userInfo.setCreateDate(user.getCreateDate());
			userInfo.setEnabled(user.isEnabled());
			userInfo.setStatus(user.getStatus());
			userInfo.setUsername(user.getUsername());
			userInfo.setMails(user.getMails());
			userInfo.setName(user.getName());

			Set<String> authorities = new HashSet<String>();
			for (Authority authority : user.getAuthorities()) {
				authorities.add(authority.getId().getAuthority());
			}

			userInfo.setAuthorities(authorities);

			userInfos.add(userInfo);
		}

		model.addAttribute("users", userInfos);

		return "admin/user";
	}

	@RequestMapping("project")
	@Secured("ROLE_ADMIN")
	public String project(Model model) {

		logger.debug("Enter method...");

		List<Project> projects = projectRepository.findAll();

		List<Language> languages = languageRepository.findAll();

		List<User> users = userRepository.findAll();

		model.addAttribute("projects", projects);
		model.addAttribute("languages", languages);
		model.addAttribute("users", users);
		model.addAttribute("user", getLoginUser());

		return "admin/project";
	}

	@RequestMapping("language")
	@Secured("ROLE_ADMIN")
	public String language(Model model) {

		logger.debug("Enter method...");

		List<Language> languages = languageRepository.findAll();

		List<User> users = userRepository.findAll();

		model.addAttribute("languages", languages);
		model.addAttribute("users", users);
		model.addAttribute("user", getLoginUser());

		return "admin/language";
	}

	@RequestMapping("feedback")
	public String feedback(Model model) {
		return "admin/feedback";
	}

	@RequestMapping("translate")
	public String translate(
			Model model,
			@PageableDefault(sort = { "createDate" }, direction = Direction.DESC) Pageable pageable,
			@RequestParam(value = "projectId", required = false) Long projectId,
			@RequestParam(value = "my", required = false) String my,
			@RequestParam(value = "new", required = false) String _new,
			@RequestParam(value = "languageId", required = false) Long languageId,
			@RequestParam(value = "id", required = false) Long id,
			@RequestParam(value = "search", required = false) String search) {

		List<Project> projects = projectRepository.findAll();

		if (projects.size() == 0) {
			throw new RuntimeException("系统中不存在项目,请先创建项目后再尝试访问本页面!");
		}

		Set<Language> languages = null;
		Project project = null;
		org.springframework.data.domain.Page<Translate> page = null;
		if (projectId != null) {
			project = projectRepository.findOne(projectId);
			if (project == null) {
				return "redirect:translate";
			}
		} else {
			if (projects.size() > 0) {
				project = projects.get(0);
				projectId = projects.get(0).getId();
			}
		}

		if (project != null) {

			languages = project.getLanguages();
			projectId = project.getId();

			if (StringUtil.isNotEmpty(id)) {
				page = translateRepository.findById(id, pageable);
			} else if (StringUtil.isNotEmpty(my)) {
				page = translateRepository.findByProjectAndCreator(project,
						WebUtil.getUsername(), pageable);
			} else if (StringUtil.isNotEmpty(_new)) {
				page = translateRepository.findByProjectAndStatus(project,
						Status.New, pageable);
			} else if (StringUtil.isNotEmpty(languageId)) {
				long total = translateRepository.countUnTranslatedByProject(
						languageId, projectId);
				List<Translate> unTranslates = translateRepository
						.queryUnTranslatedByProject(languageId, projectId,
								pageable.getOffset(), pageable.getPageSize());
				page = new PageImpl<Translate>(unTranslates, pageable, total);
			} else if (StringUtil.isNotEmpty(search)) {
				String like = "%" + search + "%";
				page = translateRepository.findByProjectAndSearchLike(project,
						like, pageable);
			} else {
				page = translateRepository.findByProject(project, pageable);
			}
		}

		List<Language> languages2 = new ArrayList<Language>();

		if (languages != null && project != null
				&& project.getLanguage() != null) {
			for (Language language : languages) {
				if (language.getId().equals(project.getLanguage().getId())) {
					languages2.add(0, language);
				} else {
					languages2.add(language);
				}
			}
		} else {
			languages2.addAll(languages);
		}

		// login user labels
		List<Label> labels = labelRepository.findByCreator(WebUtil
				.getUsername());
		Set<String> lbls = null;
		if (labels != null) {
			lbls = labels.stream().map((label) -> {
				return label.getName();
			}).collect(Collectors.toSet());
		} else {
			lbls = new HashSet<String>();
		}

		model.addAttribute("projects", projects);
		model.addAttribute("project", project);
		model.addAttribute("labels", lbls);
		model.addAttribute("page", page);
		model.addAttribute("languages", languages2);
		model.addAttribute("projectId", projectId);
		model.addAttribute("user", getLoginUser());

		return "admin/translate";
	}

	@RequestMapping("import")
	public String _import(Model model,
			@RequestParam(value = "projectId", required = false) Long projectId) {

		List<Project> projects = projectRepository.findAll();
		Set<Translate> translates = null;
		Set<Language> languages = null;
		Project project = null;
		if (projectId != null) {
			project = projectRepository.findOne(projectId);
			if (project != null) {
				translates = project.getTranslates();
				languages = project.getLanguages();
			}
		} else {
			if (projects.size() > 0) {
				project = projects.get(0);
				projectId = projects.get(0).getId();
				translates = projects.get(0).getTranslates();
				languages = projects.get(0).getLanguages();
			}
		}

		List<Language> languages2 = new ArrayList<Language>();

		if (languages != null && project != null
				&& project.getLanguage() != null) {
			for (Language language : languages) {
				if (language.getId().equals(project.getLanguage().getId())) {
					languages2.add(0, language);
				} else {
					languages2.add(language);
				}
			}
		} else {
			languages2.addAll(languages);
		}

		// login user labels
		List<Label> labels = labelRepository
				.findByCreator(WebUtil.getUsername());
		Set<String> lbls = null;
		if (labels != null) {
			lbls = labels.stream().map((label) -> {
				return label.getName();
			}).collect(Collectors.toSet());
		} else {
			lbls = new HashSet<String>();
		}

		model.addAttribute("projects", projects);
		model.addAttribute("translates", translates);
		model.addAttribute("languages", languages2);
		model.addAttribute("projectId", projectId);
		model.addAttribute("labels", lbls);

		return "admin/import";
	}

}
