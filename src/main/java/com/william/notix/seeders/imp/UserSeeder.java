package com.william.notix.seeders.imp;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.william.notix.entities.User;
import com.william.notix.seeders.Seeder;
import com.william.notix.services.UserService;

import lombok.RequiredArgsConstructor;

@Order(1)
@Component
@RequiredArgsConstructor
public class UserSeeder implements Seeder{

    private final UserService userService;

    @Override
    public void run() throws Exception {
        userService.registerUser(
            new User()
                .setEmail("william@email.com")
                .setFullName("William Kurnia Mulyadi Putra")
                .setPassword("password")
        );

        userService.registerUser(
            new User()
                .setEmail("aisyah@email.com")
                .setFullName("Aisyah Putri Ramadhania")
                .setPassword("password")
        );

        userService.registerUser(
            new User()
                .setEmail("andre@email.com")
                .setFullName("Andre Wijaya")
                .setPassword("password")
        );
    }
    
}
