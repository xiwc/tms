/**
 * 立衡脊柱版权所有 (lhjz)
 */
package com.lhjz.portal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lhjz.portal.base.BaseController;
import com.lhjz.portal.entity.Config;
import com.lhjz.portal.entity.Language;
import com.lhjz.portal.entity.Project;
import com.lhjz.portal.entity.Settings;
import com.lhjz.portal.entity.Translate;
import com.lhjz.portal.entity.security.Authority;
import com.lhjz.portal.entity.security.User;
import com.lhjz.portal.model.RespBody;
import com.lhjz.portal.model.UserInfo;
import com.lhjz.portal.pojo.ContactForm;
import com.lhjz.portal.pojo.Enum.Key;
import com.lhjz.portal.pojo.Enum.Module;
import com.lhjz.portal.pojo.Enum.Page;
import com.lhjz.portal.repository.ConfigRepository;
import com.lhjz.portal.repository.FileRepository;
import com.lhjz.portal.repository.ProjectRepository;
import com.lhjz.portal.repository.SettingsRepository;
import com.lhjz.portal.repository.UserRepository;
import com.lhjz.portal.util.EnumUtil;
import com.lhjz.portal.util.FileUtil;
import com.lhjz.portal.util.JsonUtil;
import com.lhjz.portal.util.MapUtil;
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
	SettingsRepository settingsRepository;

	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ConfigRepository configRepository;

	@RequestMapping("login")
	public String login(Model model) {

		logger.debug("Enter method: {}", "login");

		return "admin/login";
	}

	@RequestMapping()
	public String home(Model model) {

		List<Settings> settings = settingsRepository.findByPage(Page.Index);

		List<Settings> bigImgs = new ArrayList<Settings>();
		List<Settings> hotNews = new ArrayList<Settings>();
		List<Settings> moreNews = new ArrayList<Settings>();

		for (Settings settings2 : settings) {
			if (settings2.getModule() == Module.BigImg) {
				bigImgs.add(settings2);
			} else if (settings2.getModule() == Module.HotNews) {
				hotNews.add(settings2);
			} else if (settings2.getModule() == Module.MoreNews) {
				moreNews.add(settings2);
			}
		}

		model.addAttribute("bigImgs", bigImgs);
		model.addAttribute("hotNews", hotNews);
		model.addAttribute("moreNews", moreNews);

		return "admin/index";
	}

	@RequestMapping("about")
	public String about(Model model) {

		List<Settings> settings = settingsRepository.findByPage(Page.About);

		List<Settings> bigImgs = new ArrayList<Settings>();
		List<Settings> branches = new ArrayList<Settings>();
		List<Settings> experts = new ArrayList<Settings>();

		for (Settings settings2 : settings) {
			if (settings2.getModule() == Module.BigImg) {
				bigImgs.add(settings2);
			} else if (settings2.getModule() == Module.Branch) {
				branches.add(settings2);
			} else if (settings2.getModule() == Module.Expert) {
				experts.add(settings2);
			}
		}

		model.addAttribute("bigImgs", bigImgs);
		model.addAttribute("branches", branches);
		model.addAttribute("experts", experts);

		return "admin/about";
	}

	@RequestMapping("case")
	public String case_(Model model) {

		List<Settings> settings = settingsRepository.findByPage(Page.Case);

		List<Settings> introductions = new ArrayList<Settings>();
		List<Settings> mores = new ArrayList<Settings>();

		for (Settings settings2 : settings) {
			if (settings2.getModule() == Module.Introduction) {
				introductions.add(settings2);
			} else if (settings2.getModule() == Module.More) {
				mores.add(settings2);
			}
		}

		model.addAttribute("introductions", introductions);
		model.addAttribute("mores", mores);

		return "admin/case";
	}

	@RequestMapping("contact")
	public String contact(Model model) {

		Config config = configRepository.findFirstByKey(Key.Contact);

		if (config != null) {
			model.addAttribute("contact",
					JsonUtil.json2Object(config.getValue(), ContactForm.class));
		} else {
			model.addAttribute("contact", new ContactForm());
		}

		return "admin/contact";
	}

	@RequestMapping("env")
	public String env(Model model) {

		List<Settings> settings = settingsRepository.findByPage(Page.Env);

		List<Settings> introductions = new ArrayList<Settings>();
		List<Settings> mores = new ArrayList<Settings>();

		for (Settings settings2 : settings) {
			if (settings2.getModule() == Module.Introduction) {
				introductions.add(settings2);
			} else if (settings2.getModule() == Module.More) {
				mores.add(settings2);
			}
		}

		model.addAttribute("introductions", introductions);
		model.addAttribute("mores", mores);

		return "admin/env";
	}

	@RequestMapping("feature")
	public String feature(Model model) {

		List<Settings> settings = settingsRepository.findByPage(Page.Feature);

		List<Settings> introductions = new ArrayList<Settings>();
		List<Settings> mores = new ArrayList<Settings>();

		for (Settings settings2 : settings) {
			if (settings2.getModule() == Module.Introduction) {
				introductions.add(settings2);
			} else if (settings2.getModule() == Module.More) {
				mores.add(settings2);
			}
		}

		model.addAttribute("introductions", introductions);
		model.addAttribute("mores", mores);

		return "admin/feature";
	}

	@RequestMapping("health")
	public String health(Model model) {

		List<Settings> settings = settingsRepository.findByPage(Page.Health);

		List<Settings> introductions = new ArrayList<Settings>();
		List<Settings> mores = new ArrayList<Settings>();

		for (Settings settings2 : settings) {
			if (settings2.getModule() == Module.Introduction) {
				introductions.add(settings2);
			} else if (settings2.getModule() == Module.More) {
				mores.add(settings2);
			}
		}

		model.addAttribute("introductions", introductions);
		model.addAttribute("mores", mores);

		return "admin/health";
	}

	@RequestMapping("product")
	public String product(Model model) {

		List<Settings> settings = settingsRepository.findByPage(Page.Product);

		List<Settings> introductions = new ArrayList<Settings>();
		List<Settings> mores = new ArrayList<Settings>();

		for (Settings settings2 : settings) {
			if (settings2.getModule() == Module.Introduction) {
				introductions.add(settings2);
			} else if (settings2.getModule() == Module.More) {
				mores.add(settings2);
			}
		}

		model.addAttribute("introductions", introductions);
		model.addAttribute("mores", mores);

		return "admin/product";
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

	@RequestMapping("resource")
	public String resource(Model model) {

		String storePath = env.getProperty("lhjz.upload.img.store.path");
		int sizeLarge = env.getProperty("lhjz.upload.img.scale.size.large",
				Integer.class);
		int sizeHuge = env.getProperty("lhjz.upload.img.scale.size.huge",
				Integer.class);
		int sizeOriginal = env.getProperty(
				"lhjz.upload.img.scale.size.original", Integer.class);

		// img relative path (eg:'upload/img/' & 640 & '/' )
		model.addAttribute("path",
				FileUtil.joinPaths(storePath, sizeOriginal + "/"));
		model.addAttribute("pathLarge",
				FileUtil.joinPaths(storePath, sizeLarge + "/"));
		model.addAttribute("pathHuge",
				FileUtil.joinPaths(storePath, sizeHuge + "/"));
		// list all files
		model.addAttribute("imgs", fileRepository.findAll());

		return "admin/resource";
	}

	@RequestMapping("article")
	public String article(Model model) {
		return "admin/article";
	}

	@RequestMapping("feedback")
	public String feedback(Model model) {
		return "admin/feedback";
	}

	@RequestMapping("translate")
	public String translate(Model model,
			@RequestParam(value = "projectId", required = false) Long projectId) {

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

		return "admin/translate";
	}

	@RequestMapping(value = "pageEnable", method = RequestMethod.POST)
	@ResponseBody
	public RespBody pageEnable(@RequestParam("page") String page,
			@RequestParam("enable") boolean enable) {

		Page page2 = EnumUtil.page(page);

		if (page2 == Page.Unknow) {
			logger.error("设置页面不存在, Page: {}", page);
			return RespBody.failed("设置页面不存在!");
		}

		Config config = configRepository.findFirstByKey(Key.PageEnable);

		// page enable config exists.
		if (config != null) {
			String json = config.getValue();

			@SuppressWarnings("unchecked")
			Map<String, Object> map = JsonUtil.json2Object(json, Map.class);

			if (map == null) {
				map = new HashMap<>();
			}

			map.put(page, enable);

			config.setValue(JsonUtil.toJson(map));

			configRepository.saveAndFlush(config);
		} else {// first set page enable config
			Config config2 = new Config();

			config2.setKey(Key.PageEnable);
			config2.setValue(JsonUtil.toJson(MapUtil.objArr2Map(page, enable)));

			config2.setUsername(WebUtil.getUsername());

			configRepository.saveAndFlush(config2);
		}

		return RespBody.succeed();
	}

	@SuppressWarnings("unchecked")
	@ModelAttribute("pageEnable")
	public Map<String, Object> pageEnable() {

		Config config = configRepository.findFirstByKey(Key.PageEnable);

		if (config != null) {
			return JsonUtil.json2Object(config.getValue(), Map.class);
		}

		return null;
	}
}
