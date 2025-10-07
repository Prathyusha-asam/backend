package com.demo.dms.repository;

import com.demo.dms.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Integer> {
    Optional<UserAccount> findByEmailIgnoreCase(String email);

    Optional<UserAccount> findByEmail(String mail);
}
