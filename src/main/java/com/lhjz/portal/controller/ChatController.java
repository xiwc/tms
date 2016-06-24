/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lhjz.portal.base.BaseController;
import com.lhjz.portal.component.MailSender2;
import com.lhjz.portal.entity.Chat;
import com.lhjz.portal.entity.Log;
import com.lhjz.portal.entity.security.User;
import com.lhjz.portal.model.Mail;
import com.lhjz.portal.model.RespBody;
import com.lhjz.portal.pojo.Enum.Action;
import com.lhjz.portal.pojo.Enum.Status;
import com.lhjz.portal.pojo.Enum.Target;
import com.lhjz.portal.pojo.Enum.VoteType;
import com.lhjz.portal.repository.ChatRepository;
import com.lhjz.portal.repository.LogRepository;
import com.lhjz.portal.util.CollectionUtil;
import com.lhjz.portal.util.DateUtil;
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
@RequestMapping("admin/chat")
public class ChatController extends BaseController {

	static Logger logger = LoggerFactory.getLogger(ChatController.class);

	@Autowired
	ChatRepository chatRepository;

	@Autowired
	LogRepository logRepository;

	@Autowired
	MailSender2 mailSender;

	String dynamicAction = "admin/dynamic";

	@RequestMapping(value = "create", method = RequestMethod.POST)
	@ResponseBody
	public RespBody create(@RequestParam("baseURL") String baseURL,
			@RequestParam(value = "usernames", required = false) String usernames,
			@RequestParam("content") String content,
			@RequestParam(value = "preMore", defaultValue = "true") Boolean preMore,
			@RequestParam("lastId") Long lastId,
			@RequestParam("contentHtml") String contentHtml) {

		if (StringUtil.isEmpty(content)) {
			return RespBody.failed("提交内容不能为空!");
		}

		Chat chat = new Chat();
		chat.setContent(content);
		chat.setCreateDate(new Date());
		chat.setCreator(getLoginUser());
		chat.setStatus(Status.New);

		Chat chat2 = chatRepository.saveAndFlush(chat);

		log(Action.Create, Target.Chat, chat2.getId());

		final User loginUser = getLoginUser();
		final String href = baseURL + dynamicAction + "?id=" + chat2.getId();
		final String html = contentHtml;

		final Mail mail = Mail.instance();
		if (StringUtil.isNotEmpty(usernames)) {
			String[] usernameArr = usernames.split(",");
			Arrays.asList(usernameArr).stream().forEach((username) -> {
				mail.addUsers(getUser(username));
			});

			ThreadUtil.exec(() -> {

				try {
					Thread.sleep(3000);
					mailSender.sendHtml(String.format("TMS-沟通动态@消息_%s",
							DateUtil.format(new Date(), DateUtil.FORMAT7)),
							TemplateUtil.process("templates/mail/mail-dynamic",
									MapUtil.objArr2Map("user", loginUser,
											"date", new Date(), "href", href,
											"title", "下面的沟通消息中有@到你", "content",
											html)),
							mail.get());
					logger.info("沟通邮件发送成功！");
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("沟通邮件发送失败！");
				}

			});
		}

		if (!preMore && chatRepository.countQueryRecent(lastId) <= 50) {
			List<Chat> chats = chatRepository.queryRecent(lastId);
			return RespBody.succeed(chats);
		}

		return RespBody.succeed(chat2);
	}

