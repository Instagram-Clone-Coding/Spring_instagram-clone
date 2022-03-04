package cloneproject.Instagram.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import cloneproject.Instagram.util.JwtUtil;
import lombok.RequiredArgsConstructor;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

    private final JwtUtil jwtUtil;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource configurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Override
    public void configure(WebSecurity web) throws Exception{
        web.ignoring().antMatchers("/static/css/**", "/static/js/**", "*.ico"); // 나중에 수정

        // swagger
        web.ignoring().antMatchers(
                "/v2/api-docs", "/configuration/ui", "/swagger-resources",
                "/configuration/security", "/swagger-ui.html", "/webjars/**","/swagger/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        JwtFilter customFilter = new JwtFilter(jwtUtil);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler);

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.cors().configurationSource(configurationSource())
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/login", "/accounts", "/swagger-resources/**", "/swagger-ui/**").permitAll()

                //게시물
                .antMatchers("/posts", "/posts/**", "/comments/**", "/hashtags/**").hasAuthority("ROLE_USER")

                // 유저 포스트
                .antMatchers("/accounts/**/posts", "/accounts/**/posts/recent", "/accounts/**/posts/tagged", "/accounts/**/posts/tagged/recent").permitAll()
                .antMatchers("/accounts/**/posts/saved", "/accounts/**/posts/saved/recent").hasAuthority("ROLE_USER")
                
                // 유저 프로필 관련
                .antMatchers("/accounts/**").permitAll()
                .antMatchers("/accounts/**/mini", "/accounts/edit", "/accounts/image", "/menu/profile").hasAuthority("ROLE_USER")
                .antMatchers("/accounts/password").hasAuthority("ROLE_USER")
                // 유저 기타
                .antMatchers("/search", "/alarms").hasAuthority("ROLE_USER")

                // 팔로우 & 차단
                .antMatchers("/**/follow", "/**/followers", "/**/following").hasAuthority("ROLE_USER")
                .antMatchers("/**/block").hasAuthority("ROLE_USER")

                // DM
                .antMatchers("/chat/rooms/**").hasAuthority("ROLE_USER");

    }

}