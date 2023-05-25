package bank.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, proxyTargetClass = true)
public class ResourceServerConfig {

    private CustomJwtAuthenticationConverter customJwtAuthenticationConverter;

    //Configuração para usar o @PreAuthorize
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .csrf().disable()
                .httpBasic().disable()
                .rememberMe().disable()
                .formLogin().disable()
                .logout().disable()
                .requestCache().disable()
                .headers().frameOptions().deny()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(STATELESS)
                .and()
                .authorizeHttpRequests() // Todos os endpoints agora são simplesmente autenticados
                .anyRequest()
                .authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(customJwtAuthenticationConverter); // conf default do Spring Security

        return http.build();
    }
}