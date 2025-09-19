package com.demo.dms.web.dto;

import com.demo.dms.entity.UserAccount;
import com.demo.dms.repository.UserAccountRepository;
import com.demo.dms.service.UserService;

import java.util.Optional;

public record TokenPairLoginResponse(String accessToken, String refreshToken,
                                     String tokenType, long expiresInMs, Optional<UserAccount> userAccountRepository) {

    public static TokenPairLoginResponse bearer(String at, String rt, long expMs, Optional<UserAccount> userAccountRepository) {
        return new TokenPairLoginResponse(at, rt, "Bearer", expMs, userAccountRepository);
    }

}
