/**
 * 立衡脊柱版权所有 (lhjz)
 */
package com.lhjz.portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lhjz.portal.entity.Settings;
import com.lhjz.portal.pojo.Enum.Module;
import com.lhjz.portal.pojo.Enum.Page;

/**
 * 
 * @author xi
 * 
 * @date 2015年3月28日 下午2:09:06
 * 
 */
public interface SettingsRepository extends JpaRepository<Settings, Long> {

	List<Settings> findByPage(Page page);

	List<Settings> findByPageAndModule(Page page, Module module);

}
