/**
 * 立衡脊柱版权所有 (lhjz)
 */
package com.lhjz.portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lhjz.portal.entity.Article;

/**
 * 
 * @author xi
 * 
 * @date 2015年3月28日 下午2:09:06
 * 
 */
public interface ArticleRepository extends JpaRepository<Article, Long> {

	List<Article> findByContent(String content);

}
