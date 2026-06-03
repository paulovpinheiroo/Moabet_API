package com.api.moabet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.moabet.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
