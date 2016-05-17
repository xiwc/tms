/**
 * 立衡脊柱版权所有 (lhjz)
 */
package com.lhjz.portal.controller;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.lhjz.portal.repository.AuthorityRepository;
import com.lhjz.portal.repository.ProjectRepository;
import com.lhjz.portal.repository.TranslateRepository;
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
@RequestMapping("admin/translate")
public class TranslateController extends BaseController {

	static Logger logger = LoggerFactory.getLogger(TranslateController.class);

	@Autowired
	TranslateRepository translateRepository;

	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	AuthorityRepository authorityRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@RequestMapping(value = "save", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
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
			String name = language.getName();
			if (jsonO.has(name)) {
				String content = jsonO.get(name).getAsString();
				TranslateItem item = new TranslateItem();
				item.setContent(content);
				item.setCreateDate(new Date());
				item.setCreator(WebUtil.getUsername());
				item.setLanguage(language);
				item.setStatus(Status.New);
				item.setTranslate(translate);

				translate.getTranslateItems().add(item);
			}
		}

		translateRepository.saveAndFlush(translate);

		log(Action.Create, Target.Translate, translate);

		return RespBody.succeed(translate);
	}

	//
	// @RequestMapping(value = "update", method = RequestMethod.POST)
	// @ResponseBody
	// @Secured({ "ROLE_ADMIN" })
	// public RespBody update(UserForm userForm) {
	//
	// User user = userRepository.findOne(userForm.getUsername());
	//
	// if (user == null) {
	// logger.error("更新用户不存在! ID: {}", userForm.getUsername());
	// return RespBody.failed("更新用户不存在!");
	// }
	//
	// if (StringUtil.isNotEmpty(userForm.getPassword())) {
	//
	// if (userForm.getPassword().length() < 6) {
	// logger.error("修改密码长度小于六位, ID: {}", userForm.getUsername());
	// return RespBody.failed("修改密码长度不能小于六位!");
	// }
	//
	// user.setPassword(passwordEncoder.encode(userForm.getPassword()));
	// }
	//
	// if (userForm.getEnabled() != null && user.getStatus() != Status.Bultin) {
	// user.setEnabled(userForm.getEnabled());
	// }
	//
	// userRepository.saveAndFlush(user);
	//
	// return RespBody.succeed(user.getUsername());
	// }
	//
	// @RequestMapping(value = "update2", method = RequestMethod.POST)
	// @ResponseBody
	// @Secured({ "ROLE_USER" })
	// public RespBody update2(UserForm userForm) {
	//
	// if (!WebUtil.getUsername().equals(userForm.getUsername())) {
	// logger.error("普通用户无权限修改其他用户信息!");
	// return RespBody.failed("普通用户无权限修改其他用户信息!");
	// }
	//
	// User user = userRepository.findOne(userForm.getUsername());
	//
	// if (user == null) {
	// logger.error("更新用户不存在! ID: {}", userForm.getUsername());
	// return RespBody.failed("更新用户不存在!");
	// }
	//
	// if (StringUtil.isNotEmpty(userForm.getPassword())) {
	//
	// if (userForm.getPassword().length() < 6) {
	// logger.error("修改密码长度小于六位, ID: {}", userForm.getUsername());
	// return RespBody.failed("修改密码长度不能小于六位!");
	// }
	//
	// user.setPassword(passwordEncoder.encode(userForm.getPassword()));
	// }
	//
	// if (userForm.getEnabled() != null && user.getStatus() != Status.Bultin) {
	// user.setEnabled(userForm.getEnabled());
	// }
	//
	// userRepository.saveAndFlush(user);
	//
	// return RespBody.succeed(user.getUsername());
	// }
	//
	// @RequestMapping(value = "delete", method = RequestMethod.POST)
	// @ResponseBody
	// @Secured({ "ROLE_ADMIN" })
	// public RespBody delete(@RequestParam("username") String username) {
	//
	// User user = userRepository.findOne(username);
	//
	// if (user == null) {
	// logger.error("删除用户不存在! ID: {}", username);
	// return RespBody.failed("删除用户不存在!");
	// }
	//
	// if (user.getStatus() == Status.Bultin) {
	// logger.error("内置用户,不能删除! ID: {}", username);
	// return RespBody.failed("内置用户,不能删除!");
	// }
	//
	// userRepository.delete(user);
	//
	// return RespBody.succeed(username);
	// }
	//
	@RequestMapping(value = "get", method = RequestMethod.GET)
	@ResponseBody
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	public RespBody get(@RequestParam("projectId") Long projectId) {

		Project project = projectRepository.findOne(projectId);

		if (project == null) {
			logger.error("项目不存在! ID: {}", projectId);
			return RespBody.failed("项目不存在!");
		}

		Set<Translate> translates = project.getTranslates();

		return RespBody.succeed(translates);
	}
}
