/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.lhjz.portal.entity.Log;
import com.lhjz.portal.pojo.Enum.Target;

/**
 * 
 * @author xi
 * 
 * @date 2015年3月28日 下午2:09:06
 * 
 */
public interface LogRepository extends JpaRepository<Log, Long> {

	Page<Log> findByTarget(Target target, Pageable pageable);
}
