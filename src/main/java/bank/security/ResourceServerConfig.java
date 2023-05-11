package bank.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig {

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
                .authorizeHttpRequests()
                //Customer
                .requestMatchers(HttpMethod.GET, "/customer").hasAnyAuthority("SCOPE_admin")
                .requestMatchers(HttpMethod.GET, "/customer/{cpf}").hasAnyAuthority("SCOPE_admin")
                .requestMatchers(HttpMethod.POST, "/customer").hasAnyAuthority("SCOPE_admin")
                .requestMatchers(HttpMethod.PUT, "/customer/update").hasAnyAuthority("SCOPE_user")
                .requestMatchers(HttpMethod.DELETE, "/customer/{cpf}").hasAnyAuthority("SCOPE_admin")
                        //Accounts
                .requestMatchers(HttpMethod.GET, "/accounts").hasAnyAuthority("SCOPE_admin")
                .requestMatchers(HttpMethod.GET, "/accounts/{id}").hasAnyAuthority("SCOPE_admin")
                .requestMatchers(HttpMethod.POST, "/accounts/{cpf}").hasAnyAuthority("SCOPE_user")
                .requestMatchers(HttpMethod.DELETE, "/accounts/{id}").hasAnyAuthority("SCOPE_admin")
                        //Transaction
                .requestMatchers(HttpMethod.GET, "/transaction").hasAnyAuthority("SCOPE_admin")
                .requestMatchers(HttpMethod.GET, "/transaction/customer/{id}").hasAnyAuthority("SCOPE_admin")
                .requestMatchers(HttpMethod.POST, "/transaction/deposit").hasAnyAuthority("SCOPE_user")
                .requestMatchers(HttpMethod.POST, "/transaction/withdraw/{accountNumber}").hasAnyAuthority("SCOPE_user")
                .requestMatchers(HttpMethod.POST, "/transaction/transfer").hasAnyAuthority("SCOPE_user")
                .requestMatchers(HttpMethod.GET, "/transaction/{transactionId}").hasAnyAuthority("SCOPE_admin", "SCOPE_user")
                //Terminou
                .anyRequest()
                .authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt(); // atencao: necessario pois sobrescrevemos a conf default do Spring Security

        return http.build();
    }
}