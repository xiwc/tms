/**
 * 立衡脊柱版权所有 (lhjz)
 */
package com.lhjz.portal.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.lhjz.portal.base.BaseController;
import com.lhjz.portal.component.MailSender2;
import com.lhjz.portal.entity.Article;
import com.lhjz.portal.entity.Config;
import com.lhjz.portal.entity.Diagnose;
import com.lhjz.portal.entity.Feedback;
import com.lhjz.portal.entity.Job;
import com.lhjz.portal.entity.JobApply;
import com.lhjz.portal.entity.Settings;
import com.lhjz.portal.model.Message;
import com.lhjz.portal.model.RespBody;
import com.lhjz.portal.pojo.ContactForm;
import com.lhjz.portal.pojo.DiagnoseForm;
import com.lhjz.portal.pojo.Enum.Action;
import com.lhjz.portal.pojo.Enum.Key;
import com.lhjz.portal.pojo.Enum.Module;
import com.lhjz.portal.pojo.Enum.Page;
import com.lhjz.portal.pojo.Enum.Target;
import com.lhjz.portal.pojo.FeedbackForm;
import com.lhjz.portal.repository.ArticleRepository;
import com.lhjz.portal.repository.ConfigRepository;
import com.lhjz.portal.repository.DiagnoseRepository;
import com.lhjz.portal.repository.FeedbackRepository;
import com.lhjz.portal.repository.JobApplyRepository;
import com.lhjz.portal.repository.JobRepository;
import com.lhjz.portal.repository.SettingsRepository;
import com.lhjz.portal.util.DateUtil;
import com.lhjz.portal.util.FileUtil;
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
@RequestMapping()
public class RootController extends BaseController {

	static final Logger logger = LoggerFactory.getLogger(RootController.class);

	@Autowired
	DiagnoseRepository diagnoseRepository;

	@Autowired
	SettingsRepository settingsRepository;

	@Autowired
	ArticleRepository articleRepository;

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

	@Autowired
	JobRepository jobRepository;

	@Autowired
	JobApplyRepository jobApplyRepository;

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

	@RequestMapping("article")
	public String article(@RequestParam(value = "id", required = true) Long id,
			Model model) {

		Article article = articleRepository.findOne(id);

		if (article == null) {
			model.addAttribute("error", Message.error("您查看的文章不存在,或已经被删除!"));
			return "landing/error";
		}

		model.addAttribute("article", article);

		return "landing/article";
	}

	@RequestMapping("job")
	public String job(Model model) {

		List<Settings> settings = settingsRepository.findByPage(Page.Job);

		List<Settings> introductions = new ArrayList<Settings>();

		for (Settings settings2 : settings) {
			if (settings2.getModule() == Module.Introduction) {
				introductions.add(settings2);
			}
		}

		model.addAttribute("introductions", introductions);

		List<Job> jobs = jobRepository.findAll();

		for (Job job : jobs) {
			String labels = job.getLabels();

			if (labels != null) {
				String[] arr = labels.split(",");

				for (String lbl : arr) {
					String[] arr2 = lbl.trim().split("\\s+");
					job.getLabelList().addAll(Arrays.asList(arr2));
				}
			}
		}

		model.addAttribute("jobs", jobs);

		return "landing/job";
	}

