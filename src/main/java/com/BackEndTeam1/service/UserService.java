package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.UserDTO;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserDTO saveUser(UserDTO userDTO){
        User user = modelMapper.map(userDTO, User.class);
        //패스워드 암호화 할때 config(RootConfig)에 직접 매핑해줘야함
        user.setPass(passwordEncoder.encode(user.getPass()));
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDTO.class);
    }

    public UserDTO updateUser(UserDTO userDTO){
        User user = modelMapper.map(userDTO, User.class);
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDTO.class);
    }

    public UserDTO findByUserId(String userId) {
        Optional<User> user = userRepository.findByUserId(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        return modelMapper.map(user.get(), UserDTO.class);
    }


    public boolean isUserIdAvailable(String userId) {
        Optional<User> existingUser = userRepository.findByUserId(userId);
        log.info("Found user: " + existingUser.isPresent()); // 로그 추가
        return !existingUser.isPresent();
    }

    public boolean isPhoneNumberExists(String phoneNumber) {
        return userRepository.existsByHp(phoneNumber);
    }

    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}