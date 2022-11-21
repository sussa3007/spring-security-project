package com.suyoung.springsecurityproject.config;

import com.suyoung.springsecurityproject.filter.TesterAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
public class SpringSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // basic authentication
//        http.httpBasic(); // basic authentication filter 활성화
        http.httpBasic().disable(); // basic authentication filter 비활성화

        // csrf
        http.csrf();

        // remember-me
        http.rememberMe();

        //tester authentication filter
        http.apply(new CustomFilterConfig());
        // permit
        http.authorizeHttpRequests()
                .antMatchers("/","/images/**", "/css/**", "/home", "/signup","/login").permitAll()
                .antMatchers("/post").hasRole("USER")
                .antMatchers("/admin").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/notice").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/notice").hasRole("ADMIN")
                .anyRequest().authenticated();
        // login
        http.formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .permitAll(); // 모두 허용
        // logout
        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/");

        return http.build();
    }


    public class CustomFilterConfig extends AbstractHttpConfigurer<CustomFilterConfig, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            System.out.println("# Do filter");
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
            TesterAuthenticationFilter testerAuthenticationFilter = new TesterAuthenticationFilter(authenticationManager);
            testerAuthenticationFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login","POST"));
            builder.addFilter(testerAuthenticationFilter);
        }
    }


}
