package com.BackEndTeam1.service.mongo;

import com.BackEndTeam1.document.UserLoginDocument;
import com.BackEndTeam1.entity.TeamSpace;
import com.BackEndTeam1.entity.TeamSpaceMember;
import com.BackEndTeam1.repository.mongo.UserLoginRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
public class UserLoginService {
    private final UserLoginRepository userLoginRepository;
    private final ModelMapper modelMapper;

    public void updateUserStatus(String userId, String currentStatus, List<String> roomNames,List<Long>teamId, String userprofile, String username) {
        UserLoginDocument status = userLoginRepository.findByUserId(userId)
                .orElse(UserLoginDocument.builder().userId(userId).build());
        log.info("status : "+status);
        status.setCurrentStatus(currentStatus);
        status.setLastUpdated(LocalDateTime.now());
        status.setProfileimg(userprofile);
        userLoginRepository.save(status);
    }

    public void changeUserStatus(String userId, String currentStatus) {
        UserLoginDocument status = userLoginRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("UserLoginDocument not found for userId: " + userId));
        status.setCurrentStatus(currentStatus);
        status.setLastUpdated(LocalDateTime.now());
        userLoginRepository.save(status);
    }
}
