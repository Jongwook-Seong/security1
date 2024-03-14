package com.cos.security1.config;

import com.cos.security1.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록됩니다.
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true) // @Secured 활성화, @PreAuthorize, @PostAuthorize 활성화
public class SecurityConfig {

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    // 해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다.
//    @Bean
//    public BCryptPasswordEncoder encodePwd() {
//        return new BCryptPasswordEncoder();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(CsrfConfigurer::disable);
//        http.sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//        http.formLogin((form) -> form.disable());
//        http.httpBasic((basic) -> basic.disable());
        http.authorizeHttpRequests(authorize ->
                authorize
                        .requestMatchers("/user/**").authenticated()
                        .requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                        .anyRequest().permitAll()
        );
        http.formLogin(form -> form
                .loginPage("/loginForm")
                .loginProcessingUrl("/login") // /login 주소가 호출되면 시큐리티가 낚아채서 대신 로그인을 진행한다.
                .defaultSuccessUrl("/"));
        http.oauth2Login(form -> form
                .loginPage("/loginForm")
                .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                        .userService(principalOauth2UserService)));
        /**
         * 구글 로그인이 완료된 이후 후처리(principalOauth2UserService.loadUser())가 필요
         * 1. 코드 받기(인증)
         * 2. 액세스토큰(권한)
         * 3. 사용자 프로필 정보 가져오기
         * 4-1. 그 정보를 토대로 회원가입을 자동 진행시키기도 함
         * 4-2. 추가 정보가 필요하다면 추가적인 회원가입 절차가 필요
         * 여기서 후처리에서는 코드를 받는 게 아니라(코드는 이미 받았다),
         * 코드를 통해 액세스토큰을 받고, 액세스토큰으로 사용자 프로필 정보까지 받은 정보가 userRequest에 리턴
         */
        return http.build();
    }
}
