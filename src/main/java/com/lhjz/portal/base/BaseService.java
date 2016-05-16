/**
 * 立衡脊柱版权所有 (lhjz)
 */
package com.lhjz.portal.base;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 
 * @author weichx
 * 
 * @date Apr 2, 2015 2:42:31 PM
 * 
 */
public abstract class BaseService {

	static Logger logger = LoggerFactory.getLogger(BaseService.class);

	@Autowired
	protected MessageSource messageSource;

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	@PersistenceContext
	protected EntityManager entityManager;
}
