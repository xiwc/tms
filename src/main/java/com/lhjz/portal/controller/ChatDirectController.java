/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.lhjz.portal.util.StringUtil;

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

	String dynamicAction = "admin/dynamic";

	@RequestMapping(value = "create", method = RequestMethod.POST)
	@ResponseBody
	public RespBody create(
			@RequestParam("baseURL") String baseURL,
			@RequestParam(value = "chatTo", required = false) String chatTo,
			@RequestParam("content") String content,
			@RequestParam(value = "preMore", defaultValue = "true") Boolean preMore,
			@RequestParam("lastId") Long lastId,
			@RequestParam("contentHtml") String contentHtml) {

		if (StringUtil.isEmpty(content)) {
			return RespBody.failed("提交内容不能为空!");
		}
		
		User chatToUser = userRepository.findOne(chatTo);
		
		if (chatToUser == null) {
			return RespBody.failed("聊天对象不存在!");
		}

		ChatDirect chatDirect = new ChatDirect();
		chatDirect.setChatTo(chatToUser);
		chatDirect.setContent(content);

		chatDirectRepository.saveAndFlush(chatDirect);

		// final User loginUser = getLoginUser();
		//
		// ThreadUtil.exec(() -> {
		//
		// try {
		// Thread.sleep(3000);
		// mailSender.sendHtml(String.format("TMS-沟通动态@消息_%s",
		// DateUtil.format(new Date(), DateUtil.FORMAT7)),
		// TemplateUtil.process("templates/mail/mail-dynamic",
		// MapUtil.objArr2Map("user", loginUser,
		// "date", new Date(), "href", href,
		// "title", "下面的沟通消息中有@到你", "content",
		// html)), mail.get());
		// logger.info("沟通邮件发送成功！");
		// } catch (Exception e) {
		// e.printStackTrace();
		// logger.error("沟通邮件发送失败！");
		// }
		//
		// });
		//
		// if (!preMore && chatRepository.countQueryRecent(lastId) <= 50) {
		// List<Chat> chats = chatRepository.queryRecent(lastId);
		// return RespBody.succeed(chats);
		// }

		return RespBody.succeed();
	}

}
