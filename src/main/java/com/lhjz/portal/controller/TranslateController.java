/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
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
import com.lhjz.portal.component.MailSender2;
import com.lhjz.portal.constant.SysConstant;
import com.lhjz.portal.entity.Label;
import com.lhjz.portal.entity.Language;
import com.lhjz.portal.entity.Project;
import com.lhjz.portal.entity.Translate;
import com.lhjz.portal.entity.TranslateItem;
import com.lhjz.portal.entity.TranslateItemHistory;
import com.lhjz.portal.entity.security.User;
import com.lhjz.portal.model.Mail;
import com.lhjz.portal.model.RespBody;
import com.lhjz.portal.pojo.Enum.Action;
import com.lhjz.portal.pojo.Enum.Status;
import com.lhjz.portal.pojo.Enum.Target;
import com.lhjz.portal.pojo.TranslateForm;
import com.lhjz.portal.pojo.TranslateItemForm;
import com.lhjz.portal.repository.AuthorityRepository;
import com.lhjz.portal.repository.LabelRepository;
import com.lhjz.portal.repository.ProjectRepository;
import com.lhjz.portal.repository.TranslateItemHistoryRepository;
import com.lhjz.portal.repository.TranslateItemRepository;
import com.lhjz.portal.repository.TranslateRepository;
import com.lhjz.portal.repository.UserRepository;
import com.lhjz.portal.util.CommonUtil;
import com.lhjz.portal.util.DateUtil;
import com.lhjz.portal.util.JsonUtil;
import com.lhjz.portal.util.MapUtil;
import com.lhjz.portal.util.StringUtil;
import com.lhjz.portal.util.TemplateUtil;
import com.lhjz.portal.util.ThreadUtil;
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
	TranslateItemHistoryRepository translateItemHistoryRepository;

	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	AuthorityRepository authorityRepository;

	@Autowired
	LabelRepository labelRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	MailSender2 mailSender;

	String translateAction = "admin/translate";

	@RequestMapping(value = "save", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_SUPER", "ROLE_ADMIN", "ROLE_USER" })
	public RespBody save(@RequestParam("projectId") Long projectId,
			@RequestParam("baseURL") String baseURL,
			@Valid TranslateForm translateForm, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return RespBody.failed(bindingResult.getAllErrors().stream()
					.map(err -> err.getDefaultMessage())
					.collect(Collectors.joining("<br/>")));
		}

		final Project project = projectRepository.findOne(projectId);

		if (translateRepository
				.findByKeyAndProject(translateForm.getKey(), project)
				.size() > 0) {
			logger.error("添加名称已经存在, ID: {}", translateForm.getKey());
			return RespBody.failed("添加名称已经存在!");
		}

		final Translate translate = new Translate();

		Set<Label> labels = null;

		String tags = translateForm.getTags();
		if (StringUtil.isNotEmpty(tags)) {
			String[] tagsArr = tags.split(",");
			labels = new HashSet<Label>();
			for (String tag : tagsArr) {
				Label label = new Label();
				label.setCreateDate(new Date());
				label.setCreator(WebUtil.getUsername());
				label.setName(tag);
				label.setStatus(Status.New);
				label.setTranslate(translate);

				labels.add(label);
			}

		}

		translate.setKey(translateForm.getKey());
		translate.setProject(project);
		translate.setCreateDate(new Date());
		translate.setCreator(WebUtil.getUsername());
		translate.setDescription(translateForm.getDesc());
		translate.setStatus(Status.New);
		if (labels != null) {
			translate.getLabels().addAll(labels);
		}
		final User loginUser = getLoginUser();
		translate.getWatchers().add(loginUser);

		JsonObject jsonO = (JsonObject) JsonUtil
				.toJsonElement(translateForm.getContent());
		Set<Language> lngs = project.getLanguages();

		final Mail mail2 = Mail.instance().parseTranslateForm(translateForm);

		for (Language language : lngs) {
			TranslateItem item = new TranslateItem();
			String name = language.getName();
			if (jsonO.has(name)) {
				item.setContent(jsonO.get(name).getAsString());
				// 如果描述为空, 取项目主语言翻译值.
				if (language.getId().equals(project.getLanguage().getId())
						&& StringUtil.isEmpty(translate.getDescription())) {
					translate.setDescription(item.getContent());
				}
			} else {
				item.setContent(StringUtil.EMPTY);
			}
			item.setCreateDate(new Date());
			item.setCreator(WebUtil.getUsername());
			item.setLanguage(language);
			item.setStatus(Status.New);
			item.setTranslate(translate);

			mail2.put(
					language.getDescription() + "[" + language.getName() + "]",
					item.getContent());

			translate.getTranslateItems().add(item);
		}

		translate.setSearch(translate.toString());

		translateRepository.saveAndFlush(translate);

		loginUser.getWatcherTranslates().add(translate);
		userRepository.saveAndFlush(loginUser);

		log(Action.Create, Target.Translate, translate);

		final Mail mail = Mail.instance().addWatchers(translate)
				.addUsers(getUser(translate.getCreator()));
		// .removeUser(getLoginUser());

		final String href = baseURL + translateAction + "?projectId="
				+ projectId + "&id=" + translate.getId();

		ThreadUtil.exec(() -> {

			try {
				mailSender
						.sendHtml(
								String.format("TMS-翻译新建_%s",
										DateUtil.format(new Date(),
												DateUtil.FORMAT7)),
								TemplateUtil.process(
										"templates/mail/translate-create",
										MapUtil.objArr2Map("translate",
												translate, "user", loginUser,
												"href", href, "project",
												project, "body", mail2.body())),
								mail.get());
				logger.info("翻译新建邮件发送成功！ID:{}", translate.getId());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("翻译新建邮件发送失败！ID:{}", translate.getId());
			}

		});

		return RespBody.succeed(translate);
	}

	@RequestMapping(value = "update", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_SUPER", "ROLE_ADMIN", "ROLE_USER" })
	public RespBody update(@RequestParam("id") Long id,
			TranslateItemForm translateItemForm,
			@RequestParam("baseURL") String baseURL) {

		TranslateItem translateItem = translateItemRepository.findOne(id);

		String oldContent = StringUtil.EMPTY;

		if (translateItem != null) {

			TranslateItemHistory translateItemHistory = new TranslateItemHistory();
			translateItemHistory.setCreateDate(new Date());
			translateItemHistory.setCreator(WebUtil.getUsername());
			translateItemHistory.setItemContent(translateItem.getContent());
			translateItemHistory
					.setItemCreateDate(translateItem.getUpdateDate() != null
							? translateItem.getUpdateDate()
							: translateItem.getCreateDate());
			translateItemHistory
					.setItemCreator(translateItem.getUpdater() != null
							? translateItem.getUpdater()
							: translateItem.getCreator());
			translateItemHistory.setTranslateItem(translateItem);

			translateItemHistoryRepository.saveAndFlush(translateItemHistory);

			oldContent = translateItem.getContent();

			translateItem.setContent(translateItemForm.getContent());
			translateItem.setUpdater(WebUtil.getUsername());
			translateItem.setUpdateDate(new Date());

			TranslateItem translateItem2 = translateItemRepository
					.saveAndFlush(translateItem);

			final Translate translate = translateItem2.getTranslate();
			translate.setUpdateDate(new Date());
			translate.setUpdater(WebUtil.getUsername());
			translate.setStatus(Status.Updated);

			final User loginUser = getLoginUser();
			translate.getWatchers().add(loginUser);

			if (translate.getProject().getLanguage().getId()
					.equals(translateItem.getLanguage().getId())) {
				// 修改翻译描述为主语言内容
				if (StringUtil.isNotEmpty(translateItemForm.getContent())) {
					translate.setDescription(translateItemForm.getContent());
				}
			}

			translate.setSearch(translate.toString());

			translateRepository.saveAndFlush(translate);

			loginUser.getWatcherTranslates().add(translate);
			userRepository.saveAndFlush(loginUser);

			final Mail mail = Mail.instance().addWatchers(translate)
					.addUsers(getUser(translate.getCreator()));
			// .removeUser(getLoginUser());

			final String href = baseURL + translateAction + "?projectId="
					+ translate.getProject().getId() + "&id="
					+ translate.getId();

			final Mail mail2 = Mail.instance()
					.put(translateItem.getLanguage().getDescription() + "["
							+ translateItem.getLanguage().getName() + "]",
							oldContent + SysConstant.CHANGE_TO
									+ translateItemForm.getContent());

			final Project project = translate.getProject();

			ThreadUtil.exec(() -> {

				try {
					mailSender.sendHtml(String.format("TMS-翻译更新_%s",
							DateUtil.format(new Date(), DateUtil.FORMAT7)),
							TemplateUtil.process(
									"templates/mail/translate-update",
									MapUtil.objArr2Map("translate", translate,
											"user", loginUser, "href", href,
											"project", project, "body",
											mail2.body())),
							mail.get());
					logger.info("翻译更新邮件发送成功！ID:{}", translate.getId());
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("翻译更新邮件发送失败！ID:{}", translate.getId());
				}

			});

		} else {
			logger.error("更新翻译条目不存在! ID: {}", id);
			return RespBody.failed("更新翻译条目不存在!");
		}

		logWithProperties(Action.Update, Target.TranslateItem, "content",
				translateItemForm.getContent(), oldContent);

		return RespBody.succeed(id);
	}

	private TranslateItem getExitTranslateItem(String lngName,
			Translate translate) {

		Set<TranslateItem> translateItems = translate.getTranslateItems();
		for (TranslateItem translateItem : translateItems) {
			if (translateItem.getLanguage().getName().equals(lngName)) {
				return translateItem;
			}
		}
		return null;
	}

	private boolean isLabelExits(Set<Label> labels, String tag) {
		for (Label label : labels) {
			if (label.getName().equalsIgnoreCase(tag)) {
				return true;
			}
		}
		return false;
	}

	@RequestMapping(value = "update2", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_SUPER", "ROLE_ADMIN", "ROLE_USER" })
	public RespBody update2(@RequestParam("id") Long id,
			@RequestParam("baseURL") String baseURL,
			@Valid TranslateForm translateForm, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return RespBody.failed(bindingResult.getAllErrors().stream()
					.map(err -> err.getDefaultMessage())
					.collect(Collectors.joining("<br/>")));
		}

		final Translate translate = translateRepository.findOne(id);

		if (translate != null) {

			final Mail mail2 = Mail.instance()
					.parseTranslateUpdated(translateForm, translate);

			translate.setKey(translateForm.getKey());
			translate.setDescription(translateForm.getDesc());
			translate.setStatus(Status.Updated);
			translate.setUpdater(WebUtil.getUsername());
			translate.setUpdateDate(new Date());

			final User loginUser = getLoginUser();
			translate.getWatchers().add(loginUser);

			Set<Label> labels = translate.getLabels();
			Set<Label> delLabels = new HashSet<Label>();
			Set<Label> addLabels = new HashSet<Label>();
			String tags = translateForm.getTags();
			if (StringUtil.isNotEmpty(tags)) {
				Set<String> tagSet = CommonUtil.arr2Set(tags.split(","));
				for (Label label : labels) {
					if (!tagSet.contains(label.getName())) { // delete tag
						delLabels.add(label);
					}
				}

				for (String tag : tagSet) {
					if (!isLabelExits(labels, tag)) { // add tag
						Label label = new Label();
						label.setCreateDate(new Date());
						label.setCreator(WebUtil.getUsername());
						label.setName(tag);
						label.setStatus(Status.New);
						label.setTranslate(translate);

						addLabels.add(label);
					}
				}
			} else {
				delLabels = new HashSet<Label>(labels); // delete all tags
			}

			if (delLabels.size() > 0) {
				labels.removeAll(delLabels);
				labelRepository.deleteInBatch(delLabels);
				labelRepository.flush();
			}

			if (addLabels.size() > 0) {
				List<Label> save = labelRepository.save(addLabels);
				labels.addAll(save);
			}

			JsonObject jsonO = (JsonObject) JsonUtil
					.toJsonElement(translateForm.getContent());
			Set<Language> lngs = translate.getProject().getLanguages();

			for (Language language : lngs) {

				String name = language.getName();
				String content = StringUtil.EMPTY;
				if (jsonO.has(name)) {
					content = jsonO.get(name).getAsString();
				}
				if (language.getId()
						.equals(translate.getProject().getLanguage().getId())
						&& StringUtil.isNotEmpty(content)) {
					translate.setDescription(content);
				}

				TranslateItem exitTranslateItem = getExitTranslateItem(name,
						translate);
				if (exitTranslateItem != null) {

					TranslateItemHistory translateItemHistory = new TranslateItemHistory();
					translateItemHistory.setCreateDate(new Date());
					translateItemHistory.setCreator(WebUtil.getUsername());
					translateItemHistory
							.setItemContent(exitTranslateItem.getContent());
					translateItemHistory.setItemCreateDate(
							exitTranslateItem.getUpdateDate() != null
									? exitTranslateItem.getUpdateDate()
									: exitTranslateItem.getCreateDate());
					translateItemHistory.setItemCreator(
							exitTranslateItem.getUpdater() != null
									? exitTranslateItem.getUpdater()
									: exitTranslateItem.getCreator());
					translateItemHistory.setTranslateItem(exitTranslateItem);

					translateItemHistoryRepository
							.saveAndFlush(translateItemHistory);

					String oldContent = exitTranslateItem.getContent();
					exitTranslateItem.setContent(content);
					exitTranslateItem.setStatus(Status.Updated);
					exitTranslateItem.setUpdater(WebUtil.getUsername());
					exitTranslateItem.setUpdateDate(new Date());

					translateItemRepository.saveAndFlush(exitTranslateItem);

					if (!StringUtils.equals(oldContent, content)) {
						mail2.put(
								language.getDescription() + "["
										+ language.getName() + "]",
								oldContent + SysConstant.CHANGE_TO + content);
					}

					logWithProperties(Action.Update, Target.TranslateItem,
							"content", content, oldContent);
				} else {
					TranslateItem item = new TranslateItem();
					item.setContent(content);
					item.setCreateDate(new Date());
					item.setCreator(WebUtil.getUsername());
					item.setLanguage(language);
					item.setStatus(Status.New);
					item.setTranslate(translate);

					translateItemRepository.saveAndFlush(item);

					if (!StringUtils.equals(StringUtil.EMPTY,
							item.getContent())) {
						mail2.put(
								language.getDescription() + "["
										+ language.getName() + "]",
								SysConstant.CHANGE_TO + item.getContent());
					}

					logWithProperties(Action.Create, Target.TranslateItem,
							"content", item);

					translate.getTranslateItems().add(item);
				}

			}

			translate.setSearch(translate.toString());

			translateRepository.saveAndFlush(translate);

			loginUser.getWatcherTranslates().add(translate);
			userRepository.saveAndFlush(loginUser);

			log(Action.Update, Target.Translate, translate);

			final Mail mail = Mail.instance().addWatchers(translate)
					.addUsers(getUser(translate.getCreator()));
			// .removeUser(getLoginUser());

			final String href = baseURL + translateAction + "?projectId="
					+ translate.getProject().getId() + "&id="
					+ translate.getId();

			final Project project = translate.getProject();

			ThreadUtil.exec(() -> {

				try {
					mailSender.sendHtml(
							String.format("TMS-翻译更新_%s",
									DateUtil.format(new Date(),
											DateUtil.FORMAT7)),
							TemplateUtil.process(
									"templates/mail/translate-update",
									MapUtil.objArr2Map("translate", translate,
											"user", loginUser, "href", href,
											"project", project, "body",
											mail2.body())),
							mail.get());
					logger.info("翻译更新邮件发送成功！ID:{}", translate.getId());
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("翻译更新邮件发送失败！ID:{}", translate.getId());
				}

			});

		} else {
			logger.error("更新翻译不存在! ID: {}", id);
			return RespBody.failed("更新翻译不存在!");
		}

		return RespBody.succeed(id);
	}

	@RequestMapping(value = "updateKey", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_SUPER", "ROLE_ADMIN", "ROLE_USER" })
	public RespBody updateKey(@RequestParam("id") Long id,
			@RequestParam("baseURL") String baseURL,
			@RequestParam("key") String key) {

		final Translate translate = translateRepository.findOne(id);

		if (translate != null) {

			final Mail mail2 = Mail.instance().put("翻译名称",
					translate.getKey() + SysConstant.CHANGE_TO + key);

			translate.setKey(key);
			translate.setStatus(Status.Updated);
			translate.setUpdater(WebUtil.getUsername());
			translate.setUpdateDate(new Date());

			final User loginUser = getLoginUser();
			translate.getWatchers().add(loginUser);

			translate.setSearch(translate.toString());

			translateRepository.saveAndFlush(translate);

			loginUser.getWatcherTranslates().add(translate);
			userRepository.saveAndFlush(loginUser);

			log(Action.Update, Target.Translate, translate);

			final Mail mail = Mail.instance().addWatchers(translate)
					.addUsers(getUser(translate.getCreator()));
			// .removeUser(getLoginUser());

			final String href = baseURL + translateAction + "?projectId="
					+ translate.getProject().getId() + "&id="
					+ translate.getId();

			final Project project = translate.getProject();

			ThreadUtil.exec(() -> {

				try {
					mailSender.sendHtml(String.format("TMS-翻译更新_%s",
							DateUtil.format(new Date(), DateUtil.FORMAT7)),
							TemplateUtil.process(
									"templates/mail/translate-update",
									MapUtil.objArr2Map("translate", translate,
											"user", loginUser, "href", href,
											"project", project, "body",
											mail2.body())),
							mail.get());
					logger.info("翻译更新邮件发送成功！ID:{}", translate.getId());
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("翻译更新邮件发送失败！ID:{}", translate.getId());
				}

			});

		} else {
			logger.error("更新翻译不存在! ID: {}", id);
			return RespBody.failed("更新翻译不存在!");
		}

		return RespBody.succeed(id);
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_SUPER", "ROLE_ADMIN", "ROLE_USER" })
	public RespBody delete(@RequestParam("id") Long id,
			@RequestParam("baseURL") String baseURL) {

		final Translate translate = translateRepository.findOne(id);

		// TODO 权限判断: 普通用户 只能删除自己创建的, 管理员可以删除所有.

		Long projectId = translate.getProject().getId();
		final Mail mail2 = Mail.instance().parseTranslate(translate);
		final Project project = translate.getProject();

		Set<User> watchers = translate.getWatchers();
		for (User user : watchers) {
			user.getWatcherTranslates().remove(translate);
		}

		userRepository.save(watchers);
		userRepository.flush();

		translateRepository.delete(id);
		translateRepository.flush();

		logWithProperties(Action.Delete, Target.Translate, "id", id);

		final User loginUser = getLoginUser();

		final Mail mail = Mail.instance().addWatchers(translate)
				.addUsers(getUser(translate.getCreator()));
		// .removeUser(loginUser);

		final String href = baseURL + translateAction + "?projectId="
				+ projectId;

		ThreadUtil.exec(() -> {

			try {
				mailSender.sendHtml(String.format("TMS-翻译删除_%s",
						DateUtil.format(new Date(), DateUtil.FORMAT7)),
						TemplateUtil.process("templates/mail/translate-delete",
								MapUtil.objArr2Map("translate", translate,
										"user", loginUser, "deleter",
										loginUser.getUsername(), "deleteDate",
										new Date(), "href", href, "project",
										project, "body", mail2.body())),
						mail.get());
				logger.info("翻译更删除邮件发送成功！ID:{}", translate.getId());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("翻译删除邮件发送失败！ID:{}", translate.getId());
			}

		});

		return RespBody.succeed(id);
	}

	@RequestMapping(value = "deleteTag", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_SUPER", "ROLE_ADMIN", "ROLE_USER" })
	public RespBody deleteTag(@RequestParam("id") Long id,
			@RequestParam("baseURL") String baseURL) {

		Label label = labelRepository.findOne(id);

		final Translate translate = label.getTranslate();

		// final Mail mail = Mail.instance().addWatchers(translate)
		// .addUsers(getUser(translate.getCreator()));
		// .removeUser(getLoginUser());

		// final String href = baseURL + translateAction + "?projectId="
		// + translate.getProject().getId() + "&id=" + translate.getId();

		translate.getLabels().remove(label);
		translate.setUpdateDate(new Date());
		translate.setUpdater(WebUtil.getUsername());
		translate.setStatus(Status.Updated);
		translate.setSearch(translate.toString());

		translateRepository.saveAndFlush(translate);

		labelRepository.delete(label);
		labelRepository.flush();

		log(Action.Delete, Target.Label, label);

		// final Mail mail2 = Mail.instance().put("删除标签", label.getName());
		//
		// final User loginUser = getLoginUser();

		// ThreadUtil.exec(() -> {
		//
		// try {
		// mailSender.sendHtml(String.format("TMS-翻译更新_%s",
		// DateUtil.format(new Date(), DateUtil.FORMAT7)),
		// TemplateUtil.process("templates/mail/translate-update",
		// MapUtil.objArr2Map("translate", translate,
		// "user", loginUser, "href", href,
		// "body", mail2.body())), mail.get());
		// logger.info("翻译更新邮件发送成功！ID:{}", translate.getId());
		// } catch (Exception e) {
		// e.printStackTrace();
		// logger.error("翻译更新邮件发送失败！ID:{}", translate.getId());
		// }
		//
		// });

		return RespBody.succeed(id);
	}

	@RequestMapping(value = "deleteWatcher", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_SUPER", "ROLE_ADMIN", "ROLE_USER" })
	public RespBody deleteWatcher(@RequestParam("id") Long id,
			@RequestParam("username") String username,
			@RequestParam("baseURL") String baseURL) {

		User user = userRepository.findOne(username);

		if (user == null) {
			return RespBody.failed("删除关注者不存在!");
		}

		final Translate translate = translateRepository.findOne(id);

		user.getWatcherTranslates().remove(translate);

		userRepository.save(user);

		translate.getWatchers().remove(user);
		translate.setUpdateDate(new Date());
		translate.setUpdater(WebUtil.getUsername());

		translateRepository.saveAndFlush(translate);

		final Mail mail = Mail.instance()
				.addUsers(getUser(translate.getCreator())).addUsers(user);
		// .removeUser(getLoginUser());

		final String href = baseURL + translateAction + "?projectId="
				+ translate.getProject().getId() + "&id=" + translate.getId();

		logWithProperties(Action.Delete, Target.Translate, "watchers",
				username);

		final Mail mail2 = Mail.instance().put("删除关注者",
				user.getUsername() + (StringUtil.isNotEmpty(user.getName())
						? "[" + user.getName() + "]" : StringUtil.EMPTY));

		final User loginUser = getLoginUser();

		final Project project = translate.getProject();

		ThreadUtil.exec(() -> {

			try {
				mailSender
						.sendHtml(
								String.format("TMS-翻译更新_%s",
										DateUtil.format(new Date(),
												DateUtil.FORMAT7)),
								TemplateUtil.process(
										"templates/mail/translate-update",
										MapUtil.objArr2Map("translate",
												translate, "user", loginUser,
												"href", href, "project",
												project, "body", mail2.body())),
								mail.get());
				logger.info("翻译删除关注者更新邮件发送成功！ID:{}", translate.getId());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("翻译删除关注者更新邮件发送失败！ID:{}", translate.getId());
			}

		});

		return RespBody.succeed(id);
	}

	@RequestMapping(value = "addTag", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_SUPER", "ROLE_ADMIN", "ROLE_USER" })
	public RespBody addTag(@RequestParam("baseURL") String baseURL,
			@RequestParam("id") Long id, @RequestParam("tag") String tag) {

		if (StringUtil.isEmpty(tag)) {
			return RespBody.failed("标签内容不能为空!");
		}

		Translate translate = translateRepository.findOne(id);

		Label label2 = labelRepository.findOneByNameAndTranslate(tag,
				translate);

		if (label2 != null) {
			return RespBody.failed("标签添加重复!");
		}

		Label label = new Label();
		label.setCreateDate(new Date());
		label.setCreator(WebUtil.getUsername());
		label.setName(tag);
		label.setStatus(Status.New);
		label.setTranslate(translate);

		Label label3 = labelRepository.saveAndFlush(label);
		translate.getLabels().add(label3);
		translate.setSearch(translate.toString());

		translateRepository.saveAndFlush(translate);

		log(Action.Create, Target.Label, label);

		// final Mail mail = Mail.instance().addWatchers(translate)
		// .addUsers(getUser(translate.getCreator()));
		// // .removeUser(getLoginUser());
		//
		// final String href = baseURL + translateAction + "?projectId="
		// + translate.getProject().getId() + "&id=" + translate.getId();
		//
		// final User loginUser = getLoginUser();
		//
		// final Mail mail2 = Mail.instance().put("添加标签", label.getName());

		// ThreadUtil.exec(() -> {
		//
		// try {
		// mailSender.sendHtml(String.format("TMS-翻译更新_%s",
		// DateUtil.format(new Date(), DateUtil.FORMAT7)),
		// TemplateUtil.process("templates/mail/translate-update",
		// MapUtil.objArr2Map("translate", translate,
		// "user", loginUser, "href", href,
		// "body", mail2.body())), mail.get());
		// logger.info("翻译更新邮件发送成功！ID:{}", translate.getId());
		// } catch (Exception e) {
		// e.printStackTrace();
		// logger.error("翻译更新邮件发送失败！ID:{}", translate.getId());
		// }
		//
		// });

		return RespBody.succeed(label);
	}

	@RequestMapping(value = "addWatcher", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_SUPER", "ROLE_ADMIN", "ROLE_USER" })
	public RespBody addWatcher(@RequestParam("baseURL") String baseURL,
			@RequestParam("id") Long id,
			@RequestParam("username") String username) {

		if (StringUtil.isEmpty(username)) {
			return RespBody.failed("添加关注者不能为空!");
		}

		final Translate translate = translateRepository.findOne(id);

		User user = userRepository.findOne(username);

		if (user == null) {
			return RespBody.failed("添加关注者不存在!");
		}

		Set<User> watchers = translate.getWatchers();

		for (User user2 : watchers) {
			if (user2.getUsername().equals(user.getUsername())) {
				return RespBody.failed("添加关注者已经存在!");
			}
		}

		watchers.add(user);

		user.getWatcherTranslates().add(translate);

		userRepository.saveAndFlush(user);

		translate.setUpdateDate(new Date());
		translate.setUpdater(WebUtil.getUsername());

		translateRepository.saveAndFlush(translate);

		logWithProperties(Action.Create, Target.Translate, "watchers",
				username);

		final Mail mail = Mail.instance()
				.addUsers(getUser(translate.getCreator())).addUsers(user);
		// .removeUser(getLoginUser());

		final String href = baseURL + translateAction + "?projectId="
				+ translate.getProject().getId() + "&id=" + translate.getId();

		final User loginUser = getLoginUser();

		final Mail mail2 = Mail.instance().put("添加关注者",
				user.getUsername() + (StringUtil.isNotEmpty(user.getName())
						? "[" + user.getName() + "]" : StringUtil.EMPTY));

		final Project project = translate.getProject();

		ThreadUtil.exec(() -> {

			try {
				mailSender
						.sendHtml(
								String.format("TMS-翻译更新_%s",
										DateUtil.format(new Date(),
												DateUtil.FORMAT7)),
								TemplateUtil.process(
										"templates/mail/translate-update",
										MapUtil.objArr2Map("translate",
												translate, "user", loginUser,
												"href", href, "project",
												project, "body", mail2.body())),
								mail.get());
				logger.info("翻译添加关注者更新邮件发送成功！ID:{}", translate.getId());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("翻译添加关注者更新邮件发送失败！ID:{}", translate.getId());
			}

		});

		return RespBody.succeed(user);
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
