package it.polito.gruppo2.mamk.lab3.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class ServerSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder userPasswordEncoder;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // This should point to where users are stored (Which bean finds the users)
        auth.userDetailsService(userDetailsService).passwordEncoder(userPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        AuthenticationEntryPoint entryPoint = new CustomAuthenticationEntryPoint();
        http.cors()
                .and().authorizeRequests().anyRequest().authenticated()
                .and().httpBasic().authenticationEntryPoint(entryPoint)
                .and().exceptionHandling().authenticationEntryPoint(entryPoint);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**")
                .and().ignoring().antMatchers(HttpMethod.DELETE, "/oauth/**")
                .and().ignoring().antMatchers(HttpMethod.GET, "/*")
                .and().ignoring().antMatchers(HttpMethod.POST, "/api/user/registration")
                .and().ignoring().antMatchers(HttpMethod.GET, "/api/user/checkExistence")
                .and().ignoring().regexMatchers(HttpMethod.GET, "^(?!/oauth/|/api/|/prova/).*");
    }
}
