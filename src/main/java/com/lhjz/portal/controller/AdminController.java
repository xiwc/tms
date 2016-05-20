/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lhjz.portal.base.BaseController;
import com.lhjz.portal.entity.Language;
import com.lhjz.portal.entity.Project;
import com.lhjz.portal.entity.Translate;
import com.lhjz.portal.entity.security.Authority;
import com.lhjz.portal.entity.security.User;
import com.lhjz.portal.model.UserInfo;
import com.lhjz.portal.pojo.Enum.Status;
import com.lhjz.portal.repository.FileRepository;
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
	TranslateRepository translateRepository;

	@Autowired
	UserRepository userRepository;

	@RequestMapping("login")
	public String login(Model model) {

		logger.debug("Enter method: {}", "login");

		return "admin/login";
	}

	@RequestMapping()
	public String home(Model model) {

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

	@RequestMapping("feedback")
	public String feedback(Model model) {
		return "admin/feedback";
	}

	@RequestMapping("translate")
	public String translate(Model model, @PageableDefault Pageable pageable,
			@RequestParam(value = "projectId", required = false) Long projectId,
			@RequestParam(value = "my", required = false) String my,
			@RequestParam(value = "new", required = false) String _new,
			@RequestParam(value = "languageId", required = false) Long languageId,
			@RequestParam(value = "search", required = false) String search) {

		List<Project> projects = projectRepository.findAll();
		Set<Language> languages = null;
		Project project = null;
		org.springframework.data.domain.Page<Translate> page = null;
		if (projectId != null) {
			project = projectRepository.findOne(projectId);
		} else {
			if (projects.size() > 0) {
				project = projects.get(0);
				projectId = projects.get(0).getId();
			}
		}

		if (project != null) {

			languages = project.getLanguages();
			projectId = project.getId();

			if (StringUtil.isNotEmpty(my)) {
				page = translateRepository.findByProjectAndCreator(project, WebUtil.getUsername(), pageable);
			} else if (StringUtil.isNotEmpty(_new)) {
				page = translateRepository.findByProjectAndStatus(project, Status.New, pageable);
			} else if (StringUtil.isNotEmpty(languageId)) {
				long total = translateRepository.countUnTranslatedByProject(languageId, projectId);
				List<Translate> unTranslates = translateRepository.queryUnTranslatedByProject(languageId, projectId,
						pageable.getOffset(), pageable.getPageSize());
				page = new PageImpl<Translate>(unTranslates, pageable, total);
			} else if (StringUtil.isNotEmpty(search)) {
				String like = "%" + search + "%";
				page = translateRepository.findByProjectAndKeyLikeOrProjectAndDescriptionLike(project, like, project,
						like, pageable);
			} else {
				page = translateRepository.findByProject(project, pageable);
			}
		}
		model.addAttribute("projects", projects);
		model.addAttribute("page", page);
		model.addAttribute("languages", languages);
		model.addAttribute("projectId", projectId);

		return "admin/translate";
	}

	@RequestMapping("import")
	public String _import(Model model, @RequestParam(value = "projectId", required = false) Long projectId) {

		List<Project> projects = projectRepository.findAll();
		Set<Translate> translates = null;
		Set<Language> languages = null;
		if (projectId != null) {
			Project project = projectRepository.findOne(projectId);
			if (project != null) {
				translates = project.getTranslates();
				languages = project.getLanguages();
			}
		} else {
			if (projects.size() > 0) {
				projectId = projects.get(0).getId();
				translates = projects.get(0).getTranslates();
				languages = projects.get(0).getLanguages();
			}
		}

		model.addAttribute("projects", projects);
		model.addAttribute("translates", translates);
		model.addAttribute("languages", languages);
		model.addAttribute("projectId", projectId);

		return "admin/import";
	}

}
