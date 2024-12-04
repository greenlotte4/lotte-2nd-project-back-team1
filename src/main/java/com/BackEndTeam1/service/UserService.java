package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.PageRequestDTO;
import com.BackEndTeam1.dto.PageResponseDTO;
import com.BackEndTeam1.dto.UserDTO;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
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
        log.info("user.get().getCreatedAt()" + user.get().getCreatedAt());
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



    public User getLoggedInUser() {
        // Spring Security에서 현재 인증된 사용자 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }

        String username = authentication.getName(); // 인증된 유저의 username(email, id 등)
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    public void updateLastLoginTime(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            userRepository.save(user);
        }
    }
    
    //사용자 정보를 리스트 형식 + 페이지 처리 해서 출력
    public PageResponseDTO<UserDTO> userlist(PageRequestDTO pageRequestDTO) {
        log.info("pageRequestDTO : " + pageRequestDTO);
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPg() -1,
                pageRequestDTO.getSize(),
                Sort.by("userId").descending());
        log.info("pageable : " + pageable);
        Page<User> userPage = userRepository.findAll(pageable);
        List<UserDTO> userDTOList = userPage.getContent().stream()
                .map(entity -> modelMapper.map(entity, UserDTO.class))
                .toList();
        int total = (int) userPage.getTotalElements();
        PageResponseDTO<UserDTO> responseDTO = PageResponseDTO.<UserDTO>builder()
                .dtoList(userDTOList)
                .pageRequestDTO(pageRequestDTO)
                .total(total)
                .build();
        return responseDTO;

    }
}