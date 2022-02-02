package com.formreleaf.repository;


import com.formreleaf.domain.PasswordResetToken;
import com.formreleaf.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);

    PasswordResetToken findByUser(User user);

}