	@RequestMapping(value = "update", method = RequestMethod.POST)
	@ResponseBody
	public RespBody update(@RequestParam("id") Long id,
			@RequestParam("content") String content,
			@RequestParam("baseURL") String baseURL,
			@RequestParam(value = "usernames", required = false) String usernames,
			@RequestParam("contentHtmlOld") String contentHtmlOld,
			@RequestParam("contentHtml") String contentHtml) {

		if (StringUtil.isEmpty(content)) {
			return RespBody.failed("修改内容不能为空!");
		}

		Chat chat = chatRepository.findOne(id);
		chat.setContent(content);
		chat.setUpdateDate(new Date());
		chat.setUpdater(getLoginUser());
		chat.setStatus(Status.Updated);

		Chat chat2 = chatRepository.saveAndFlush(chat);

		log(Action.Update, Target.Chat, chat2.getId());

		final User loginUser = getLoginUser();
		final String href = baseURL + dynamicAction + "?id=" + chat2.getId();
		final String html = "<h3>编辑后内容:</h3>" + contentHtml
				+ "<hr/><h3>编辑前内容:</h3>" + contentHtmlOld;

		final Mail mail = Mail.instance();
		if (StringUtil.isNotEmpty(usernames)) {
			String[] usernameArr = usernames.split(",");
			Arrays.asList(usernameArr).stream().forEach((username) -> {
				mail.addUsers(getUser(username));
			});

			ThreadUtil.exec(() -> {

				try {
					Thread.sleep(3000);
					mailSender.sendHtml(
							String.format("TMS-沟通动态编辑@消息_%s",
									DateUtil.format(new Date(),
											DateUtil.FORMAT7)),
							TemplateUtil.process("templates/mail/mail-dynamic",
									MapUtil.objArr2Map("user", loginUser,
											"date", new Date(), "href", href,
											"title", "下面编辑的沟通消息中有@到你",
											"content", html)),
							mail.get());
					logger.info("沟通编辑邮件发送成功！");
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("沟通编辑邮件发送失败！");
				}

			});
		}

		return RespBody.succeed(chat2);
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	@ResponseBody
	public RespBody delete(@RequestParam("id") Long id) {

		Chat chat = chatRepository.findOne(id);
		if (chat == null) {
			return RespBody.failed("删除聊天内容不存在!");
		}

		chatRepository.delete(chat);
		chatRepository.flush();

		log(Action.Delete, Target.Chat, chat.getId(), chat);

		return RespBody.succeed(id);
	}

	@RequestMapping(value = { "get", "get/unmask" }, method = RequestMethod.GET)
	@ResponseBody
	public RespBody get(@RequestParam("id") Long id) {

		Chat chat = chatRepository.findOne(id);
		if (chat == null) {
			return RespBody.failed("获取聊天内容不存在!");
		}

		return RespBody.succeed(chat);
	}

	@RequestMapping(value = { "poll",
			"poll/unmask" }, method = RequestMethod.GET)
	@ResponseBody
	public RespBody poll(@RequestParam("lastId") Long lastId,
			@RequestParam("lastEvtId") Long lastEvtId) {

		long cnt = chatRepository.countQueryRecent(lastId);
		long cntLogs = logRepository.countQueryRecent(lastEvtId);

		Long[] data = new Long[] { lastId, cnt, lastEvtId, cntLogs };

		List<Log> logs = logRepository.queryRecent(lastEvtId);

		return RespBody.succeed(data).addMsg(logs);
	}

	@RequestMapping(value = "getNews", method = RequestMethod.GET)
	@ResponseBody
	public RespBody getNews(@RequestParam("lastId") Long lastId) {

		List<Chat> chats = chatRepository.queryRecent(lastId);

		return RespBody.succeed(chats);
	}

	@RequestMapping(value = "more", method = RequestMethod.GET)
	@ResponseBody
	public RespBody more(@PageableDefault(sort = {
			"createDate" }, direction = Direction.DESC) Pageable pageable) {

		Page<Chat> chats = chatRepository.findAll(pageable);
		chats = new PageImpl<Chat>(
				CollectionUtil.reverseList(chats.getContent()), pageable,
				chats.getTotalElements());

		return RespBody.succeed(chats);
	}

	@RequestMapping(value = "moreLogs", method = RequestMethod.GET)
	@ResponseBody
	public RespBody moreLogs(@PageableDefault(sort = {
			"createDate" }, direction = Direction.DESC) Pageable pageable) {

		Page<Log> logs = logRepository.findByTarget(Target.Translate, pageable);

		return RespBody.succeed(logs);
	}

	@RequestMapping(value = { "search",
			"search/unmask" }, method = RequestMethod.GET)
	@ResponseBody
	public RespBody search(
			@PageableDefault(sort = {
					"createDate" }, direction = Direction.DESC) Pageable pageable,
			@RequestParam(value = "search", required = true) String search) {

		Page<Chat> chats = chatRepository.findByContentLike("%" + search + "%",
				pageable);
		// chats = new PageImpl<Chat>(CollectionUtil.reverseList(chats
		// .getContent()), pageable, chats.getTotalElements());

		return RespBody.succeed(chats);
	}

	@RequestMapping(value = { "searchBy",
			"searchBy/unmask" }, method = RequestMethod.GET)
	@ResponseBody
	public RespBody searchBy(@RequestParam(value = "id") Long id,
			@PageableDefault(sort = {
					"createDate" }, direction = Direction.DESC) Pageable pageable) {

		long cntGtId = chatRepository.countGtId(id);
		int size = pageable.getPageSize();
		long page = cntGtId / size;
		if (cntGtId % size == 0) {
			page--;
		}

		pageable = new PageRequest(page > -1 ? (int) page : 0, size,
				Direction.DESC, "createDate");

		Page<Chat> chats = chatRepository.findAll(pageable);
		chats = new PageImpl<Chat>(
				CollectionUtil.reverseList(chats.getContent()), pageable,
				chats.getTotalElements());

		return RespBody.succeed(chats);
	}

	private boolean isVoterExists(String voters) {
		boolean isExits = false;
		if (voters != null) {
			String loginUsername = WebUtil.getUsername();
			String[] voterArr = voters.split(",");

			for (String voter : voterArr) {
				if (voter.equals(loginUsername)) {
					isExits = true;
					break;
				}
			}
		}

		return isExits;
	}

	@RequestMapping(value = { "vote",
			"vote/unmask" }, method = RequestMethod.POST)
	@ResponseBody
	public RespBody vote(@RequestParam("id") Long id,
			@RequestParam("baseURL") String baseURL,
			@RequestParam("contentHtml") String contentHtml,
			@RequestParam(value = "type", required = false) String type) {

		Chat chat = chatRepository.findOne(id);
		if (chat == null) {
			return RespBody.failed("投票聊天内容不存在!");
		}
		String loginUsername = WebUtil.getUsername();

		Chat chat2 = null;

		String title = "";
		final User loginUser = getLoginUser();

		if (VoteType.Zan.name().equalsIgnoreCase(type)) {
			String voteZan = chat.getVoteZan();
			if (isVoterExists(voteZan)) {
				return RespBody.failed("您已经投票[赞]过！");
			} else {
				chat.setVoteZan(voteZan == null ? loginUsername
						: voteZan + ',' + loginUsername);

				chat2 = chatRepository.saveAndFlush(chat);
				title = loginUser.getName() + "[" + loginUsername
						+ "]赞了你的沟通消息!";
			}

		} else {
			String voteCai = chat.getVoteCai();
			if (isVoterExists(voteCai)) {
				return RespBody.failed("您已经投票[踩]过！");
			} else {
				chat.setVoteCai(voteCai == null ? loginUsername
						: voteCai + ',' + loginUsername);
				chat2 = chatRepository.saveAndFlush(chat);
				title = loginUser.getName() + "[" + loginUsername
						+ "]踩了你的沟通消息!";
			}
		}

		final String href = baseURL + dynamicAction + "?id=" + id;
		final String titleHtml = title;
		final String mailTo = chat.getCreator().getMails();
		final String html = "<h3>投票沟通消息内容:</h3><hr/>" + contentHtml;

		ThreadUtil.exec(() -> {

			try {
				Thread.sleep(3000);
				mailSender.sendHtml(
						String.format("TMS-沟通动态投票@消息_%s",
								DateUtil.format(new Date(), DateUtil.FORMAT7)),
						TemplateUtil.process("templates/mail/mail-dynamic",
								MapUtil.objArr2Map("user", loginUser, "date",
										new Date(), "href", href, "title",
										titleHtml, "content", html)),
						mailTo);
				logger.info("沟通消息投票邮件发送成功！");
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("沟通消息投票邮件发送失败！");
			}

		});

		log(Action.Vote, Target.Chat, chat.getId(), chat2);

		return RespBody.succeed(chat2);
	}
}
