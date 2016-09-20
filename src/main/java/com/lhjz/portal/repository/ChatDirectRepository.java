/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

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

	Page<ChatDirect> findByChatTo(User chatTo, Pageable pageable);

	List<ChatDirect> findByChatToAndIdGreaterThan(User chatTo, long id);

	Long countByChatToAndIdGreaterThan(User chatTo, long id);
}
