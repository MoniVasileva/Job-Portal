package com.vasmoni.jobportal.repository;

import com.vasmoni.jobportal.entity.PasswordResetToken;
import com.vasmoni.jobportal.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.stream.Stream;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);

    PasswordResetToken findByUser(Users user);

    Stream<PasswordResetToken> findAllByExpiryDateLessThan(Date now);

    void deleteByExpiryDateLessThan(Date now);

    @Modifying
    @Query("delete from PasswordResetToken t where t.expiryDate <= ?1")
    void deleteAllExpiredSince(Date now);
}