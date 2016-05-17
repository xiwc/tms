/**
 * 立衡脊柱版权所有 (lhjz)
 */
package com.lhjz.portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lhjz.portal.entity.Project;
import com.lhjz.portal.entity.Translate;

/**
 * 
 * @author xi
 * 
 * @date 2015年3月28日 下午2:09:06
 * 
 */
public interface TranslateRepository extends JpaRepository<Translate, Long> {
	
	List<Translate> findByKeyAndProject(String key, Project project);

	List<Translate> findByProject(Project project);
}
