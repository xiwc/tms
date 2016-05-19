/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lhjz.portal.base.BaseController;
import com.lhjz.portal.component.MailSender2;
import com.lhjz.portal.entity.Config;
import com.lhjz.portal.entity.Feedback;
import com.lhjz.portal.entity.Settings;
import com.lhjz.portal.model.RespBody;
import com.lhjz.portal.pojo.ContactForm;
import com.lhjz.portal.pojo.Enum.Action;
import com.lhjz.portal.pojo.Enum.Key;
import com.lhjz.portal.pojo.Enum.Module;
import com.lhjz.portal.pojo.Enum.Page;
import com.lhjz.portal.pojo.Enum.Target;
import com.lhjz.portal.pojo.FeedbackForm;
import com.lhjz.portal.repository.ConfigRepository;
import com.lhjz.portal.repository.FeedbackRepository;
import com.lhjz.portal.repository.SettingsRepository;
import com.lhjz.portal.util.DateUtil;
import com.lhjz.portal.util.JsonUtil;
import com.lhjz.portal.util.MapUtil;
import com.lhjz.portal.util.StringUtil;
import com.lhjz.portal.util.TemplateUtil;
import com.lhjz.portal.util.ThreadUtil;

/**
 * 
 * @author xi
 * 
 * @date 2015年3月28日 下午1:19:05
 * 
 */
@Controller
@RequestMapping()
public class RootController extends BaseController {

	static final Logger logger = LoggerFactory.getLogger(RootController.class);

	@Autowired
	SettingsRepository settingsRepository;

	@Autowired
	ConfigRepository configRepository;

	@Autowired
	FeedbackRepository feedbackRepository;

	@Autowired
	MailSender2 mailSender;

	@Value("${lhjz.mail.to.addresses}")
	private String toAddrArr;

	@Value("${lhjz.mail.job.to.addresses}")
	private String toAddrArrForJob;

	@Autowired
	Environment env;

	@SuppressWarnings("unchecked")
	@ModelAttribute("pageEnable")
	public Map<String, Object> pageEnable() {

		Config config = configRepository.findFirstByKey(Key.PageEnable);

		if (config != null) {
			return JsonUtil.json2Object(config.getValue(), Map.class);
		}

		return null;
	}

	@RequestMapping()
	public String home(HttpServletRequest request, Model model) {

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

		logWithProperties(Action.Visit, Target.Page, Page.Index.name(),
				request.getRemoteAddr());

		return "landing/index";
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

		return "landing/about";
	}

	@RequestMapping("case")
	public String cases(Model model) {

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

		return "landing/case";
	}

	@RequestMapping("diagnose")
	public String diagnose(Model model) {
		return "landing/diagnose";
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

		return "landing/contact";
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

		return "landing/env";
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

		return "landing/feature";
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

		return "landing/health";
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

		return "landing/product";
	}


	@RequestMapping(value = "feedback/save", method = RequestMethod.POST)
	@ResponseBody
	public RespBody feedbackSave(@Valid FeedbackForm feedbackForm,
			BindingResult bindingResult) {

		logger.debug("Enter method: {}", "feedbackSave");

		if (bindingResult.hasErrors()) {
			return RespBody.failed(bindingResult.getAllErrors().stream()
					.map(err -> err.getDefaultMessage())
					.collect(Collectors.joining("<br/>")));
		}

		List<Feedback> feedbacks = feedbackRepository
				.findByContent(feedbackForm.getContent());

		if (feedbacks.size() > 0) {
			return RespBody.failed("您已经反馈过该内容！");
		}

		Feedback feedback = new Feedback();
		feedback.setContent(feedbackForm.getContent());
		feedback.setCreateDate(new Date());
		feedback.setMail(feedbackForm.getMail());
		feedback.setName(feedbackForm.getName());
		feedback.setPhone(feedbackForm.getPhone());
		feedback.setUsername(env.getProperty("lhjz.anonymous.user.name",
				"anonymous_user"));
		feedback.setUrl(feedbackForm.getUrl());

		final Feedback feedback2 = feedbackRepository.saveAndFlush(feedback);

		log(Action.Create, Target.Feedback, feedback2);

		ThreadUtil.exec(() -> {

			try {
				mailSender.sendHtml(
						String.format("立恒脊柱-用户反馈_%s",
								DateUtil.format(new Date(), DateUtil.FORMAT2)),
						TemplateUtil.process("templates/mail/feedback",
								MapUtil.objArr2Map("feedback", feedback2)),
						StringUtil.split(toAddrArr, ","));
				logger.info("反馈邮件发送成功！ID:{}", feedback2.getId());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("反馈邮件发送失败！ID:{}", feedback2.getId());
			}

		});

		return RespBody.succeed("反馈提交成功，谢谢！");
	}

}
