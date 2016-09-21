/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	@Query(value = "SELECT COUNT(*) FROM chat_direct WHERE id >= ?1 AND chat_to = ?2", nativeQuery = true)
	long countGtId(Long id, User chatTo);

	Page<ChatDirect> findByChatTo(User chatTo, Pageable pageable);

	List<ChatDirect> findByChatToAndIdGreaterThan(User chatTo, long id);

	Long countByChatToAndIdGreaterThan(User chatTo, long id);

	@Query(value = "SELECT * FROM chat_direct WHERE chat_to = ?1 AND id < ?2 ORDER BY create_date DESC LIMIT ?3", nativeQuery = true)
	List<ChatDirect> queryMoreOld(User chatTo, Long startId, int limit);

	@Query(value = "SELECT * FROM chat_direct WHERE chat_to = ?1 AND id > ?2 ORDER BY create_date ASC LIMIT ?3", nativeQuery = true)
	List<ChatDirect> queryMoreNew(User chatTo, Long startId, int limit);

	@Query(value = "SELECT COUNT(*) as cnt FROM chat_direct WHERE id < ?1 AND chat_to = ?2", nativeQuery = true)
	long countAllOld(Long startId, User chatTo);

	@Query(value = "SELECT COUNT(*) as cnt FROM chat_direct WHERE id > ?1 AND chat_to = ?2", nativeQuery = true)
	long countAllNew(Long startId, User chatTo);

}
