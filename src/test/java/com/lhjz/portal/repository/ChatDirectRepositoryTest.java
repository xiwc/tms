package com.lhjz.portal.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.lhjz.portal.Application;
import com.lhjz.portal.entity.ChatDirect;

@SpringApplicationConfiguration(classes = Application.class)
public class ChatDirectRepositoryTest extends AbstractTestNGSpringContextTests {

	@Autowired
	ChatDirectRepository chatDirectRepository;

	@Autowired
	UserRepository userRepository;

	@Test
	public void countAllNew() {
		System.out.println(chatDirectRepository.count());
	}

	@Test
	public void save() {
		ChatDirect cd = new ChatDirect();
		cd.setChatTo(userRepository.findOne("admin"));
		cd.setContent("test..........");

		chatDirectRepository.saveAndFlush(cd);

	}
}