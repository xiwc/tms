/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import com.lhjz.portal.model.RespBody;
import com.lhjz.portal.pojo.Enum.Action;
import com.lhjz.portal.pojo.Enum.Status;
import com.lhjz.portal.pojo.Enum.Target;
import com.lhjz.portal.repository.ChatRepository;
import com.lhjz.portal.repository.LogRepository;
import com.lhjz.portal.util.CollectionUtil;
import com.lhjz.portal.util.StringUtil;

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

	String translateAction = "admin/translate";

	@RequestMapping(value = "create", method = RequestMethod.POST)
	@ResponseBody
	public RespBody create(@RequestParam("content") String content) {

		if (StringUtil.isEmpty(content)) {
			return RespBody.failed("提交内容不能为空!");
		}

		Chat chat = new Chat();
		chat.setContent(content);
		chat.setCreateDate(new Date());
		chat.setCreator(getLoginUser());
		chat.setStatus(Status.New);

		Chat chat2 = chatRepository.saveAndFlush(chat);

		log(Action.Create, Target.Chat, chat2);

		return RespBody.succeed(chat2);
	}

	@RequestMapping(value = "update", method = RequestMethod.POST)
	@ResponseBody
	public RespBody update(@RequestParam("id") Long id,
			@RequestParam("content") String content) {

		if (StringUtil.isEmpty(content)) {
			return RespBody.failed("修改内容不能为空!");
		}

		Chat chat = chatRepository.findOne(id);
		chat.setContent(content);
		chat.setUpdateDate(new Date());
		chat.setUpdater(getLoginUser());
		chat.setStatus(Status.Updated);

		Chat chat2 = chatRepository.saveAndFlush(chat);

		log(Action.Update, Target.Chat, chat2);

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

		log(Action.Delete, Target.Chat, chat);

		return RespBody.succeed(id);
	}

	@RequestMapping(value = "get", method = RequestMethod.GET)
	@ResponseBody
	public RespBody get(@RequestParam("id") Long id) {

		Chat chat = chatRepository.findOne(id);
		if (chat == null) {
			return RespBody.failed("获取聊天内容不存在!");
		}

		return RespBody.succeed(chat);
	}

	@RequestMapping(value = "more", method = RequestMethod.GET)
	@ResponseBody
	public RespBody more(
			@PageableDefault(sort = { "createDate" }, direction = Direction.DESC) Pageable pageable) {

		Page<Chat> chats = chatRepository.findAll(pageable);
		chats = new PageImpl<Chat>(CollectionUtil.reverseList(chats
				.getContent()), pageable, chats.getTotalElements());

		return RespBody.succeed(chats);
	}

	@RequestMapping(value = "moreLogs", method = RequestMethod.GET)
	@ResponseBody
	public RespBody moreLogs(
			@PageableDefault(sort = { "createDate" }, direction = Direction.DESC) Pageable pageable) {

		Page<Log> logs = logRepository.findByTarget(Target.Translate, pageable);

		return RespBody.succeed(logs);
	}
}
