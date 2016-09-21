/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lhjz.portal.entity.ChatDirect;
import com.lhjz.portal.entity.security.User;

/**
 * 
 * @author xi
 * 
 * @date 2015年3月28日 下午2:09:06
 * 
 */
public interface ChatDirectRepository extends JpaRepository<ChatDirect, Long> {

	@Query(value = "SELECT COUNT(*) FROM chat_direct WHERE ((creator = ?1 AND chat_to = ?2) OR (creator = ?2 AND chat_to = ?1)) AND id >= ?3", nativeQuery = true)
	long countGtId(User chatFrom, User chatTo, long id);

	@Query(value = "SELECT COUNT(*) FROM chat_direct WHERE (creator = ?1 AND chat_to = ?2) OR (creator = ?2 AND chat_to = ?1)", nativeQuery = true)
	long countChatDirect(User chatFrom, User chatTo);

	@Query(value = "SELECT * FROM chat_direct WHERE ((creator = ?1 AND chat_to = ?2) OR (creator = ?2 AND chat_to = ?1)) AND id > ?3", nativeQuery = true)
	List<ChatDirect> queryChatDirectAndIdGreaterThan(User chatfrom, User chatTo,
			long id);

	@Query(value = "SELECT * FROM chat_direct WHERE (creator = ?1 AND chat_to = ?2) OR (creator = ?2 AND chat_to = ?1) ORDER BY create_date DESC LIMIT ?3,?4", nativeQuery = true)
	List<ChatDirect> queryChatDirect(User chatfrom, User chatTo, long start,
			int limit);

	@Query(value = "SELECT * FROM chat_direct WHERE ((creator = ?1 AND chat_to = ?2) OR (creator = ?2 AND chat_to = ?1)) AND id < ?3 ORDER BY create_date DESC LIMIT ?4", nativeQuery = true)
	List<ChatDirect> queryMoreOld(User chatFrom, User chatTo, Long startId,
			int limit);

	@Query(value = "SELECT * FROM chat_direct WHERE ((creator = ?1 AND chat_to = ?2) OR (creator = ?2 AND chat_to = ?1)) AND id > ?3 ORDER BY create_date ASC LIMIT ?4", nativeQuery = true)
	List<ChatDirect> queryMoreNew(User chatFrom, User chatTo, Long startId,
			int limit);

	@Query(value = "SELECT COUNT(*) as cnt FROM chat_direct WHERE ((creator = ?1 AND chat_to = ?2) OR (creator = ?2 AND chat_to = ?1)) AND id < ?3", nativeQuery = true)
	long countAllOld(User chatFrom, User chatTo, Long startId);

	@Query(value = "SELECT COUNT(*) as cnt FROM chat_direct WHERE ((creator = ?1 AND chat_to = ?2) OR (creator = ?2 AND chat_to = ?1)) AND id > ?3", nativeQuery = true)
	long countAllNew(User chatFrom, User chatTo, Long startId);

}
