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
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lhjz.portal.base.BaseController;
import com.lhjz.portal.entity.security.Authority;
import com.lhjz.portal.entity.security.AuthorityId;
import com.lhjz.portal.entity.security.User;
import com.lhjz.portal.model.RespBody;
import com.lhjz.portal.pojo.Enum.Action;
import com.lhjz.portal.pojo.Enum.Role;
import com.lhjz.portal.pojo.Enum.Status;
import com.lhjz.portal.pojo.Enum.Target;
import com.lhjz.portal.pojo.UserForm;
import com.lhjz.portal.repository.AuthorityRepository;
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
@RequestMapping("admin/user")
public class UserController extends BaseController {

	static Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserRepository userRepository;

	@Autowired
	AuthorityRepository authorityRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@RequestMapping(value = "save", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_ADMIN" })
	public RespBody save(@RequestParam("role") String role,
			@Valid UserForm userForm, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return RespBody.failed(bindingResult.getAllErrors().stream()
					.map(err -> err.getDefaultMessage())
					.collect(Collectors.joining("<br/>")));
		}

		if (userRepository.exists(userForm.getUsername())) {
			logger.error("添加用户已经存在, ID: {}", userForm.getUsername());
			return RespBody.failed("添加用户已经存在!");
		}

		// save username and password
		User user = new User();
		user.setUsername(userForm.getUsername());
		user.setPassword(passwordEncoder.encode(userForm.getPassword()));
		user.setEnabled(userForm.getEnabled());
		user.setCreateDate(new Date());

		userRepository.saveAndFlush(user);

		log(Action.Create, Target.User, user);

		// save default authority `ROLE_USER`
		Authority authority = new Authority();
		authority.setId(new AuthorityId(userForm.getUsername(), Role.ROLE_USER
				.name()));

		authorityRepository.saveAndFlush(authority);

		log(Action.Create, Target.Authority, authority);

		if (role.equalsIgnoreCase("admin")) {
			Authority authority2 = new Authority();
			authority2.setId(new AuthorityId(userForm.getUsername(),
					Role.ROLE_ADMIN.name()));

			authorityRepository.saveAndFlush(authority2);

			log(Action.Create, Target.Authority, authority2);
		}

		return RespBody.succeed(user.getUsername());
	}

	@RequestMapping(value = "update", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_ADMIN" })
	public RespBody update(UserForm userForm) {

		User user = userRepository.findOne(userForm.getUsername());

		if (user == null) {
			logger.error("更新用户不存在! ID: {}", userForm.getUsername());
			return RespBody.failed("更新用户不存在!");
		}

		if (StringUtil.isNotEmpty(userForm.getPassword())) {

			if (userForm.getPassword().length() < 6) {
				logger.error("修改密码长度小于六位, ID: {}", userForm.getUsername());
				return RespBody.failed("修改密码长度不能小于六位!");
			}

			user.setPassword(passwordEncoder.encode(userForm.getPassword()));
		}

		if (userForm.getEnabled() != null && user.getStatus() != Status.Bultin) {
			user.setEnabled(userForm.getEnabled());
		}

		userRepository.saveAndFlush(user);

		return RespBody.succeed(user.getUsername());
	}

	@RequestMapping(value = "update2", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_USER" })
	public RespBody update2(UserForm userForm) {

		if (!WebUtil.getUsername().equals(userForm.getUsername())) {
			logger.error("普通用户无权限修改其他用户信息!");
			return RespBody.failed("普通用户无权限修改其他用户信息!");
		}

		User user = userRepository.findOne(userForm.getUsername());

		if (user == null) {
			logger.error("更新用户不存在! ID: {}", userForm.getUsername());
			return RespBody.failed("更新用户不存在!");
		}

		if (StringUtil.isNotEmpty(userForm.getPassword())) {

			if (userForm.getPassword().length() < 6) {
				logger.error("修改密码长度小于六位, ID: {}", userForm.getUsername());
				return RespBody.failed("修改密码长度不能小于六位!");
			}

			user.setPassword(passwordEncoder.encode(userForm.getPassword()));
		}

		if (userForm.getEnabled() != null && user.getStatus() != Status.Bultin) {
			user.setEnabled(userForm.getEnabled());
		}

		userRepository.saveAndFlush(user);

		return RespBody.succeed(user.getUsername());
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_ADMIN" })
	public RespBody delete(@RequestParam("username") String username) {

		User user = userRepository.findOne(username);

		if (user == null) {
			logger.error("删除用户不存在! ID: {}", username);
			return RespBody.failed("删除用户不存在!");
		}

		if (user.getStatus() == Status.Bultin) {
			logger.error("内置用户,不能删除! ID: {}", username);
			return RespBody.failed("内置用户,不能删除!");
		}

		userRepository.delete(user);

		return RespBody.succeed(username);
	}

	@RequestMapping(value = "get", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_ADMIN" })
	public RespBody get(@RequestParam("username") String username) {

		User user = userRepository.findOne(username);

		if (user == null) {
			logger.error("查询用户不存在! ID: {}", username);
			return RespBody.failed("查询用户不存在!");
		}

		// prevent ref to each other
		user.setAuthorities(null);

		return RespBody.succeed(user);
	}
}
