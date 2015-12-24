package stink5.oauth2.lab2.security;

import static java.util.Objects.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDetailsService userDetailsService;
    private TokenAuthenticationService tokenAuthenticationService;

    @Autowired
    public void setUserDetailsService(final UserDetailsService userDetailsService) {
        this.userDetailsService = requireNonNull(userDetailsService);
    }

    @Autowired
    public void setTokenAuthenticationService(
        final TokenAuthenticationService tokenAuthenticationService
    ) {
        this.tokenAuthenticationService = requireNonNull(tokenAuthenticationService);
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.csrf().disable()
            .addFilterBefore(
                new OAuthLoginFilter(
                    "/api/**",
                    this.tokenAuthenticationService,
                    authenticationManager()
                ),
                UsernamePasswordAuthenticationFilter.class
            )
            .addFilterBefore(
                new OAuthAuthenticationFilter(this.tokenAuthenticationService),
                UsernamePasswordAuthenticationFilter.class
            )
            .authorizeRequests()
                .antMatchers("/login", "/authorize").permitAll()
                .antMatchers(HttpMethod.POST, "/oauth/token").permitAll()
                .anyRequest().authenticated()
            .and()
                .formLogin()
                    // указываем страницу с формой логина
                    .loginPage("/login")
                    // указываем action с формы логина
                    .loginProcessingUrl("/authorize")
                    // указываем URL при неудачном логине
                    .failureUrl("/login?error")
                    .defaultSuccessUrl("/user")
                    // Указываем параметры логина и пароля с формы логина
                    .usernameParameter("username")
                    .passwordParameter("password")
                    // даем доступ к форме логина всем
                    .permitAll()
            .and()
                .logout()
                    // разрешаем делать логаут всем
                    .permitAll()
                    // указываем URL логаута
                    .logoutUrl("/logout")
                    // указываем URL при удачном логауте
                    .logoutSuccessUrl("/login?logout")
                    // делаем не валидной текущую сессию
                    .invalidateHttpSession(true);
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsService)
            .passwordEncoder(NoOpPasswordEncoder.getInstance());
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return this.userDetailsService;
    }

}
