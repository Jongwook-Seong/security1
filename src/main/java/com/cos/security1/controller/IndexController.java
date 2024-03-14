package com.cos.security1.controller;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller //View를 리턴하겠다
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 세션 내 스프링 시큐리티가 관리하는 시큐리티 세션이 있다.
     * 시큐리티 세션에는 Authentication 객체가 들어간다. (DI 가능)
     * Authentication 객체에 UserDetails 타입이 들어가면 : 일반 로그인
     */
    @GetMapping("/test/login")
    public @ResponseBody String testLogin(
            Authentication authentication,
            @AuthenticationPrincipal PrincipalDetails userDetails) { //DI(의존성 주입)
        System.out.println("/test/login =============");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("authentication : " + principalDetails.getUser());
        System.out.println("userDetails : " + userDetails.getUser());
        return "세션 정보 확인하기";
    }

    /**
     * 세션 내 스프링 시큐리티가 관리하는 시큐리티 세션이 있다.
     * 시큐리티 세션에는 Authentication 객체가 들어간다. (DI 가능)
     * Authentication 객체에 OAuth2User 타입이 들어가면 : OAuth 로그인
     */
    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOAuthLogin(
            Authentication authentication,
            @AuthenticationPrincipal OAuth2User oauth) { //DI(의존성 주입)
        System.out.println("/test/oauth/login =============");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("authentication : " + oAuth2User.getAttributes());
        System.out.println("oauth2User : " + oauth.getAttributes());
        return "OAuth 세션 정보 확인하기";
    }

    //localhost:8080/
    //localhost:8080
    @GetMapping({"", "/"})
    public String index() {
        // 머스테치 기본폴더 : src/main/resources/
        // 뷰리졸버 설정 : templates (prefix), .mustache (suffix) 생략 가능
        return "index"; // src/main/resources/templates/index.mustache
    }

    // 일반 로그인을 해도 PrincipalDetails
    // OAuth 로그인을 해도 PrincipalDetails
    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        System.out.println("principalDetails = " + principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager() {
        return "manager";
    }

    // 스프링시큐리티가 해당주소를 낚아챔
    @GetMapping("/loginForm") // SecurityConfig 생성 후 작동 안 함.
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user) {
        System.out.println(user);
        user.setRole("ROLE_USER");
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        userRepository.save(user);
        return "redirect:/loginForm";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/info")
    public @ResponseBody String info() {
        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/data")
    public @ResponseBody String data() {
        return "데이터정보";
    }
}
