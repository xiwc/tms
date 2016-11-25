package com.lhjz.portal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
public class OAuth2ServerConfig {

	private static final String RESOURCE_ID_TMS_USER = "tms_user";
	private static final String RESOURCE_ID_TMS_ADMIN = "tms_admin";

	// @Configuration
	// @EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

		// @Autowired
		// private AccessDecisionManager oauth2AccessDecisionManager;

		@Override
		public void configure(ResourceServerSecurityConfigurer resources) {
			resources.resourceId(RESOURCE_ID_TMS_USER).stateless(false);
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {

			// @formatter:off
            http.sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .and()
                    .requestMatchers()
                    .antMatchers("/admin/**")
                    .and()
                    .authorizeRequests()
                    .antMatchers("/admin/**")
                    .access("#oauth2.hasScope('read') or (!#oauth2.isOAuth() and hasRole('ROLE_USER'))")
//                    .accessDecisionManager(oauth2AccessDecisionManager)
                    .and().csrf().disable();
         // @formatter:on

		}

	}

	@Configuration
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

		@Autowired
		private TokenStore tokenStore;

		@Autowired
		private UserApprovalHandler userApprovalHandler;

		@Autowired
		private AuthorizationCodeServices authorizationCodeServices;

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			// @formatter:off
			clients.inMemory()
				.withClient("user")
				.resourceIds(RESOURCE_ID_TMS_USER)
				.authorizedGrantTypes("authorization_code", "refresh_token", "implicit")
				.authorities("ROLE_USER")
				.scopes("read")
				.secret("user")
				.and()
				.withClient("admin")
				.resourceIds(RESOURCE_ID_TMS_ADMIN)
				.authorizedGrantTypes("password", "refresh_token")
				.authorities("ROLE_ADMIN")
				.scopes("read")
				.secret("admin");
			// @formatter:on
		}

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			// @formatter:off
			endpoints.tokenStore(tokenStore)
				.userApprovalHandler(userApprovalHandler)
				.authorizationCodeServices(authorizationCodeServices);
			// @formatter:on
		}

		@Override
		public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		}

	}

}
