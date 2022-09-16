package pl.edu.agh.ki.lab.to.yourflights.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Konfiguracja Spring Security niezbędna do korzystania z konsoli h2-console
 * do testowania wewnętrznej bazy danych H2
 */

@Configuration
@EnableWebSecurity
public class SecurityConfigurationH2 extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/h2/*").permitAll();
        http.csrf().disable();
        http.headers().frameOptions().disable();
    }
}