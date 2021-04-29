package com.example.cursosonlinesb.config;

import com.example.cursosonlinesb.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin()
                .loginPage("/login")
                .permitAll()

                .and()
                .authorizeRequests()
                .antMatchers("/admin/**")
                .hasAnyRole(Usuario.Rol.ADMIN.name())
                .antMatchers("/cursos/*", "/inscribir", "/mis-cursos")
                .authenticated()

                .anyRequest()
                .permitAll()
                .and()

                .rememberMe().key("rememberKey").tokenValiditySeconds(3600)
                .userDetailsService(userDetailsService)

                .and()

                .exceptionHandling().accessDeniedPage("/403")

                .and()

                .logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout", HttpMethod.GET.name())));
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
