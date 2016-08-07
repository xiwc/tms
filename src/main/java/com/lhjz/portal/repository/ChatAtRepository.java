/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lhjz.portal.entity.ChatAt;
import com.lhjz.portal.entity.security.User;
import com.lhjz.portal.pojo.Enum.Status;

/**
 * 
 * @author xi
 * 
 * @date 2015年3月28日 下午2:09:06
 * 
 */
public interface ChatAtRepository extends JpaRepository<ChatAt, Long> {

	Page<ChatAt> findByAtUserAndStatus(User user, Status status,
			Pageable pageable);

	@Query(value = "SELECT COUNT(*) FROM `chat_at` WHERE at_user = ?1 AND `status` = 'New';", nativeQuery = true)
	long countAtUserNew(String atUser);
}
