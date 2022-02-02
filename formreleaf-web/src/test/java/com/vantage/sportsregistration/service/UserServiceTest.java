package com.vantage.sportsregistration.service;

import com.vantage.sportsregistration.Application;
import com.formreleaf.domain.User;
import com.formreleaf.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void findByEmailTest() {

        User user2 = userRepository.findByEmailIgnoreCaseAndEnabledTrue("application@livingoncodes.com");

        System.out.println(user2.getEmail());
        System.out.println(userRepository.count());


    }
}
