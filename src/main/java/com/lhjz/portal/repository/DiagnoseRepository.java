/**
 * 立衡脊柱版权所有 (lhjz)
 */
package com.lhjz.portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lhjz.portal.entity.Diagnose;
import com.lhjz.portal.pojo.Enum.Status;

/**
 * 
 * @author xi
 * 
 * @date 2015年3月28日 下午2:09:06
 * 
 */
public interface DiagnoseRepository extends JpaRepository<Diagnose, Long> {

	List<Diagnose> findByMailAndDescription(String mail, String description);

	List<Diagnose> findByPhoneAndDescription(String phone, String description);

	List<Diagnose> findByStatus(Status status);

}
