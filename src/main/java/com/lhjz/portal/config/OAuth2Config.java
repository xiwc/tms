package com.lhjz.portal.config;

import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.vote.ScopeVoter;

@Configuration
public class OAuth2Config extends WebSecurityConfigurerAdapter {

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**");
		web.expressionHandler(new OAuth2WebSecurityExpressionHandler());
	}

	/**
	 * OAuth2 Configuration start
	 */
	@Bean(name = "clientDetailsService")
	public ClientDetailsService clientDetailsService(DataSource dataSource) {
		return new JdbcClientDetailsService(dataSource);
	}

	@Bean(name = "tokenStore")
	public TokenStore tokenStore(DataSource dataSource) {
		return new JdbcTokenStore(dataSource);
	}

	@Bean(name = "approvalStore")
	public ApprovalStore approvalStore(DataSource dataSource) {
		return new JdbcApprovalStore(dataSource);
	}

	// @Bean(name = "tokenServices")
	// public DefaultTokenServices tokenServices(TokenStore tokenStore,
	// ClientDetailsService clientDetailsService) {
	// final DefaultTokenServices tokenServices = new DefaultTokenServices();
	// tokenServices.setTokenStore(tokenStore);
	// tokenServices.setClientDetailsService(clientDetailsService);
	// tokenServices.setSupportRefreshToken(true);
	// return tokenServices;
	// }

	@Bean(name = "oAuth2RequestFactory")
	public OAuth2RequestFactory oAuth2RequestFactory(ClientDetailsService clientDetailsService) {
		return new DefaultOAuth2RequestFactory(clientDetailsService);
	}

	@Bean(name = "oauthUserApprovalHandler")
	public UserApprovalHandler oauthUserApprovalHandler(ApprovalStore approvalStore,
			ClientDetailsService clientDetailsService, OAuth2RequestFactory oAuth2RequestFactory) {
		ApprovalStoreUserApprovalHandler userApprovalHandler = new ApprovalStoreUserApprovalHandler();
		userApprovalHandler.setApprovalStore(approvalStore);
		userApprovalHandler.setClientDetailsService(clientDetailsService);
		userApprovalHandler.setRequestFactory(oAuth2RequestFactory);
		return userApprovalHandler;
	}

	@Bean(name = "jdbcAuthorizationCodeServices")
	public AuthorizationCodeServices jdbcAuthorizationCodeServices(DataSource dataSource) {
		return new JdbcAuthorizationCodeServices(dataSource);
	}

	@Bean(name = "oauth2AuthenticationEntryPoint")
	public OAuth2AuthenticationEntryPoint oauth2AuthenticationEntryPoint() {
		return new OAuth2AuthenticationEntryPoint();
	}

	@Bean(name = "oauth2ClientDetailsUserService")
	public ClientDetailsUserDetailsService oauth2ClientDetailsUserService(ClientDetailsService clientDetailsService) {
		return new ClientDetailsUserDetailsService(clientDetailsService);
	}

	// @Bean(name = "oauth2AuthenticationManager")
	// public AuthenticationManager
	// oauth2AuthenticationManager(ClientDetailsUserDetailsService
	// detailsService) {
	// DaoAuthenticationProvider daoAuthenticationProvider = new
	// DaoAuthenticationProvider();
	// daoAuthenticationProvider.setUserDetailsService(detailsService);
	// List<AuthenticationProvider> providers =
	// Arrays.asList(daoAuthenticationProvider);
	// return new ProviderManager(providers);
	// }

	@Bean(name = "oauth2AccessDecisionManager")
	public UnanimousBased oauth2AccessDecisionManager() {
		return new UnanimousBased(Arrays.asList(new ScopeVoter(), new RoleVoter(), new AuthenticatedVoter()));
	}

	@Bean(name = "oauth2AccessDeniedHandler")
	public OAuth2AccessDeniedHandler oauth2AccessDeniedHandler() {
		return new OAuth2AccessDeniedHandler();
	}

	// @Bean(name = "clientCredentialsTokenEndpointFilter")
	// public ClientCredentialsTokenEndpointFilter
	// clientCredentialsTokenEndpointFilter(AuthenticationManager
	// oauth2AuthenticationManager) {
	// ClientCredentialsTokenEndpointFilter clientCredentialsTokenEndpointFilter
	// = new ClientCredentialsTokenEndpointFilter();
	// clientCredentialsTokenEndpointFilter.setAuthenticationManager(oauth2AuthenticationManager);
	// return clientCredentialsTokenEndpointFilter;
	// }

}
