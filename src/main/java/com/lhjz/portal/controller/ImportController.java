/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.controller;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lhjz.portal.base.BaseController;
import com.lhjz.portal.component.MailSender2;
import com.lhjz.portal.entity.Label;
import com.lhjz.portal.entity.Language;
import com.lhjz.portal.entity.Project;
import com.lhjz.portal.entity.Translate;
import com.lhjz.portal.entity.TranslateItem;
import com.lhjz.portal.entity.security.User;
import com.lhjz.portal.model.Mail;
import com.lhjz.portal.model.RespBody;
import com.lhjz.portal.pojo.Enum.Action;
import com.lhjz.portal.pojo.Enum.Status;
import com.lhjz.portal.pojo.Enum.Target;
import com.lhjz.portal.repository.AuthorityRepository;
import com.lhjz.portal.repository.LabelRepository;
import com.lhjz.portal.repository.LanguageRepository;
import com.lhjz.portal.repository.ProjectRepository;
import com.lhjz.portal.repository.TranslateItemRepository;
import com.lhjz.portal.repository.TranslateRepository;
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
@RequestMapping("admin/import")
public class ImportController extends BaseController {

	static Logger logger = LoggerFactory.getLogger(ImportController.class);

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

	@Autowired
	MailSender2 mailSender;

	String translateAction = "admin/translate";

