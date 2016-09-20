/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.lhjz.portal.entity.ChatDirect;
import com.lhjz.portal.entity.security.User;
import com.lhjz.portal.model.RespBody;
import com.lhjz.portal.repository.ChatAtRepository;
import com.lhjz.portal.repository.ChatDirectRepository;
import com.lhjz.portal.repository.ChatStowRepository;
import com.lhjz.portal.repository.GroupMemberRepository;
import com.lhjz.portal.repository.GroupRepository;
import com.lhjz.portal.repository.LogRepository;
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
@RequestMapping("admin/chat/direct")
public class ChatDirectController extends BaseController {

	static Logger logger = LoggerFactory.getLogger(ChatDirectController.class);

	@Autowired
	ChatDirectRepository chatDirectRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	GroupMemberRepository groupMemberRepository;

	@Autowired
	LogRepository logRepository;

	@Autowired
	ChatAtRepository chatAtRepository;

	@Autowired
	ChatStowRepository chatStowRepository;

	@Autowired
	MailSender2 mailSender;

	String hash = "#/chat-direct";

	@RequestMapping(value = "create", method = RequestMethod.POST)
	@ResponseBody
	public RespBody create(@RequestParam("baseUrl") String baseUrl,
			@RequestParam("path") String path,
			@RequestParam("chatTo") String chatTo,
			@RequestParam("content") String content,
			@RequestParam("contentHtml") final String contentHtml) {

		if (StringUtil.isEmpty(content)) {
			return RespBody.failed("提交内容不能为空!");
		}

		final User chatToUser = userRepository.findOne(chatTo);

		if (chatToUser == null) {
			return RespBody.failed("聊天对象不存在!");
		}

		ChatDirect chatDirect = new ChatDirect();
		chatDirect.setChatTo(chatToUser);
		chatDirect.setContent(content);

		ChatDirect chatDirect2 = chatDirectRepository.saveAndFlush(chatDirect);

		final User loginUser = getLoginUser();
		final String href = baseUrl + path + hash + "?id="
				+ chatDirect2.getId();

		ThreadUtil.exec(() -> {

			try {
				Thread.sleep(3000);
				mailSender.sendHtml(String.format("TMS-私聊@消息_%s",
						DateUtil.format(new Date(), DateUtil.FORMAT7)),
						TemplateUtil
								.process("templates/mail/mail-dynamic", MapUtil
										.objArr2Map("user", loginUser, "date",
												new Date(), "href", href,
												"title", "下面的沟通消息中有@到你",
												"content", contentHtml)),
						chatToUser.getMails());
				logger.info("私聊邮件发送成功！");
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("私聊邮件发送失败！");
			}

		});

		return RespBody.succeed();
	}

	@RequestMapping(value = "list", method = RequestMethod.GET)
	@ResponseBody
	public RespBody list(
			@PageableDefault(sort = { "createDate" }, direction = Direction.DESC) Pageable pageable,
			@RequestParam("chatTo") String chatTo) {

		User chatToUser = userRepository.findOne(chatTo);

		if (chatToUser == null) {
			return RespBody.failed("聊天对象不存在!");
		}

		Page<ChatDirect> page = chatDirectRepository.findByChatTo(chatToUser,
				pageable);

		return RespBody.succeed(page);
	}

	@RequestMapping(value = "latest", method = RequestMethod.GET)
	@ResponseBody
	public RespBody latest(@RequestParam("id") Long id,
			@RequestParam("chatTo") String chatTo) {

		User chatToUser = userRepository.findOne(chatTo);

		if (chatToUser == null) {
			return RespBody.failed("聊天对象不存在!");
		}

		long cnt = chatDirectRepository.countByChatToAndIdGreaterThan(
				chatToUser, id);
		List<ChatDirect> chats = new ArrayList<ChatDirect>();
		if (cnt <= 10) {
			chats = chatDirectRepository.findByChatToAndIdGreaterThan(
					chatToUser, id);
		}

		return RespBody.succeed(chats);
	}

}
