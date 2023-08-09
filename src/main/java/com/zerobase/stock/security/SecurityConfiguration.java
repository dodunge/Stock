package com.zerobase.stock.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter authenticationFilter;

    // 실제 api에 관한 경로들에 대한 권한 관리
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 아래 세줄은 restController로 jwt를 구현해 주고 싶을 때 무조건 작성해줘야 하는 부분
                .httpBasic().disable()
                .csrf().disable()
                // jwt는 상태 정보를 저장하지 않기 때문에 stateless정책을 따르게 됨
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // 이 밑으로는 실질적으로 권한 제어가 들어간 부분
                .and()
                    .authorizeRequests()
                        .antMatchers("/**/signup", "/**/signin").permitAll()
                .and()
                .addFilterBefore(this.authenticationFilter, UsernamePasswordAuthenticationFilter.class);

    }

    // 개발관련된 경로들만 이 아래에서 관리하고 싶어서
    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/h2-console/**");
    }

    // spring boot 2.x
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
