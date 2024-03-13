package com.cos.security1.repository;

import com.cos.security1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository가 CRUD 함수를 들고 있다.
// JpaRepository를 상속받았기 때문에 @Repository 어노테이션이 없어도 IoC 된다.
public interface UserRepository extends JpaRepository<User, Integer> {
}
