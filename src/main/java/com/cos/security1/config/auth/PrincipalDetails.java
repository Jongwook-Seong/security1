package com.cos.security1.config.auth;

import com.cos.security1.model.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

// /login 주소 요청이 오면 시큐리티가 낚아채서 로그인을 진행시킨다.
// 로그인 진행이 완료되면 시큐리티 session을 만들어준다. (Security ContextHolder)
// 오브젝트 => Authentication 타입 객체
// Authentication 안에 User 정보가 있어야 한다.
// User 오브젝트 타입 => UserDetails 타입 객체
// Security Session => Authentication => UserDetails(PrincipalDetails)

@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

    private User user; //컴포지션
    private Map<String, Object> attributes;

    // 일반 로그인을 할 때 사용하는 생성자
    public PrincipalDetails(User user) {
        this.user = user;
    }

    // OAuth 로그인을 할 때 사용하는 생성자
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {

        //1년동안 회원이 로그인을 안하면 휴면계정으로 하기로 함
        //현재시간 - 로그인시간 => 1년 초과하면 return false
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public String getName() {
//        return attributes.get("sub");
        //구글 계정 Primary Key 값인데, 중요한 정보도 아니고 사용하지도 않으므로 null 리턴
        return null;
    }
}
