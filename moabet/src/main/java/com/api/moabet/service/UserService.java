package com.api.moabet.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.api.moabet.models.User;
import com.api.moabet.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

}
