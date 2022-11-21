package com.suyoung.springsecurityproject.config;

import com.suyoung.springsecurityproject.filter.TesterAuthenticationFilter;
import com.suyoung.springsecurityproject.filter.jwt.JwtAuthenticationFilter;
import com.suyoung.springsecurityproject.filter.jwt.JwtAuthorizationFilter;
import com.suyoung.springsecurityproject.filter.jwt.JwtProperties;
import com.suyoung.springsecurityproject.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfigV1 {

    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // basic authentication
//        http.httpBasic(); // basic authentication filter 활성화
        http.httpBasic().disable(); // basic authentication filter 비활성화

        // csrf
        http.csrf().disable();

        //frameOptions
        http.headers().frameOptions().sameOrigin();

        //cors
        http.cors(Customizer.withDefaults());

        //stateless
//        http.sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // remember-me
        http.rememberMe();
        //tester authentication filter
//        http.apply(new CustomFilterConfig());

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
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies(JwtProperties.COOKIE_NAME);



        return http.build();
    }


    public class CustomFilterConfig extends AbstractHttpConfigurer<CustomFilterConfig, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {
//            System.out.println("# Do filter");
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
//            TesterAuthenticationFilter testerAuthenticationFilter = new TesterAuthenticationFilter(authenticationManager);
//            testerAuthenticationFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login","POST"));
//            builder.addFilter(testerAuthenticationFilter);

            System.out.println("# Do Jwt filter");
            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager);
            JwtAuthorizationFilter jwtAuthorizationFilter = new JwtAuthorizationFilter(userRepository);
            jwtAuthenticationFilter.setFilterProcessesUrl("/login");

            builder
                    .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(jwtAuthorizationFilter, BasicAuthenticationFilter.class);

        }
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PATCH", "DELETE"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
