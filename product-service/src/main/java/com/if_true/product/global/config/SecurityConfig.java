package com.if_true.product.global.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	private final String gatewaySecret;

	public SecurityConfig(@Value("${msa.security.gateway-secret:local-dev-secret}") String gatewaySecret) {
		this.gatewaySecret = gatewaySecret;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
			.csrf(csrf -> csrf.disable())
			.addFilterBefore(gatewayHeaderAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/actuator/health", "/actuator/info").permitAll()
				.anyRequest().authenticated()
			)
			.build();
	}

	@Bean
	OncePerRequestFilter gatewayHeaderAuthenticationFilter() {
		return new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
				throws ServletException, IOException {
				String gatewaySecretHeader = request.getHeader("X-Gateway-Secret");
				String userId = request.getHeader("X-User-Id");
				String userRole = request.getHeader("X-User-Role");
				if (gatewaySecret.equals(gatewaySecretHeader) && userId != null && !userId.isBlank()) {
					try {
						UUID.fromString(userId);
					} catch (IllegalArgumentException exception) {
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						return;
					}
					String role = userRole == null || userRole.isBlank() ? "USER" : userRole;
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userId,
						null,
						List.of(new SimpleGrantedAuthority("ROLE_" + role))
					);
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
				filterChain.doFilter(request, response);
			}
		};
	}

	@Bean
	RequestInterceptor gatewayHeaderRequestInterceptor() {
		return template -> {
			if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) {
				return;
			}
			HttpServletRequest request = attributes.getRequest();
			copyHeader(request, template, "X-User-Id");
			copyHeader(request, template, "X-User-Role");
			copyHeader(request, template, "X-Gateway-Secret");
			copyHeader(request, template, HttpHeaders.AUTHORIZATION);
		};
	}

	private void copyHeader(HttpServletRequest request, RequestTemplate template, String headerName) {
		String value = request.getHeader(headerName);
		if (value != null && !value.isBlank()) {
			template.header(headerName, value);
		}
	}
}
