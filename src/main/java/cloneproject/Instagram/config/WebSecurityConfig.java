package cloneproject.Instagram.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
    
    @Override
    public void configure(WebSecurity web) throws Exception{
        web.ignoring().antMatchers("/css/**", "/js/**"); // 나중에 수정
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/**").permitAll();
            
        http.formLogin()
            .loginPage("/login")
            .defaultSuccessUrl("/")
            .permitAll();
    }

}
