package com.lotte2backteam1.security;


import com.lotte2backteam1.entity.User;
import com.lotte2backteam1.repository.UserRepository;
import lombok.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Builder
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optUser = userRepository.findById(username);

        if (optUser.isPresent()) {
            MyUserDetails myUserDetails =  MyUserDetails.builder()
                    .user(optUser.get())
                    .build();
            return myUserDetails;
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username); // 예외 던지기

        }
    }

}