	private void joinKV(JsonObject jsonObject, String key,
			Map<String, String> kvMaps) {
		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			String k = StringUtil.isEmpty(key) ? entry.getKey()
					: StringUtil.join2(".", key, entry.getKey());
			JsonElement jsonE = entry.getValue();
			if (jsonE.isJsonPrimitive()) {
				kvMaps.put(k, jsonE.getAsString());
			} else if (jsonE.isJsonObject()) {
				joinKV((JsonObject) jsonE, k, kvMaps);
			}
		}
	}

	@RequestMapping(value = "save", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_SUPER", "ROLE_ADMIN", "ROLE_USER" })
	public RespBody save(@RequestParam("projectId") Long projectId,
			@RequestParam("languageId") Long languageId,
			@RequestParam("type") Long type,
			@RequestParam("baseURL") String baseURL,
			@RequestParam(value = "notify", defaultValue = "0") Boolean notify,
			@RequestParam(value = "labels", required = false) String labels,
			@RequestParam("content") String content) {

		Project project = projectRepository.findOne(projectId);
		Language language2 = languageRepository.findOne(languageId);

		Map<String, String> kvMaps = new HashMap<>();
		if (type == 1) {// JSON
			joinKV((JsonObject) JsonUtil.toJsonElement(content), "", kvMaps);
		} else { // Property
			Properties properties = new Properties();
			try {
				properties.load(new StringReader(content));
			} catch (IOException e) {
				e.printStackTrace();
				return RespBody.failed(e.getMessage());
			}
			Set<Object> keySet = properties.keySet();
			for (Object k : keySet) {
				kvMaps.put(String.valueOf(k), properties
						.getProperty(String.valueOf(k), StringUtil.EMPTY));
			}
		}

		List<TranslateItem> translateItems2 = new ArrayList<TranslateItem>();
		List<Translate> translates2 = new ArrayList<Translate>();
		Set<Translate> translates3 = new HashSet<Translate>();

		for (String key : kvMaps.keySet()) {
			List<Translate> translates = translateRepository
					.findByKeyAndProject(key, project);
			if (translates.size() > 0) { // 已经存在,做更新处理
				Translate translate2 = translates.get(0);
				Set<TranslateItem> translateItems = translate2
						.getTranslateItems();
				Language language = null;
				for (TranslateItem translateItem : translateItems) {
					if (translateItem.getLanguage().getId()
							.equals(languageId)) {
						language = translateItem.getLanguage();
						translateItem.setContent(kvMaps.get(key));
						translateItems2.add(translateItem);
					}
				}

				if (language == null) {
					TranslateItem translateItem = new TranslateItem();
					translateItem.setContent(kvMaps.get(key));
					translateItem.setCreateDate(new Date());
					translateItem.setCreator(WebUtil.getUsername());
					translateItem.setLanguage(language2);
					translateItem.setStatus(Status.New);
					translateItem.setTranslate(translate2);

					translate2.getTranslateItems().add(translateItem);

					translateItems2.add(translateItem);
				}

				translates3.add(translate2);

			} else { // 不存在, 做新建处理
				Translate translate = new Translate();
				translate.setCreateDate(new Date());
				translate.setCreator(WebUtil.getUsername());
				translate.setDescription(kvMaps.get(key));
				translate.setKey(key);
				translate.setProject(project);
				translate.setStatus(Status.New);
				Set<TranslateItem> translateItems = translate
						.getTranslateItems();

				Set<Language> languages = project.getLanguages();

				for (Language language : languages) {
					TranslateItem item = new TranslateItem();
					if (language.getId().equals(languageId)) {
						item.setContent(kvMaps.get(key));
					} else {
						item.setContent(StringUtil.EMPTY);
					}
					item.setCreateDate(new Date());
					item.setCreator(WebUtil.getUsername());
					item.setLanguage(language);
					item.setStatus(Status.New);
					item.setTranslate(translate);

					translateItems.add(item);
				}

				translates2.add(translate);
			}
		}

		translateItemRepository.save(translateItems2);
		translateItemRepository.flush();

		// 更新翻译search属性
		for (Translate translate : translates3) {
			translate.setSearch(translate.toString());
			translate.setUpdateDate(new Date());
			translate.setUpdater(WebUtil.getUsername());
		}
		translateRepository.save(translates3);
		translateRepository.flush();

		// 只有新建的会打标签
		String[] lbls = null;
		if (StringUtil.isNotEmpty(labels)) {
			lbls = labels.split(",");
		}

		// TODO 不是新建只是更新的 search 没有更新.
		for (Translate translate : translates2) {
			translate.setSearch(translate.toString());
		}

		List<Translate> translates = translateRepository.save(translates2);
		translateRepository.flush();

		Mail mail2 = Mail.instance();
		if (lbls != null) {
			for (Translate translate : translates) {

				Set<Label> labels2 = new HashSet<>();
				for (String lbl : lbls) {
					Label label = new Label();
					label.setCreateDate(new Date());
					label.setCreator(WebUtil.getUsername());
					label.setName(lbl);
					label.setStatus(Status.New);
					label.setTranslate(translate);

					labels2.add(label);
				}
				labelRepository.save(labels2);
				labelRepository.flush();

				translate.setLabels(labels2);

			}
		}

		// TODO 再次保存labels更新
		// translates = translateRepository.save(translates2);
		// translateRepository.flush();

		mail2.addHref(baseURL, translateAction, projectId, translates);

		log(Action.Import, Target.Import, content);

		final Mail mail = Mail.instance().addWatchers(project);

		final User loginUser = getLoginUser();

		final String href = baseURL + translateAction + "?projectId="
				+ projectId;

		// 如果邮件通知
		if (notify && mail.get().length > 0) {

			ThreadUtil.exec(() -> {

				try {
					mailSender.sendHtml(
							String.format("TMS-翻译导入_%s",
									DateUtil.format(new Date(),
											DateUtil.FORMAT7)),
							TemplateUtil.process(
									"templates/mail/translate-import",
									MapUtil.objArr2Map("user", loginUser,
											"importDate", new Date(), "href",
											href, "body", mail2.hrefs())),
							mail.get());
					logger.info("批量导入翻译邮件发送成功！");
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("批量导入翻译邮件发送失败！");
				}

			});
		}

		return RespBody.succeed(kvMaps.size());
	}

	@RequestMapping(value = "export", method = RequestMethod.POST)
	@ResponseBody
	@Secured({ "ROLE_SUPER", "ROLE_ADMIN", "ROLE_USER" })
	public RespBody export(@RequestParam("projectId") Long projectId,
			@RequestParam("languageId") Long languageId,
			@RequestParam("type") Long type) {

		Project project = projectRepository.findOne(projectId);
		Set<Translate> translates = project.getTranslates();

		Map<String, String> map = new HashMap<String, String>();

		for (Translate translate : translates) {
			Set<TranslateItem> translateItems = translate.getTranslateItems();
			for (TranslateItem translateItem : translateItems) {
				if (translateItem.getLanguage().getId().equals(languageId)) {
					map.put(translate.getKey(), translateItem.getContent());
				}
			}
		}

		if (type == 1) {
			JsonObject root = new JsonObject();
			for (String k : map.keySet()) {
				String[] keys = k.split("\\.");
				JsonObject lastE = root;
				for (int i = 0; i < keys.length; i++) {
					if (i < keys.length - 1) {
						if (!lastE.has(keys[i])) {
							lastE.add(keys[i], new JsonObject());
						}

						lastE = (JsonObject) lastE.get(keys[i]);
					} else {
						lastE.addProperty(keys[i], map.get(k));
					}
				}
			}

			String data = JsonUtil.toJson(root);
			log(Action.Export, Target.Import, data);

			return RespBody.succeed(data).addMsg(map.size());
		} else {
			List<String> list = new ArrayList<String>();
			for (String k : map.keySet()) {
				String v = map.get(k);
				v = StringUtil.join("\\\n",
						(StringUtil.isNotEmpty(v) ? v : StringUtil.EMPTY)
								.split("\n"));
				list.add(k + "=" + v);
			}

			Collections.sort(list);

			String data = StringUtil.join("\r\n", list);
			log(Action.Export, Target.Import, data);

			return RespBody.succeed(data).addMsg(map.size());
		}
	}
}
