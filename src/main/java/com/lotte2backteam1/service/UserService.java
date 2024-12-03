package com.lotte2backteam1.service;

import com.lotte2backteam1.dto.UserDTO;
import com.lotte2backteam1.entity.User;
import com.lotte2backteam1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserDTO save(UserDTO userDTO) {
        String encodedPass = passwordEncoder.encode(userDTO.getPass());
        userDTO.setPass(encodedPass);

        User user = modelMapper.map(userDTO, User.class);
        // role 값이 null이면 기본값을 설정
        if (user.getRole() == null) {
            user.setRole("USER");
        }

        User savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, UserDTO.class);
    }
}
