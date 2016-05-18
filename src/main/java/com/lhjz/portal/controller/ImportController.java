/**
 * 立衡脊柱版权所有 (lhjz)
 */
package com.lhjz.portal.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lhjz.portal.base.BaseController;
import com.lhjz.portal.entity.Language;
import com.lhjz.portal.entity.Project;
import com.lhjz.portal.entity.Translate;
import com.lhjz.portal.entity.TranslateItem;
import com.lhjz.portal.model.RespBody;
import com.lhjz.portal.pojo.Enum.Status;
import com.lhjz.portal.repository.AuthorityRepository;
import com.lhjz.portal.repository.LanguageRepository;
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
	AuthorityRepository authorityRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	private void joinKV(JsonObject jsonObject, String key, Map<String, String> kvMaps) {
		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			String k = StringUtil.isEmpty(key) ? entry.getKey() : StringUtil.join2(".", key, entry.getKey());
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
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	public RespBody save(@RequestParam("projectId") Long projectId, @RequestParam("languageId") Long languageId,
			@RequestParam("content") String content) {

		Project project = projectRepository.findOne(projectId);
		Language language2 = languageRepository.findOne(languageId);

		JsonObject jsonO = (JsonObject) JsonUtil.toJsonElement(content);

		Map<String, String> kvMaps = new HashMap<>();
		joinKV(jsonO, "", kvMaps);

		List<TranslateItem> translateItems2 = new ArrayList<TranslateItem>();
		List<Translate> translates2 = new ArrayList<Translate>();

		for (String key : kvMaps.keySet()) {
			List<Translate> translates = translateRepository.findByKeyAndProject(key, project);
			if (translates.size() > 0) {
				Translate translate2 = translates.get(0);
				Set<TranslateItem> translateItems = translate2.getTranslateItems();
				Language language = null;
				for (TranslateItem translateItem : translateItems) {
					if (translateItem.getLanguage().getId().equals(languageId)) {
						language = translateItem.getLanguage();
						translateItem.setContent(kvMaps.get(key));
						translateItems2.add(translateItem);
						// translateItemRepository.saveAndFlush(translateItem);
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

					translateItems2.add(translateItem);
					// translateItemRepository.saveAndFlush(translateItem);
				}
			} else {
				Translate translate = new Translate();
				translate.setCreateDate(new Date());
				translate.setCreator(WebUtil.getUsername());
				translate.setDescription("导入翻译项(暂无描述)");
				translate.setKey(key);
				translate.setProject(project);
				translate.setStatus(Status.New);
				Set<TranslateItem> translateItems = translate.getTranslateItems();

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
				// translateRepository.saveAndFlush(translate);
			}
		}

		translateItemRepository.save(translateItems2);
		translateItemRepository.flush();
		translateRepository.save(translates2);
		translateRepository.flush();

		return RespBody.succeed();
	}

}
