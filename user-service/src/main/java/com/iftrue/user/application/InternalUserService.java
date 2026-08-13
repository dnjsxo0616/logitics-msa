package com.iftrue.user.application;

import com.iftrue.user.domain.User;
import com.iftrue.user.domain.UserRepository;
import com.iftrue.user.presentation.response.InternalUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InternalUserService {

    private final UserRepository userRepository;

    public InternalUserResponse getUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return new InternalUserResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                user.getSlackId(),
                user.getRole(),
                user.getHubId(),
                user.getCompanyId(),
                user.getStatus()
        );
    }
}