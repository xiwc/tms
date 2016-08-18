/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lhjz.portal.entity.Chat;
import com.lhjz.portal.entity.ChatStow;
import com.lhjz.portal.entity.security.User;
import com.lhjz.portal.pojo.Enum.Status;

/**
 * 
 * @author xi
 * 
 * @date 2015年3月28日 下午2:09:06
 * 
 */
public interface ChatStowRepository extends JpaRepository<ChatStow, Long> {

	List<ChatStow> findByStowUserAndStatus(User user, Status status);

	List<ChatStow> findByChat(Chat chat);

	ChatStow findOneByChatAndStowUser(Chat chat, User stowUser);

}