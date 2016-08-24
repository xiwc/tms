/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lhjz.portal.entity.Chat;

/**
 * 
 * @author xi
 * 
 * @date 2015年3月28日 下午2:09:06
 * 
 */
public interface ChatRepository extends JpaRepository<Chat, Long> {

	@Query(value = "SELECT COUNT(*) FROM chat WHERE id >= ?1", nativeQuery = true)
	long countGtId(Long id);

	@Query(value = "SELECT * FROM chat WHERE id > ?1 ORDER BY id ASC", nativeQuery = true)
	List<Chat> queryRecent(Long lastId);

	@Query(value = "SELECT COUNT(*) FROM chat WHERE id > ?1", nativeQuery = true)
	long countQueryRecent(Long lastId);

	@Query(value = "SELECT COUNT(*) FROM chat_at WHERE at_user = ?1 AND chat_id > ?2", nativeQuery = true)
	long countQueryRecentAt(String atUsername, Long lastId);

	Page<Chat> findByContentLike(String search, Pageable pageable);
}
