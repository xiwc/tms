/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.controller;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lhjz.portal.base.BaseController;
import com.lhjz.portal.component.MailSender2;
import com.lhjz.portal.constant.SysConstant;
import com.lhjz.portal.entity.Chat;
import com.lhjz.portal.entity.Comment;
import com.lhjz.portal.entity.Label;
import com.lhjz.portal.entity.security.Group;
import com.lhjz.portal.entity.security.User;
import com.lhjz.portal.model.RespBody;
import com.lhjz.portal.pojo.Enum.ChatType;
import com.lhjz.portal.pojo.Enum.CommentType;
import com.lhjz.portal.pojo.Enum.Status;
import com.lhjz.portal.repository.ChatRepository;
import com.lhjz.portal.repository.CommentRepository;
import com.lhjz.portal.repository.GroupMemberRepository;
import com.lhjz.portal.repository.GroupRepository;
import com.lhjz.portal.repository.LabelRepository;
import com.lhjz.portal.repository.UserRepository;
import com.lhjz.portal.util.DateUtil;
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
	ChatRepository chatRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	GroupMemberRepository groupMemberRepository;

	@Autowired
	LabelRepository labelRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	MailSender2 mailSender;

	@Value("${lhjz.mail.to.addresses}")
	private String toAddrArr;

	@Autowired
	Environment env;

	@RequestMapping()
	public String home(
			Model model,
			@RequestParam(value = "id", required = false) Long id,
			@RequestParam(value = "search", required = false) String search,
			@PageableDefault(size = 2, sort = { "createDate" }, direction = Direction.DESC) Pageable pageable) {

		Page<Chat> chats = null;

		if (StringUtil.isNotEmpty(id)) {
			long cntGtId = chatRepository.countPublicGtId(id);
			int size = pageable.getPageSize();
			long page = cntGtId / size;
			if (cntGtId % size == 0) {
				page--;
			}

			pageable = new PageRequest(page > -1 ? (int) page : 0, size,
					Direction.DESC, "createDate");

			chats = chatRepository.findByTypeAndPrivated(ChatType.Wiki, false,
					pageable);
		} else if (StringUtil.isNotEmpty(search)) {
			chats = chatRepository.findByTypeAndPrivatedAndContentLike(
					ChatType.Wiki, false, "%" + search + "%", pageable);
		} else {
			chats = chatRepository.findByTypeAndPrivated(ChatType.Wiki, false,
					pageable);
		}

		List<User> users = userRepository.findAll();

		Collections.sort(users);

		List<Group> groups = groupRepository.findAll();
		Collections.sort(groups);

		// login user labels
		List<Label> labels = labelRepository.queryWikiLabels();
		Set<String> lbls = null;
		if (labels != null) {
			lbls = labels.stream().map((label) -> {
				return label.getName();
			}).collect(Collectors.toSet());
		} else {
			lbls = new HashSet<String>();
		}

		model.addAttribute("chats", chats);
		model.addAttribute("users", users);
		model.addAttribute("groups", groups);
		model.addAttribute("user", getLoginUser());
		model.addAttribute("labels", new TreeSet<>(lbls));

		return "index";
	}

	@RequestMapping(value = "free/wiki/more", method = RequestMethod.GET)
	@ResponseBody
	public RespBody moreWiki(
			@RequestParam(value = "search", required = false) String search,
			@PageableDefault(sort = { "createDate" }, direction = Direction.DESC) Pageable pageable) {

		Page<Chat> chats = null;

		if (StringUtil.isNotEmpty(search)) {
			chats = chatRepository.findByTypeAndPrivatedAndContentLike(
					ChatType.Wiki, false, "%" + search + "%", pageable);
		} else {
			chats = chatRepository.findByTypeAndPrivated(ChatType.Wiki, false,
					pageable);
		}

		return RespBody.succeed(chats);
	}

	@RequestMapping(value = "free/wiki/reply", method = RequestMethod.POST)
	@ResponseBody
	public RespBody replyWiki(@RequestParam("baseURL") String baseURL,
			@RequestParam("id") Long id, @RequestParam("content") String content) {

		User user = getLoginUser();

		Comment comment = new Comment();
		comment.setContent(content);
		comment.setTargetId(String.valueOf(id));
		comment.setCreateDate(new Date());
		comment.setCreator(user);
		comment.setStatus(Status.New);
		comment.setType(CommentType.Reply);

		Comment comment2 = commentRepository.saveAndFlush(comment);

		final String href = baseURL + "?id=" + id;
		final String content2 = content;
		final User loginUser;
		if (user == null) {
			loginUser = new User();
			loginUser.setUsername(SysConstant.USER_VISITOR);
			loginUser.setName(SysConstant.USER_NAME_VISITOR);
		} else {
			loginUser = user;
		}

		ThreadUtil.exec(() -> {

			try {
				Thread.sleep(3000);
				mailSender.sendHtml(String.format("TMS-博文回复_%s",
						DateUtil.format(new Date(), DateUtil.FORMAT7)),
						TemplateUtil.process("templates/mail/mail-dynamic",
								MapUtil.objArr2Map("user", loginUser, "date",
										new Date(), "href", href, "title",
										"博文回复消息", "content", content2)),
						StringUtil.split(toAddrArr, ","));
				logger.info("博文回复邮件发送成功！");
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("博文回复邮件发送失败！");
			}

		});

		return RespBody.succeed(comment2);
	}

	@RequestMapping(value = "login", method = RequestMethod.GET)
	public String login() {
		return "redirect:admin/login";
	}

	@RequestMapping(value = "register", method = RequestMethod.GET)
	public String register() {
		return "register";
	}
}
