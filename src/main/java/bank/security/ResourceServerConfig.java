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
    public CustomJwtAuthenticationConverter customJwtAuthenticationTokenConverter() {
        return new CustomJwtAuthenticationConverter();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .oauth2ResourceServer()
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
                .requestMatchers(HttpMethod.GET, "/customer").hasAuthority("SCOPE_view_all_customers")
                .requestMatchers(HttpMethod.GET, "/customer/{cpf}").hasAuthority("SCOPE_view_customer")
                .requestMatchers(HttpMethod.POST, "/customer").hasAuthority("SCOPE_register_customer")
                .requestMatchers(HttpMethod.PUT, "/customer/update").hasAuthority("SCOPE_update_customer")
                .requestMatchers(HttpMethod.DELETE, "/customer/{cpf}").hasAuthority("SCOPE_delete_customer")
                //Accounts
                .requestMatchers(HttpMethod.GET, "/accounts").hasAuthority("SCOPE_view_all_accounts")
                .requestMatchers(HttpMethod.GET, "/accounts/{id}").hasAuthority("SCOPE_view_account")
                .requestMatchers(HttpMethod.POST, "/accounts/{cpf}").hasAuthority("SCOPE_register_account")
                .requestMatchers(HttpMethod.DELETE, "/accounts/{id}").hasAuthority("SCOPE_delete_account")
                //Transaction
                .requestMatchers(HttpMethod.GET, "/transaction").hasAuthority("SCOPE_view_transactions")
                .requestMatchers(HttpMethod.GET, "/transaction/customer/{id}").hasAuthority("SCOPE_view_transactions_by_customer")
                .requestMatchers(HttpMethod.POST, "/transaction/deposit").hasAuthority("SCOPE_deposit_money")
                .requestMatchers(HttpMethod.POST, "/transaction/withdraw/{accountNumber}").hasAuthority("SCOPE_withdraw_money")
                .requestMatchers(HttpMethod.POST, "/transaction/transfer").hasAuthority("SCOPE_transfer_money")
                .requestMatchers(HttpMethod.GET, "/transaction/{transactionId}").hasAuthority("SCOPE_view_transaction")
                //Terminou
                .anyRequest()
                .authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(customJwtAuthenticationTokenConverter()); // atencao: necessario pois sobrescrevemos a conf default do Spring Security

        return http.build();
    }
}