/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.config;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Component;

import com.lhjz.portal.entity.security.User;
import com.lhjz.portal.repository.UserRepository;

/**
 * 
 * @author xi
 * 
 * @date 2015年3月30日 下午9:42:00
 * 
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig {

	@Autowired
	DataSource dataSource;

	@Bean
	BCryptPasswordEncoder bCryptPasswordEncoderBean() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth)
			throws Exception {

		auth.jdbcAuthentication().dataSource(dataSource)
				.passwordEncoder(bCryptPasswordEncoderBean());
	}

	@Configuration
	@Order(1)
	@Profile({ "dev", "prod" })
	public static class SecurityConfiguration extends
			WebSecurityConfigurerAdapter {

		@Autowired
		DataSource dataSource;

		@Autowired
		LoginSuccessHandler loginSuccessHandler;

		@Bean
		public PersistentTokenRepository persistentTokenRepository() {
			JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
			db.setDataSource(dataSource);
			return db;
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {

			http.antMatcher("/admin/**")
					.authorizeRequests()
					.antMatchers("/admin/file/download/**")
					.permitAll()
					.antMatchers("/admin/css/**", "/admin/img/**",
							"/admin/js/**", "/admin/login").permitAll()
					.anyRequest().authenticated().and().formLogin()
					.loginPage("/admin/login").permitAll()
					.loginProcessingUrl("/admin/signin")
					.successHandler(loginSuccessHandler).and().logout()
					.logoutUrl("/admin/logout").permitAll()
					.logoutSuccessUrl("/admin/login?logout").and().rememberMe()
					.tokenRepository(persistentTokenRepository())
					.tokenValiditySeconds(1209600); // 2 weeks(14d)

		}

	}

	@Configuration
	@Order(1)
	@Profile("test")
	public static class SecurityConfigurationTest extends
			WebSecurityConfigurerAdapter {

		@Autowired
		LoginSuccessHandler loginSuccessHandler;

		@Override
		protected void configure(HttpSecurity http) throws Exception {

			http.antMatcher("/admin/**")
					.authorizeRequests()
					.antMatchers("/admin/file/download/**")
					.permitAll()
					.antMatchers("/admin/css/**", "/admin/img/**",
							"/admin/js/**").permitAll().anyRequest()
					.authenticated().and().formLogin()
					.loginPage("/admin/login").permitAll()
					.loginProcessingUrl("/admin/signin")
					.successHandler(loginSuccessHandler).and().logout()
					.logoutUrl("/admin/logout")
					.logoutSuccessUrl("/admin/login").and().csrf().disable();

		}

	}

	@Component("loginSuccessHandler")
	public static class LoginSuccessHandler extends
			SavedRequestAwareAuthenticationSuccessHandler {

		@Autowired
		UserRepository userRepository;

		@Override
		public void onAuthenticationSuccess(HttpServletRequest request,
				HttpServletResponse response, Authentication authentication)
				throws IOException, ServletException {

			UserDetails uds = (UserDetails) authentication.getPrincipal();
			// WebAuthenticationDetails wauth = (WebAuthenticationDetails)
			// authentication
			// .getDetails();

			User loginUser = userRepository.findOne(uds.getUsername());
			if (loginUser != null) {
				loginUser.setLastLoginDate(new Date());
				// loginUser.setLoginRemoteAddress(wauth.getRemoteAddress());
				loginUser.setLoginRemoteAddress(
						LoginSuccessHandler.getIpAddr(request));

				long loginCount = loginUser.getLoginCount();
				loginUser.setLoginCount(++loginCount);

				userRepository.saveAndFlush(loginUser);
			}

			this.setDefaultTargetUrl("/admin");
			this.setAlwaysUseDefaultTargetUrl(false);

			super.onAuthenticationSuccess(request, response, authentication);

		}

		public static final String getIpAddr(final HttpServletRequest request) {

			String ipString = request.getHeader("x-forwarded-for");
			if (StringUtils.isBlank(ipString)
					|| "unknown".equalsIgnoreCase(ipString)) {
				ipString = request.getHeader("Proxy-Client-IP");
			}
			if (StringUtils.isBlank(ipString)
					|| "unknown".equalsIgnoreCase(ipString)) {
				ipString = request.getHeader("WL-Proxy-Client-IP");
			}
			if (StringUtils.isBlank(ipString)
					|| "unknown".equalsIgnoreCase(ipString)) {
				ipString = request.getRemoteAddr();
			}

			// 多个路由时，取第一个非unknown的ip
			final String[] arr = ipString.split(",");
			for (final String str : arr) {
				if (!"unknown".equalsIgnoreCase(str)) {
					ipString = str;
					break;
				}
			}

			return ipString;
		}

	}

}