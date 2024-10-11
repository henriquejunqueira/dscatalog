package com.devsuperior.dscatalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

	// Configuração de segurança
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/h2-console/**").permitAll() // Permitir todas as requisições para o H2 Console
                .requestMatchers("/users/**").permitAll() // Permitir todas as requisições para /users
                .requestMatchers("/products/**").permitAll() // Permitir todas as requisições para /products
                .anyRequest().authenticated() // Exigir autenticação para outras rotas
            )
            .csrf().disable() // Desativar CSRF (requerido para o H2 Console funcionar)
            .headers().frameOptions().disable(); // Permitir uso de frames para o H2 Console

        return http.build();
    }
	
}