	@RequestMapping(value = "diagnose/save", method = RequestMethod.POST)
	@ResponseBody
	public RespBody diagnoseSave(@Valid DiagnoseForm diagnoseForm,
			BindingResult bindingResult) {

		logger.debug("Enter method: {}", "save");

		if (bindingResult.hasErrors()) {
			return RespBody.failed(bindingResult.getAllErrors().stream()
					.map(err -> err.getDefaultMessage())
					.collect(Collectors.joining("<br/>")));
		}

		if (StringUtil.isEmpty(diagnoseForm.getMail())
				&& StringUtil.isEmpty(diagnoseForm.getPhone())) {
			return RespBody.failed("手机或者邮箱必须填写一个。");
		}

		if (StringUtil.isNotEmpty(diagnoseForm.getMail())
				&& !StringUtil.isEmail(diagnoseForm.getMail())) {
			return RespBody.failed("邮箱格式输入不正确，请核对后输入！");
		}

		if (StringUtil.isNotEmpty(diagnoseForm.getPhone())
				&& !StringUtil.isMobile(diagnoseForm.getPhone())) {
			return RespBody.failed("手机号格式输入不正确，请核对后输入！");
		}

		if (StringUtil.isNotEmpty(diagnoseForm.getMail())) {
			List<Diagnose> diagnoses = diagnoseRepository
					.findByMailAndDescription(diagnoseForm.getMail(),
							diagnoseForm.getDescription());
			if (diagnoses.size() > 0) {
				return RespBody.failed("您你症状描述已经存在，不能重复提交！");
			}
		}

		if (StringUtil.isNotEmpty(diagnoseForm.getPhone())) {
			List<Diagnose> diagnoses = diagnoseRepository
					.findByPhoneAndDescription(diagnoseForm.getPhone(),
							diagnoseForm.getDescription());
			if (diagnoses.size() > 0) {
				return RespBody.failed("您你症状描述已经存在，不能重复提交！");
			}
		}

		Diagnose diagnose = new Diagnose();
		diagnose.setMail(diagnoseForm.getMail());
		diagnose.setPhone(diagnoseForm.getPhone());
		diagnose.setDescription(diagnoseForm.getDescription());
		diagnose.setCreateDate(new Date());

		Diagnose diagnose2 = diagnoseRepository.save(diagnose);

		log(Action.Create, Target.Diagnose, diagnose2);

		ThreadUtil
				.exec(() -> {

					try {
						String[] mailTo = StringUtil.split(
								env.getProperty("lhjz.mail.diagnose.to.addresses"),
								",");
						mailSender.sendHtml(String.format("立衡脊柱-病症描述_%s",
								DateUtil.format(new Date(), DateUtil.FORMAT2)),
								TemplateUtil.process(
										"templates/mail/diagnosis", MapUtil
												.objArr2Map("diagnose",
														diagnose)), mailTo);

						logger.debug("在线诊断-症状描述邮件发送状态: {}", "成功");
					} catch (Exception e) {
						e.printStackTrace();
						logger.debug("在线诊断-症状描述邮件发送状态: {}", "失败");
					}
				});

		return RespBody.succeed("在线诊断提交成功，我们将尽快给予您回复！");
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

	@RequestMapping(value = "job/upload", method = RequestMethod.POST)
	@ResponseBody
	public RespBody jobUpload(HttpServletRequest request, Locale locale,
			@RequestParam("file") MultipartFile[] files,
			@RequestParam("jobId") Long jobId) {

		logger.debug("upload job file start...");

		String realPath = WebUtil.getRealPath(request);

		String storePath = env.getProperty("lhjz.upload.job.store.path");

		try {
			// make upload dir if not exists
			FileUtils.forceMkdir(new File(FileUtil.joinPaths(realPath,
					storePath)));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return RespBody.failed(e.getMessage());
		}

		List<JobApply> saveFiles = new ArrayList<JobApply>();

		RespBody respBody = RespBody.succeed();

		for (MultipartFile file : files) {

			String originalFileName = file.getOriginalFilename();

			String type = originalFileName.substring(originalFileName
					.lastIndexOf("."));

			String uuid = UUID.randomUUID().toString();

			String uuidName = StringUtil.replace("{?1}{?2}", uuid, type);

			// relative file path
			String path = FileUtil.joinPaths(storePath, uuidName);

			// absolute file path
			final String filePath = FileUtil.joinPaths(realPath, path);

			try {

				// store into webapp dir
				file.transferTo(new File(filePath));

				// 保存记录到数据库
				JobApply jobApply = new JobApply();
				jobApply.setResume(path);
				jobApply.setName(originalFileName);

				Job job = new Job();
				job.setId(jobId);
				jobApply.setJob(job);

				jobApply.setCreateDate(new Date());

				JobApply jobApply2 = jobApplyRepository.saveAndFlush(jobApply);

				saveFiles.add(jobApply2);

				log(Action.Upload, Target.JobApply, jobApply2);

				// send mail to job handler.
				ThreadUtil
						.exec(() -> {
							try {
								mailSender.sendHtmlWithAttachment(
										String.format("立恒脊柱-职位申请_%s", DateUtil
												.format(new Date(),
														DateUtil.FORMAT2)),
										"<!DOCTYPE html> <html lang='zh-cn'> <head> <meta charset='utf-8' /> <meta http-equiv='X-UA-Compatible' content='IE=edge' /> <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'> <meta name='viewport' content='width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no' /> <title>立衡脊柱 - 职位申请</title> </head> <body> <h3>职位申请用户简历见邮件附件!</h3> </body> </html>",
										new String[] { filePath }, StringUtil
												.split(toAddrArrForJob, ","));
							} catch (Exception e) {
								e.printStackTrace();
								logger.error(e.getMessage(), e);
							}
						});

			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage(), e);
				return RespBody.failed(e.getMessage());
			}
		}

		// back relative file path
		return respBody.data(saveFiles);
	}
}
