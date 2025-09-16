package com.demo.dms.service;

import com.demo.dms.entity.UserAccount;
import com.demo.dms.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserAccountRepository userRepository;

    @Autowired
    public UserService(UserAccountRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<UserAccount> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Optional<UserAccount> getUsersByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }
}
